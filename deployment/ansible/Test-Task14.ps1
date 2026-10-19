[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
$evidenceRoot = Join-Path $PSScriptRoot 'task14-evidence'
$logDirectory = Join-Path $evidenceRoot 'logs'
New-Item -ItemType Directory -Force -Path $logDirectory | Out-Null
$runId = Get-Date -Format 'yyyyMMdd-HHmmss'
$logPath = Join-Path $logDirectory "task14-run-$runId.log"

$composeBase = @('--env-file', '.env', '-f', 'deployment/docker-compose.yml')
$composeFailure = @('--env-file', '.env', '-f', 'deployment/docker-compose.yml', '-f', 'deployment/ansible/task14-evidence/failure-candidate.compose.yml')
$knownGoodTag = 'gym-membership-portal:task14-last-known-good'
$appImageTag = 'gym-membership-portal:1.0.0'
$failureImageTag = 'gym-membership-portal:task14-failed-candidate'
$appStoppedForRecovery = $false
$appRecoveryVerified = $false
$candidateDeploymentStarted = $false
$rollbackVerified = $false
$scriptFailure = $null
$safetyRecoveryFailure = $null
$mysqlContainerIdBefore = ''
$knownGoodImageId = ''

function Write-Evidence {
    param([Parameter(Mandatory)][string]$Message)
    $line = '[{0}] {1}' -f (Get-Date -Format 'yyyy-MM-dd HH:mm:ss'), $Message
    Add-Content -LiteralPath $logPath -Value $line -Encoding utf8
    Write-Host $line
}

function Invoke-LoggedDocker {
    param(
        [Parameter(Mandatory)][string]$Label,
        [Parameter(Mandatory)][string[]]$Arguments,
        [switch]$AllowFailure
    )

    Write-Evidence "COMMAND: docker $($Arguments -join ' ')"
    $output = & docker @Arguments 2>&1
    $exitCode = $LASTEXITCODE
    $lines = @($output | ForEach-Object { [string]$_ })
    foreach ($line in $lines) {
        Add-Content -LiteralPath $logPath -Value $line -Encoding utf8
        Write-Host $line
    }
    Write-Evidence "RESULT: $Label (exit=$exitCode)"
    if ($exitCode -ne 0 -and -not $AllowFailure) {
        throw "Docker command failed: $Label (exit=$exitCode)."
    }
    return ($lines -join "`n").Trim()
}

function Invoke-AnsiblePlaybook {
    param([Parameter(Mandatory)][string]$Playbook)

    $projectMount = "type=bind,source=$repoRoot,target=/workspace"
    $socketMount = 'type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock'
    $arguments = @(
        'run', '--rm', '--mount', $projectMount, '--mount', $socketMount,
        '--workdir', '/workspace/deployment/ansible', '--env', 'ANSIBLE_NOCOLOR=1',
        'gym-portal-ansible:local', '-i', 'inventory.ini', $Playbook
    )
    return Invoke-LoggedDocker -Label "Ansible $Playbook" -Arguments $arguments
}

function Get-ComposeServiceId {
    param([Parameter(Mandatory)][ValidateSet('mysql', 'gym-portal')][string]$Service)
    $arguments = @('compose') + $composeBase + @('ps', '-q', $Service)
    return (Invoke-LoggedDocker -Label "Get $Service container ID" -Arguments $arguments).Trim()
}

function Wait-ForPortalHealth {
    param([int]$Attempts = 30)
    for ($attempt = 1; $attempt -le $Attempts; $attempt++) {
        try {
            # Invoke-RestMethod parses the health JSON as an object and throws
            # for non-2xx responses, avoiding PowerShell's byte-array content issue.
            $body = Invoke-RestMethod -Uri 'http://localhost:8080/actuator/health' -TimeoutSec 5
            if ($body.status -eq 'UP') {
                Write-Evidence "HEALTH: HTTP 200, status=$($body.status), attempt=$attempt"
                return
            }
            Write-Evidence "HEALTH: received non-ready response on attempt $attempt"
        } catch {
            Write-Evidence "HEALTH: endpoint not ready on attempt $attempt"
        }
        Start-Sleep -Seconds 2
    }
    throw "Portal health endpoint did not return HTTP 200 / UP after $Attempts attempts."
}

Push-Location $repoRoot
try {
    Write-Evidence 'Task 14 controlled provisioning, recovery, and rollback validation started.'
    if (-not (Test-Path -LiteralPath '.env' -PathType Leaf)) {
        throw 'Required local .env file is missing; no service action was attempted.'
    }

    $daemonVersion = Invoke-LoggedDocker -Label 'Docker daemon availability' -Arguments @('info', '--format', '{{.ServerVersion}}')
    Write-Evidence "Docker daemon server version: $daemonVersion"

    if ((Invoke-LoggedDocker -Label 'Build Ansible runner if absent' -Arguments @('image', 'inspect', '--format', '{{.Id}}', 'gym-portal-ansible:local') -AllowFailure) -eq '') {
        Invoke-LoggedDocker -Label 'Build Ansible runner' -Arguments @('build', '-t', 'gym-portal-ansible:local', 'deployment/ansible') | Out-Null
    }

    $projectMount = "type=bind,source=$repoRoot,target=/workspace"
    foreach ($playbook in @('provision.yml', 'snapshot.yml', 'rollback.yml')) {
        Invoke-LoggedDocker -Label "Syntax-check $playbook" -Arguments @(
            'run', '--rm', '--mount', $projectMount, '--workdir', '/workspace/deployment/ansible',
            'gym-portal-ansible:local', '--syntax-check', '-i', 'inventory.ini', $playbook
        ) | Out-Null
    }
    Invoke-LoggedDocker -Label 'Parse local Ansible inventory' -Arguments @(
        'run', '--rm', '--entrypoint', 'ansible-inventory', '--mount', $projectMount,
        '--workdir', '/workspace/deployment/ansible', 'gym-portal-ansible:local', '--list', '-i', 'inventory.ini'
    ) | Out-Null

    Invoke-LoggedDocker -Label 'Compose configuration preflight' -Arguments (@('compose') + $composeBase + @('config', '--quiet')) | Out-Null
    Invoke-AnsiblePlaybook 'provision.yml' | Out-Null
    Wait-ForPortalHealth

    $mysqlContainerIdBefore = Get-ComposeServiceId 'mysql'
    if (-not $mysqlContainerIdBefore) { throw 'Compose did not return the MySQL container ID.' }

    # Preserve the actual running image before any failure simulation.
    Invoke-AnsiblePlaybook 'snapshot.yml' | Out-Null
    $knownGoodImageId = Invoke-LoggedDocker -Label 'Read saved known-good image ID' -Arguments @('image', 'inspect', '--format', '{{.Id}}', $knownGoodTag)
    $activeImageId = Invoke-LoggedDocker -Label 'Read configured application image ID' -Arguments @('image', 'inspect', '--format', '{{.Id}}', $appImageTag)
    if ($knownGoodImageId -ne $activeImageId) { throw 'Snapshot image does not match the healthy configured application image.' }

    # Failure test 1: stop and restart only the app container.
    $appStoppedForRecovery = $true
    Invoke-LoggedDocker -Label 'Stop only the Gym Portal app service for recovery test' -Arguments (@('compose') + $composeBase + @('stop', 'gym-portal')) | Out-Null
    $mysqlAfterAppStop = Get-ComposeServiceId 'mysql'
    if ($mysqlAfterAppStop -ne $mysqlContainerIdBefore) { throw 'MySQL container identity changed while stopping only the app.' }

    Invoke-AnsiblePlaybook 'provision.yml' | Out-Null
    Wait-ForPortalHealth
    $mysqlAfterProvision = Get-ComposeServiceId 'mysql'
    if ($mysqlAfterProvision -ne $mysqlContainerIdBefore) { throw 'MySQL container identity changed during app recovery.' }
    $appRecoveryVerified = $true
    Write-Evidence 'RECOVERY TEST PASSED: stopped app service restarted healthy; MySQL container identity was unchanged.'

    # Refresh the rollback snapshot from the now-healthy deployed app.
    Invoke-AnsiblePlaybook 'snapshot.yml' | Out-Null
    $knownGoodImageId = Invoke-LoggedDocker -Label 'Confirm refreshed known-good image ID' -Arguments @('image', 'inspect', '--format', '{{.Id}}', $knownGoodTag)

    # Build a test-only image that stays running but cannot serve the app health endpoint.
    Invoke-LoggedDocker -Label 'Build intentionally unhealthy Task 14 test candidate' -Arguments @(
        'build', '-f', 'deployment/ansible/task14-evidence/failure-candidate.Dockerfile',
        '-t', $failureImageTag, 'deployment/ansible/task14-evidence'
    ) | Out-Null
    Invoke-LoggedDocker -Label 'Validate quick failure-test Compose override' -Arguments (@('compose') + $composeFailure + @('config', '--quiet')) | Out-Null

    # Replace only the local stable tag; its original image remains pinned by the known-good tag.
    Invoke-LoggedDocker -Label 'Temporarily point app tag at failure-test candidate' -Arguments @('image', 'tag', $failureImageTag, $appImageTag) | Out-Null
    $candidateDeploymentStarted = $true
    Invoke-LoggedDocker -Label 'Deploy intentionally unhealthy candidate to app service only' -Arguments (@('compose') + $composeFailure + @('up', '-d', '--no-build', '--no-deps', 'gym-portal')) | Out-Null

    $candidateContainerId = Invoke-LoggedDocker -Label 'Get failure-test app container ID' -Arguments (@('compose') + $composeFailure + @('ps', '-q', 'gym-portal'))
    $candidateHealth = 'starting'
    for ($attempt = 1; $attempt -le 20; $attempt++) {
        $candidateHealth = Invoke-LoggedDocker -Label "Failure-test candidate health state $attempt" -Arguments @('inspect', '--format', '{{.State.Health.Status}}', $candidateContainerId)
        if ($candidateHealth -eq 'unhealthy') { break }
        Start-Sleep -Seconds 2
    }
    if ($candidateHealth -ne 'unhealthy') { throw "Failure-test candidate did not reach unhealthy state (state=$candidateHealth)." }
    Write-Evidence 'ROLLBACK TEST SETUP PASSED: candidate app image reached unhealthy state; MySQL was not targeted.'

    # The rollback play uses only the base Compose file and restores the known-good image/tag.
    Invoke-AnsiblePlaybook 'rollback.yml' | Out-Null
    Wait-ForPortalHealth
    $mysqlAfterRollback = Get-ComposeServiceId 'mysql'
    $restoredAppId = Get-ComposeServiceId 'gym-portal'
    $restoredImageId = Invoke-LoggedDocker -Label 'Read restored app image ID' -Arguments @('inspect', '--format', '{{.Image}}', $restoredAppId)
    if ($mysqlAfterRollback -ne $mysqlContainerIdBefore) { throw 'MySQL container identity changed during rollback.' }
    if ($restoredImageId -ne $knownGoodImageId) { throw 'Rollback did not restore the saved known-good application image.' }
    $rollbackVerified = $true
    $candidateDeploymentStarted = $false
    Write-Evidence 'ROLLBACK TEST PASSED: known-good app image restored; app health is UP; MySQL identity is unchanged.'

    $rollbackRepeatOutput = Invoke-AnsiblePlaybook 'rollback.yml'
    if ($rollbackRepeatOutput -notmatch 'changed=0\s+unreachable=0\s+failed=0') {
        throw 'Repeated rollback did not report changed=0 and failed=0.'
    }
    Write-Evidence 'ROLLBACK IDEMPOTENCE CHECK PASSED: repeated rollback reported changed=0 and failed=0.'

    # One final healthy provisioning pass verifies idempotence after recovery.
    $provisionOutput = Invoke-AnsiblePlaybook 'provision.yml'
    if ($provisionOutput -notmatch 'changed=0\s+unreachable=0\s+failed=0') {
        throw 'Final healthy provisioning run did not report changed=0 and failed=0.'
    }
    Wait-ForPortalHealth
    Write-Evidence 'IDEMPOTENCE CHECK PASSED: repeated healthy provisioning reported changed=0 and failed=0.'
    Write-Evidence 'TASK 14 VALIDATION PASSED.'
} catch {
    $scriptFailure = $_.Exception.Message
    Write-Evidence "VALIDATION ERROR: $scriptFailure"
} finally {
    # Safety recovery runs even if a check fails after the controlled outage begins.
    if ($candidateDeploymentStarted -and -not $rollbackVerified -and $knownGoodImageId) {
        try {
            Write-Evidence 'SAFETY RECOVERY: attempting Ansible rollback before script exit.'
            Invoke-AnsiblePlaybook 'rollback.yml' | Out-Null
            Wait-ForPortalHealth
            $rollbackVerified = $true
            Write-Evidence 'SAFETY RECOVERY PASSED: known-good rollback restored the portal.'
        } catch {
            Write-Evidence 'Ansible safety rollback failed; trying direct image-tag and app-only Compose recovery.'
            try {
                Invoke-LoggedDocker -Label 'Direct safety restore of known-good app tag' -Arguments @('image', 'tag', $knownGoodTag, $appImageTag) | Out-Null
                Invoke-LoggedDocker -Label 'Direct safety restart of app only' -Arguments (@('compose') + $composeBase + @('up', '-d', '--no-build', '--no-deps', 'gym-portal')) | Out-Null
                Wait-ForPortalHealth
                Write-Evidence 'DIRECT SAFETY RECOVERY PASSED.'
            } catch {
                $safetyRecoveryFailure = $_.Exception.Message
                Write-Evidence "DIRECT SAFETY RECOVERY FAILED: $safetyRecoveryFailure"
            }
        }
    } elseif ($appStoppedForRecovery -and -not $appRecoveryVerified) {
        try {
            Write-Evidence 'SAFETY RECOVERY: provisioning app service after an interrupted recovery test.'
            Invoke-AnsiblePlaybook 'provision.yml' | Out-Null
            Wait-ForPortalHealth
            Write-Evidence 'APP SERVICE SAFETY RECOVERY PASSED.'
        } catch {
            $safetyRecoveryFailure = $_.Exception.Message
            Write-Evidence "APP SERVICE SAFETY RECOVERY FAILED: $safetyRecoveryFailure"
        }
    }
    Pop-Location
}

if ($safetyRecoveryFailure) {
    throw "Task 14 validation did not finish and automatic safety recovery also failed: $safetyRecoveryFailure. Evidence: $logPath"
}
if ($scriptFailure) {
    if (-not $appStoppedForRecovery -and -not $candidateDeploymentStarted) {
        throw "Task 14 validation failed before controlled recovery actions; the running services were left untouched: $scriptFailure Evidence: $logPath"
    }
    throw "Task 14 validation failed: $scriptFailure. Any controlled outage was followed by a safety recovery attempt. Evidence: $logPath"
}

Write-Host "Task 14 evidence log: $logPath"
