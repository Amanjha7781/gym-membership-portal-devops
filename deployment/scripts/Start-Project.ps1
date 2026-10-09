$ErrorActionPreference = 'Stop'
$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
Set-Location $projectRoot

if (-not (Test-Path '.env')) {
    throw 'Create .env from .env.example and replace the password placeholders first.'
}

mvn -B clean verify
if ($LASTEXITCODE -ne 0) { throw 'Maven verification failed; containers were not started.' }

docker compose -f deployment/docker-compose.yml up -d --build
if ($LASTEXITCODE -ne 0) { throw 'Docker Compose could not start the project.' }

docker compose -f deployment/docker-compose.yml ps
Write-Host 'Open http://localhost:8080 after both services report healthy.'

