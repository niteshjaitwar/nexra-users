param (
    [Parameter(Mandatory=$true)]
    [string]$Otp
)

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
$username = "ent_user_latest"
$email = "nexra.team@gmail.com"

if (Test-Path "last_test_user.txt") {
    $username = Get-Content "last_test_user.txt"
}
if (Test-Path "last_test_email.txt") {
    $email = Get-Content "last_test_email.txt"
}

Write-Host "=== Starting E2E Test Part 2 (User: $username, OTP: $Otp) ===" -ForegroundColor Cyan

# 1. Verify Email
Write-Host "`n1. Verifying Email..."
$verifyBody = @"
{
    "email": "$email",
    "otp": "$Otp"
}
"@
try {
    $verifyResponse = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/auth/verify-email" -Body $verifyBody
    Write-Host "Success! Email verified." -ForegroundColor Green
} catch {
    Write-Host "Verification failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. Login
Write-Host "`n2. Logging In..."
$loginBody = @"
{
    "username": "$username",
    "password": "Password@123"
}
"@
$loginResponse = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/auth/login" -Body $loginBody
$accessToken = $loginResponse.access_token
$refreshToken = $loginResponse.refresh_token

if ($accessToken) {
    Write-Host "Success! Logged in." -ForegroundColor Green
} else {
    Write-Host "Login Failed!" -ForegroundColor Red
    exit 1
}

# 3. Refresh Token
Write-Host "`n3. Refreshing Token..."
$refreshBody = @"
{
    "refreshToken": "$refreshToken"
}
"@
$refreshResponse = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/auth/refresh-token" -Body $refreshBody
$newAccessToken = $refreshResponse.access_token

if ($newAccessToken) {
    Write-Host "Success! Token Refreshed." -ForegroundColor Green
} else {
    Write-Host "Token Refresh Failed!" -ForegroundColor Red
    exit 1
}

# 4. Logout
Write-Host "`n4. Logging Out..."
Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/auth/logout" -Token $newAccessToken | Out-Null
Write-Host "Success! Logged Out." -ForegroundColor Green

# 5. Verify Blacklist (Try to access secured endpoint with old token)
Write-Host "`n5. Verifying Token Blacklist..."
try {
    Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/users" -Token $newAccessToken
    Write-Host "ERROR: Token verified but should be blacklisted!" -ForegroundColor Red
} catch {
    # 403 Forbidden or 401 Unauthorized expected
    Write-Host "Success! Access denied with blacklisted token." -ForegroundColor Green
}

Write-Host "`n=== Enterprise E2E Test Part 2 Completed ===" -ForegroundColor Cyan
