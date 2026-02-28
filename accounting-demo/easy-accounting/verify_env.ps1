# 验证环境切换脚本 (Windows PowerShell)

$JAR_FILE = "target/easy-accounting-0.0.1-SNAPSHOT.jar"
$URL = "http://localhost:8080/actuator/env"

# 1. 确保构建成功
Write-Host "正在构建项目..."
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Error "构建失败！"
    exit 1
}

# 2. 定义启动与检查函数
function Test-Profile ($Profile, $ExpectedKey, $ExpectedValue) {
    Write-Host "`n>>> 正在测试 Profile: [$Profile] ..."
    
    $Process = Start-Process -FilePath "java" -ArgumentList "-jar", "-Dspring.profiles.active=$Profile", $JAR_FILE -PassThru -NoNewWindow
    
    # 等待启动 (简单的休眠，实际可轮询健康检查接口)
    Write-Host "等待应用启动 (15s)..."
    Start-Sleep -Seconds 15
    
    try {
        $Response = Invoke-RestMethod -Uri $URL -Method Get
        # 这里简化检查，实际需解析 JSON
        # 假设 Actuator 暴露了 env
        Write-Host "应用已响应，正在验证配置..."
        
        # 简单验证: 打印 activeProfiles
        Write-Host "Active Profiles: $($Response.activeProfiles)"
        
        if ($Response.activeProfiles -contains $Profile) {
            Write-Host "验证成功: Profile [$Profile] 已生效" -ForegroundColor Green
        } else {
            Write-Host "验证失败: Profile [$Profile] 未生效" -ForegroundColor Red
        }

    } catch {
        Write-Error "无法连接到应用: $_"
    } finally {
        Stop-Process -Id $Process.Id -Force
        Write-Host "应用已停止。"
    }
}

# 3. 执行测试
# 验证 Dev
Test-Profile "dev" "spring.profiles.active" "dev"

# 验证 Prod
Test-Profile "prod" "spring.profiles.active" "prod"

Write-Host "`n验证脚本执行完毕。"
