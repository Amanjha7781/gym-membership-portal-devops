$ErrorActionPreference = 'Stop'
$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
Set-Location $projectRoot
docker compose -f deployment/docker-compose.yml down
if ($LASTEXITCODE -ne 0) { throw 'Docker Compose stop failed.' }
Write-Host 'Containers stopped. The MySQL named volume was preserved.'

