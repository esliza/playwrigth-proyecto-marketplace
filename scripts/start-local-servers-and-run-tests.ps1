param(
    [string]$FrontendPath = "C:\Users\Estefany Liza\Documents\Pagina Web\condominio-marketplace\frontend\vite-project",
    [string]$BackendPath = "C:\Users\Estefany Liza\Documents\Pagina Web\condominio-marketplace\backend",
    [int]$FrontendPort = 5130,
    [int]$BackendPort = 8080,
    [int]$TimeoutSec = 180
)

function Wait-ForPort {
    param($Port, $Timeout)
    $start = Get-Date
    while (((Get-Date) - $start).TotalSeconds -lt $Timeout) {
        try {
            $r = Test-NetConnection -ComputerName 'localhost' -Port $Port -WarningAction SilentlyContinue
            if ($r -and $r.TcpTestSucceeded) {
                Write-Host "Port $Port is open."
                return $true
            }
        } catch {
        }
        Start-Sleep -Seconds 2
    }
    return $false
}

Write-Host "Starting services with frontend:$FrontendPath backend:$BackendPath"

# Start backend
if (Test-Path $BackendPath) {
    Write-Host "Starting backend from $BackendPath"
    Push-Location $BackendPath
    if (Test-Path "package.json") {
        Start-Process npm -ArgumentList 'run','start' -NoNewWindow -WorkingDirectory $BackendPath
    } elseif (Test-Path "pom.xml") {
        Start-Process mvn -ArgumentList 'spring-boot:run' -NoNewWindow -WorkingDirectory $BackendPath
    } else {
        Write-Host "No recognized start script in backend path ($BackendPath). Skipping backend start."
    }
    Pop-Location
} else {
    Write-Host "Backend path $BackendPath not found. Skipping backend start."
}

# Start frontend
if (Test-Path $FrontendPath) {
    Write-Host "Starting frontend from $FrontendPath"
    Push-Location $FrontendPath
    if (Test-Path "package.json") {
        Start-Process npm -ArgumentList 'run','dev' -NoNewWindow -WorkingDirectory $FrontendPath
    } else {
        Write-Host "No package.json found in frontend path ($FrontendPath). Skipping frontend start."
    }
    Pop-Location
} else {
    Write-Host "Frontend path $FrontendPath not found. Skipping frontend start."
}

Write-Host "Waiting for backend port $BackendPort and frontend port $FrontendPort to be available (timeout: ${TimeoutSec}s)..."
 $bReady = Wait-ForPort -Port $BackendPort -Timeout $TimeoutSec
 $fReady = Wait-ForPort -Port $FrontendPort -Timeout $TimeoutSec

if (-not $bReady) { Write-Warning "Backend did not become ready on port $BackendPort within timeout." }
if (-not $fReady) { Write-Warning "Frontend did not become ready on port $FrontendPort within timeout." }

if ($bReady -or $fReady) {
    $baseUrl = "http://localhost:$FrontendPort"
    Write-Host "Running tests against $baseUrl"
    # Run maven tests in repo root
    & mvn -DbaseUrl=$baseUrl -Dplaywright.headless=true -Dplaywright.enableRecording=false test
    exit $LASTEXITCODE
} else {
    Write-Error "Neither backend nor frontend became ready. Aborting test run."
    exit 2
}
