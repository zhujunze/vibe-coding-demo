# Easy Accounting Initialization Script

Write-Host "Starting Easy Accounting Initialization..."

# 1. Build Project
Write-Host "Building project..."
./mvnw clean package -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host "Build Success!"
} else {
    Write-Host "Build Failed!"
    exit 1
}

# 2. Run Tests
Write-Host "Running Tests..."
./mvnw test

if ($LASTEXITCODE -eq 0) {
    Write-Host "Tests Passed!"
} else {
    Write-Host "Tests Failed!"
    exit 1
}

Write-Host "Initialization Complete. You can now run the application."
