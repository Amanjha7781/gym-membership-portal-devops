$ErrorActionPreference = 'Stop'
$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
Set-Location $projectRoot
docker compose -f deployment/docker-compose.yml ps
if ($LASTEXITCODE -ne 0) { throw 'Docker Compose status check failed.' }

