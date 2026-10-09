$ErrorActionPreference = 'Stop'
$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
Set-Location $projectRoot
docker compose -f deployment/docker-compose.yml restart
if ($LASTEXITCODE -ne 0) { throw 'Docker Compose restart failed.' }
docker compose -f deployment/docker-compose.yml ps
try {
    Invoke-RestMethod 'http://localhost:8080/actuator/health' -TimeoutSec 10 | ConvertTo-Json -Compress
} catch {
    throw "Portal health check failed: $($_.Exception.Message)"
}

