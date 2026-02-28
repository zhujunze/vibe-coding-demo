# 获取重置密码Token

## 接口描述
验证手机号和验证码，获取重置密码的一次性Token

## 请求信息
- **URL**: `/api/auth/reset-password-token`
- **Method**: `POST`
- **Content-Type**: `application/json`

## 请求参数

### Body Parameters

| 参数名 | 类型 | 必填 | 说明 | 示例 |
|---|---|---|---|---|
| phone | String | 是 | 手机号 | 13800138000 |
| smsCode | String | 是 | 短信验证码 | 123456 |

## 响应结构

### 成功响应 (200 OK)

```json
{
  "code": 200,
  "message": "success",
  "data": "eyJhbGciOiJIUzI1NiJ9...",
  "trace_id": "c0a80101-..."
}
```

### 数据字段说明 (Data Fields)

| 字段名 | 类型 | 说明 | 示例 |
|---|---|---|---|
| data | String | 临时Token | eyJhbGciOiJIUzI1NiJ9... |

## 错误码

| 错误码 | 说明 |
|---|---|
| 20004 | 验证码错误 |
| 20002 | 用户不存在 |
