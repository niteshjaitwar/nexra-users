$ErrorActionPreference = "Stop"

function Invoke-ApiRequest {
    param (
        [string]$Method,
        [string]$Url,
        [string]$Body = $null,
        [string]$Token = $null
    )
    
    $Headers = @{}
    if ($Token) {
        $Headers["Authorization"] = "Bearer $Token"
    }
    if ($Body) {
        $Headers["Content-Type"] = "application/json"
    }
    
    $params = @{
        Method = $Method
        Uri = $Url
        Headers = $Headers
    }
    if ($Body) {
        $params.Body = $Body
    }
    
    return Invoke-RestMethod @params
}

$baseUrl = "http://localhost:8089"
$timestamp = Get-Date -Format "HHmmss"
$username = "ent_user_$timestamp"
$email = "nexra.team+$timestamp@gmail.com"
$username | Out-File "last_test_user.txt" -Force
$email | Out-File "last_test_email.txt" -Force # Using the configured email to ensure logic holds
# Note: For real test using different emails might be better but for now re-using.

Write-Host "=== Starting Enterprise E2E Test ===" -ForegroundColor Cyan

# 1. Register
Write-Host "1. Registering..."
$registerBody = @"
{
    "username": "$username",
    "email": "$email",
    "password": "Password@123",
    "roles": []
}
"@
Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/auth/register" -Body $registerBody | Out-Null
Write-Host "Registered. Waiting for Async OTP..."

Start-Sleep -Seconds 5

# 2. Extract OTP from Logs (Simulated "Check Email")
Write-Host "2. Checking Logs for OTP..."
# This relies on being able to read the log file or console output. 
# Since we are running the app in background, we might check a log file? 
# Or since we are the agent, we can inspect terminal output of the run_command?
# Actually run_command output is not easily accessible to this script.
# We will cheat: assume we can't get OTP automatically in this script without complex log scraping.
# BUT, I made OtpService log it. 
# The agent (me) will check the run_command output.
# This script will pause for input or fail if I don't provide a mechanism.
# Let's try to verify Login fails first.

# 3. Try Login (Should Fail/Disabled)
Write-Host "3. Attempting Login (Should fail/disabled)..."
try {
    $loginBody = @"
{
    "username": "$username",
    "password": "Password@123"
}
"@
    Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/auth/login" -Body $loginBody
    Write-Host "ERROR: Login succeeded but should have failed!" -ForegroundColor Red
} catch {
    Write-Host "Login failed as expected (User Disabled/Locked)" -ForegroundColor Green
}

Write-Host "=== Test Paused: Verification requires OTP ==="
# I cannot automate the OTP injection easily without a shared file/socket.
# I will stop the script here and perform the verification manually using the OTP I see in the logs.
