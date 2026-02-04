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

    try {
        $params = @{
            Method = $Method
            Uri = $Url
            Headers = $Headers
        }
        if ($Body) {
            $params.Body = $Body
        }
        
        $response = Invoke-RestMethod @params
        return $response
    } catch {
        Write-Host "Error calling $Url ($Method): $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            Write-Host "Response Body: $($reader.ReadToEnd())" -ForegroundColor Red
        }
        throw $_
    }
}

$baseUrl = "http://localhost:8089"
$timestamp = Get-Date -Format "HHmmss"
$username = "user_$timestamp"
$email = "user_$timestamp@example.com"
$password = "Password@123"

Write-Host "`n=== Starting E2E Test ($username) ===" -ForegroundColor Cyan

# 1. Register
Write-Host "`n1. Registering user..."
$registerBody = @"
{
    "username": "$username",
    "email": "$email",
    "password": "$password",
    "roles": []
}
"@
$registerResponse = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/auth/register" -Body $registerBody
Write-Host "Success! User ID: $($registerResponse.user.id)" -ForegroundColor Green

# 2. Login
Write-Host "`n2. Logging in..."
$loginBody = @"
{
    "username": "$username",
    "password": "$password"
}
"@
$loginResponse = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/auth/login" -Body $loginBody
$token = $loginResponse.access_token
Write-Host "Success! Token received." -ForegroundColor Green

# 3. Get User Profile (using username from register response which is actually returning AuthResponse... wait AuthController register returns AuthResponse)
# Wait, AuthController.register returns AuthResponse, not UserDTO. 
# Let's verify AuthController code again. 
# AuthController.register logic:
# UserDTO registeredUser = userService.registerUser(userDTO);
# ...
# return ResponseEntity.ok(AuthResponse...)
# So register response has access_token, refresh_token. It does NOT have ID directly unless I parse it or get it from somewhere else.
# But I can use 'getUserByUsername' if I have the username.

Write-Host "`n3. Fetching User by Username..."
$userProfile = Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/users/username/$username" -Token $token
$userId = $userProfile.id
Write-Host "Success! Found User ID: $userId" -ForegroundColor Green

# 4. Update User
Write-Host "`n4. Updating User..."
$updateBody = @"
{
    "username": "${username}_updated",
    "email": "${email}_updated",
    "roles": []
}
"@
# Note: changing username might invalidate token if subject claim depends on it? 
# JwtService.extractUsername extracts subject. 
# UserServiceImpl register uses userDTO.getUsername() as subject?
# Let's check logic. Assuming changing username is allowed and token remains valid for *current* request until checked against DB again?
# Actually CustomUserDetailsService loads by username. If I change username in DB, the old token has old username.
# CustomUserDetailsService.loadUserByUsername(oldName) will fail if I verify token against DB.
# JwtAuthenticationFilter: userDetailsService.loadUserByUsername(userEmail);
# So if I update username, subsequent requests with OLD token (containing OLD username) will fail because loadUserByUsername(OLD) will throw NotFound.
# So I should NOT update username for this simple test, or I must re-login.
# Let's just update email.

$updateBodySafe = @"
{
    "username": "$username",
    "email": "${email}_updated",
    "roles": []
}
"@

$updateResponse = Invoke-ApiRequest -Method "PUT" -Url "$baseUrl/api/users/$userId" -Body $updateBodySafe -Token $token
Write-Host "Success! Updated email to: $($updateResponse.email)" -ForegroundColor Green

# 5. Delete User
Write-Host "`n5. Deleting User..."
Invoke-ApiRequest -Method "DELETE" -Url "$baseUrl/api/users/$userId" -Token $token
Write-Host "Success! User deleted." -ForegroundColor Green

Write-Host "`n=== E2E Test Completed Successfully ===" -ForegroundColor Cyan
