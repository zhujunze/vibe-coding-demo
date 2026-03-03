<template>
  <view class="register-page">
    <!-- 页面头部 -->
    <view class="register-header">
      <view class="back-btn-wrapper" @click="navigateToLogin">
        <view class="back-btn">
          <text class="back-icon">←</text>
          <text class="back-text">返回登录</text>
        </view>
      </view>
      <view class="header-content">
        <view class="app-icon-wrapper">
          <view class="app-icon">
            <text class="icon-emoji">📝</text>
          </view>
        </view>
        <view class="header-text">
          <text class="title">注册新账户</text>
          <text class="subtitle">创建您的极简记账账户</text>
        </view>
      </view>
    </view>

    <!-- 注册表单 -->
    <view class="register-form-container">
      <view class="form-wrapper">
        <!-- 手机号输入 -->
        <view class="input-group">
          <view class="input-label-wrapper">
            <text class="input-label">手机号</text>
            <text v-if="errors.phone" class="error-text">{{ errors.phone }}</text>
          </view>
          <view class="input-wrapper">
            <text class="input-icon">📱</text>
            <input 
              class="input-field" 
              type="number" 
              placeholder="请输入手机号" 
              v-model="formData.phone"
              @input="onPhoneInput"
            />
          </view>
        </view>

        <!-- 验证码输入 -->
        <view class="input-group">
          <view class="input-label-wrapper">
            <text class="input-label">验证码</text>
            <text v-if="errors.smsCode" class="error-text">{{ errors.smsCode }}</text>
          </view>
          <view class="input-wrapper">
            <text class="input-icon">🔐</text>
            <input 
              class="input-field flex-1" 
              type="text" 
              placeholder="请输入验证码" 
              v-model="formData.smsCode"
              @input="onSmsCodeInput"
            />
            <button 
              class="sms-code-btn" 
              :class="{ 'sms-code-btn-disabled': !canSendSms || smsCountdown > 0 }"
              :disabled="!canSendSms || smsCountdown > 0"
              @click="sendSmsCodeRequest"
            >
              {{ getSmsCodeButtonText }}
            </button>
          </view>
        </view>

        <!-- 密码输入 -->
        <view class="input-group">
          <view class="input-label-wrapper">
            <text class="input-label">设置密码</text>
            <text v-if="errors.password" class="error-text">{{ errors.password }}</text>
          </view>
          <view class="input-wrapper">
            <text class="input-icon">🔒</text>
            <input 
              class="input-field flex-1" 
              :type="passwordVisible ? 'text' : 'password'" 
              placeholder="请输入至少8位密码" 
              v-model="formData.password"
              @input="onPasswordInput"
            />
            <text 
              class="password-toggle" 
              @click="togglePasswordVisibility"
            >
              {{ passwordVisible ? '👁️' : '👁️‍🗨️' }}
            </text>
          </view>
          <!-- 密码强度提示 -->
          <view class="password-strength" v-if="formData.password">
            <view class="strength-bar">
              <view class="strength-progress" :style="strengthBarStyle"></view>
            </view>
            <text class="strength-text">{{ strengthText }}</text>
          </view>
        </view>

        <!-- 用户协议 -->
        <view class="agreement-section">
          <label class="checkbox-label">
            <view class="checkbox-wrapper">
              <view 
                class="checkbox" 
                :class="{ 'checkbox-checked': formData.agreeTerms }"
                @click="onAgreementChange"
              >
                <text v-if="formData.agreeTerms" class="check-icon">✓</text>
              </view>
            </view>
            <text class="agreement-text">
              我已阅读并同意
              <text class="link-text" @click="showAgreement('service')">《服务协议》</text>
              和
              <text class="link-text" @click="showAgreement('privacy')">《隐私政策》</text>
            </text>
          </label>
          <text v-if="errors.agreeTerms" class="error-text agreement-error">{{ errors.agreeTerms }}</text>
        </view>

        <!-- 注册按钮 -->
        <button 
          class="register-btn" 
          :class="{'register-btn-disabled': !canSubmit}"
          :disabled="!canSubmit"
          @click="handleRegister"
        >
          {{ isLoading ? '注册中...' : '注册' }}
        </button>

        <!-- 已有账号链接 -->
        <view class="other-actions">
          <view class="login-btn-wrapper" @click="navigateToLogin">
            <text class="action-text">已有账号？立即登录</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 协议弹窗 -->
    <view class="modal-overlay" v-if="showAgreementModal" @click="closeAgreement">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">{{ agreementTitle }}</text>
          <view class="close-btn-wrapper" @click="closeAgreement">
            <text class="close-btn">×</text>
          </view>
        </view>
        <scroll-view class="modal-body" scroll-y>
          <text class="modal-text">{{ agreementContent }}</text>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { register, sendSmsCode } from '@/utils/api/auth.js'

// 表单数据
const formData = reactive({
  phone: '',
  smsCode: '',
  password: '',
  inviteCode: '',
  agreeTerms: false
})

// 错误信息
const errors = reactive({
  phone: '',
  smsCode: '',
  password: '',
  agreeTerms: ''
})

// 状态管理
const isLoading = ref(false)
const passwordVisible = ref(false)
const showAgreementModal = ref(false)
const agreementTitle = ref('')
const agreementContent = ref('')

// 短信验证码状态
const smsCountdown = ref(0)
const smsTimer = ref(null)

// 密码强度计算
const passwordStrength = computed(() => {
  const password = formData.password
  if (!password) return 0
  
  let strength = 0
  if (password.length >= 8) strength += 1
  if (/[a-z]/.test(password)) strength += 1
  if (/[A-Z]/.test(password)) strength += 1
  if (/\d/.test(password)) strength += 1
  
  return strength
})

// 密码强度文本
const strengthText = computed(() => {
  const strength = passwordStrength.value
  if (strength === 0) return ''
  if (strength <= 1) return '弱密码'
  if (strength <= 2) return '中等密码'
  return '强密码'
})

// 密码强度进度条样式
const strengthBarStyle = computed(() => {
  const strength = passwordStrength.value
  const width = strength * 25
  let color = '#ffab91'
  if (strength >= 2) color = '#ffa502'
  if (strength >= 3) color = '#2ed573'
  
  return {
    width: `${width}%`,
    backgroundColor: color
  }
})

// 是否可以发送短信验证码
const canSendSms = computed(() => {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(formData.phone) && smsCountdown.value === 0
})

// 短信验证码按钮文本
const getSmsCodeButtonText = computed(() => {
  return smsCountdown.value > 0 ? `${smsCountdown.value}秒后重发` : '获取验证码'
})

// 手机号输入处理
const onPhoneInput = (event) => {
  formData.phone = event.detail.value
  if (errors.phone) errors.phone = ''
}

// 验证码输入处理
const onSmsCodeInput = (event) => {
  formData.smsCode = event.detail.value
  if (errors.smsCode) errors.smsCode = ''
}

// 密码输入处理
const onPasswordInput = (event) => {
  formData.password = event.detail.value
  if (errors.password) errors.password = ''
}

// 协议勾选处理
const onAgreementChange = () => {
  formData.agreeTerms = !formData.agreeTerms
  if (errors.agreeTerms) errors.agreeTerms = ''
}

// 切换密码可见性
const togglePasswordVisibility = () => {
  passwordVisible.value = !passwordVisible.value
}

// 发送短信验证码
const sendSmsCodeRequest = async () => {
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(formData.phone)) {
    errors.phone = '请输入正确的手机号'
    return
  }

  try {
    uni.showLoading({
      title: '发送中...'
    })

    const response = await sendSmsCode({
      phone: formData.phone,
      scene: 'register'
    })

    uni.hideLoading()

    if (response.code === 200) {
      uni.showToast({
        title: '验证码已发送',
        icon: 'success',
        duration: 2000
      })

      startSmsCountdown()
    } else {
      uni.showToast({
        title: response.message || '验证码发送失败',
        icon: 'none',
        duration: 2000
      })
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
    uni.hideLoading()
    uni.showToast({
      title: '验证码发送失败，请稍后重试',
      icon: 'none',
      duration: 2000
    })
  }
}

// 开始短信验证码倒计时
const startSmsCountdown = () => {
  smsCountdown.value = 60
  smsTimer.value = setInterval(() => {
    smsCountdown.value--
    if (smsCountdown.value <= 0) {
      clearInterval(smsTimer.value)
      smsTimer.value = null
    }
  }, 1000)
}

// 显示协议弹窗
const showAgreement = (type) => {
  if (type === 'service') {
    agreementTitle.value = '服务协议'
    agreementContent.value = `欢迎使用极简记账服务。在使用我们的服务之前，请仔细阅读并理解以下条款：

1. 服务内容
我们提供个人财务管理服务，帮助您记录收支情况。

2. 用户责任
- 请妥善保管您的账户信息
- 如实填写相关信息
- 合法使用服务

3. 数据安全
- 我们承诺保护您的个人隐私
- 采取合理的技术措施保护数据安全
- 未经您同意，不会向第三方透露个人信息

4. 服务变更
我们保留随时修改服务条款的权利，修改后的条款将在应用内公布。

5. 免责声明
- 请您妥善保管账户信息，因泄露造成的损失由用户承担
- 网络服务可能因不可抗力中断，我们尽力保障服务稳定性`
  } else if (type === 'privacy') {
    agreementTitle.value = '隐私政策'
    agreementContent.value = `我们非常重视您的隐私保护，特制定以下隐私政策：

1. 信息收集
- 设备信息：设备型号、操作系统版本等
- 使用信息：应用使用情况、操作记录等
- 位置信息：经您授权后收集位置信息

2. 信息使用
- 提供个性化服务
- 优化产品功能
- 保障账户安全

3. 信息共享
我们不会向任何无关第三方提供、出售、出租、分享您的个人信息，除非经过您明确同意或法律要求。

4. 数据安全
- 采用行业标准的安全措施保护数据
- 限制内部人员访问权限
- 定期进行安全检查

5. 您的权利
- 查看、更正您的个人信息
- 删除您的个人信息
- 注销您的账户

6. 更新
本政策可能会不定期更新，请定期查看最新版本。`
  }
  
  showAgreementModal.value = true
}

// 关闭协议弹窗
const closeAgreement = () => {
  showAgreementModal.value = false
  agreementTitle.value = ''
  agreementContent.value = ''
}

// 表单验证
const validateForm = () => {
  let isValid = true
  
  if (!formData.phone) {
    errors.phone = '请输入手机号'
    isValid = false
  } else if (!isPhoneValid.value) {
    errors.phone = '请输入正确的手机号'
    isValid = false
  }
  
  if (!formData.smsCode) {
    errors.smsCode = '请输入验证码'
    isValid = false
  } else if (formData.smsCode.length !== 6) {
    errors.smsCode = '验证码为6位数字'
    isValid = false
  }
  
  if (!formData.password) {
    errors.password = '请输入密码'
    isValid = false
  } else if (formData.password.length < 8) {
    errors.password = '密码至少需8位'
    isValid = false
  } else if (!isPasswordStrongEnough.value) {
    errors.password = '密码需包含大小写字母和数字'
    isValid = false
  }
  
  if (!formData.agreeTerms) {
    errors.agreeTerms = '请阅读并同意服务协议和隐私政策'
    isValid = false
  }
  
  return isValid
}

// 手机号格式验证
const isPhoneValid = computed(() => {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(formData.phone)
})

// 密码强度验证
const isPasswordStrongEnough = computed(() => {
  const password = formData.password
  return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/.test(password)
})

// 是否可以提交
const canSubmit = computed(() => {
  return isPhoneValid.value && 
         isPasswordStrongEnough.value && 
         formData.smsCode.length === 6 &&
         formData.agreeTerms && 
         !isLoading.value
})

// 处理注册
const handleRegister = async () => {
  if (!validateForm()) {
    return
  }

  try {
    isLoading.value = true
    
    const response = await register({
      phone: formData.phone,
      password: formData.password,
      smsCode: formData.smsCode,
      inviteCode: formData.inviteCode || undefined
    })
    
    console.log('注册响应:', response)
    
    if (response.code === 200) {
      uni.showToast({
        title: '注册成功',
        icon: 'success',
        duration: 2000
      })
      
      setTimeout(() => {
        uni.navigateTo({
          url: `/pages/auth/Login?account=${encodeURIComponent(formData.phone)}`
        })
      }, 1500)
    } else {
      handleApiError(response)
    }
  } catch (error) {
    console.error('注册失败:', error)
    if (error.errMsg) {
      uni.showToast({
        title: '网络连接失败，请检查网络',
        icon: 'none',
        duration: 2000
      })
    } else {
      uni.showToast({
        title: '注册失败，请稍后重试',
        icon: 'none',
        duration: 2000
      })
    }
  } finally {
    isLoading.value = false
  }
}

// 处理API错误
const handleApiError = (response) => {
  if (response.code === 20004) {
    errors.smsCode = '验证码错误或已失效'
    uni.showToast({
      title: '验证码错误',
      icon: 'none',
      duration: 2000
    })
  } else if (response.code === 20005) {
    errors.phone = '该手机号已注册'
    uni.showToast({
      title: '该手机号已注册',
      icon: 'none',
      duration: 2000
    })
  } else if (response.code === 400) {
    if (response.message.includes('手机')) {
      errors.phone = '手机号格式错误'
    } else if (response.message.includes('密码')) {
      errors.password = '密码复杂度不够'
    } else if (response.message.includes('验证')) {
      errors.smsCode = '验证码错误'
    } else {
      errors.phone = '注册信息有误，请检查'
    }
    uni.showToast({
      title: response.message || '注册信息有误，请检查',
      icon: 'none',
      duration: 2000
    })
  } else {
    uni.showToast({
      title: response.message || '注册失败，请稍后重试',
      icon: 'none',
      duration: 2000
    })
  }
}

// 跳转到登录页
const navigateToLogin = () => {
  uni.navigateTo({
    url: '/pages/auth/Login'
  })
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fdfbf7 0%, #fff5f0 100%);
  padding: 40rpx;
  box-sizing: border-box;
}

.register-header {
  text-align: center;
  margin-bottom: 60rpx;
  
  .back-btn-wrapper {
    display: flex;
    justify-content: flex-start;
    margin-bottom: 24rpx;
    cursor: pointer;
  }
  
  .back-btn {
    display: flex;
    align-items: center;
    gap: 8rpx;
    padding: 12rpx 24rpx;
    background: #fff3e0;
    border-radius: 20rpx;
    transition: all 0.3s ease;
    
    &:active {
      background: #ffe0b2;
    }
  }
  
  .back-icon {
    font-size: 32rpx;
    color: #ff7043;
  }
  
  .back-text {
    font-size: 28rpx;
    color: #ff7043;
    font-weight: 600;
  }
  
  .header-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 24rpx;
  }
  
  .app-icon-wrapper {
    width: 120rpx;
    height: 120rpx;
    border-radius: 36rpx;
    background: linear-gradient(135deg, #fff0f5 0%, #ffe4e1 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 8rpx 24rpx rgba(255, 182, 193, 0.3);
  }
  
  .app-icon {
    width: 80rpx;
    height: 80rpx;
    border-radius: 24rpx;
    background: linear-gradient(135deg, #ffb6c1 0%, #ff69b4 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 48rpx;
  }
  
  .header-text {
    display: flex;
    flex-direction: column;
    gap: 12rpx;
  }
  
  .title {
    display: block;
    font-size: 44rpx;
    font-weight: 700;
    color: #5d4037;
    letter-spacing: 2rpx;
  }
  
  .subtitle {
    display: block;
    font-size: 28rpx;
    color: #a1887f;
  }
}

.register-form-container {
  .form-wrapper {
    background: #ffffff;
    border-radius: 32rpx;
    padding: 60rpx 48rpx;
    box-shadow: 0 12rpx 48rpx rgba(0, 0, 0, 0.08);
  }
}

.input-group {
  margin-bottom: 40rpx;
  
  .input-label-wrapper {
    display: flex;
    flex-direction: column;
    gap: 8rpx;
    margin-bottom: 16rpx;
  }
  
  .input-label {
    display: block;
    font-size: 28rpx;
    color: #5d4037;
    font-weight: 600;
  }
  
  .input-wrapper {
    display: flex;
    align-items: center;
    gap: 16rpx;
    width: 100%;
    height: 90rpx;
    background: #fff8f1;
    border: 2rpx solid #ffe0b2;
    border-radius: 20rpx;
    padding: 0 24rpx;
    box-sizing: border-box;
    
    &:focus-within {
      background: #fffaf0;
      border-color: #ffab91;
    }
  }
  
  .input-icon {
    font-size: 32rpx;
    color: #ffab91;
  }
  
  .input-field {
    flex: 1;
    font-size: 28rpx;
    color: #5d4037;
  }
  
  .password-toggle {
    font-size: 32rpx;
    color: #ffab91;
    padding: 0 16rpx;
  }
  
  .sms-code-btn {
    width: 160rpx;
    height: 60rpx;
    background: linear-gradient(135deg, #ffab91 0%, #ff7043 100%);
    color: #ffffff;
    border: none;
    border-radius: 20rpx;
    font-size: 26rpx;
    font-weight: 600;
    box-shadow: 0 4rpx 12rpx rgba(255, 112, 67, 0.3);
    transition: all 0.3s ease;
    
    &:active:not(.sms-code-btn-disabled) {
      transform: scale(0.96);
      box-shadow: 0 2rpx 6rpx rgba(255, 112, 67, 0.4);
    }
  }
  
  .sms-code-btn-disabled {
    background: #e0e0e0;
    color: #bdbdbd;
    box-shadow: none;
  }
  
  .error-text {
    display: block;
    font-size: 24rpx;
    color: #ef5350;
    margin-top: 8rpx;
    min-height: 32rpx;
  }
}

.password-strength {
  margin-top: 12rpx;
  
  .strength-bar {
    width: 100%;
    height: 8rpx;
    background-color: #ffe0b2;
    border-radius: 4rpx;
    overflow: hidden;
    
    .strength-progress {
      height: 100%;
      transition: width 0.3s ease, background-color 0.3s ease;
    }
  }
  
  .strength-text {
    display: block;
    font-size: 24rpx;
    color: #795548;
    margin-top: 8rpx;
    font-weight: 600;
  }
}

.agreement-section {
  margin-bottom: 40rpx;
  
  .checkbox-label {
    display: flex;
    align-items: flex-start;
    gap: 16rpx;
    cursor: pointer;
  }
  
  .checkbox-wrapper {
    margin-top: 6rpx;
  }
  
  .checkbox {
    width: 40rpx;
    height: 40rpx;
    border: 3rpx solid #ffab91;
    border-radius: 12rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
  }
  
  .checkbox-checked {
    background: linear-gradient(135deg, #ffab91 0%, #ff7043 100%);
    border-color: #ff7043;
  }
  
  .check-icon {
    color: #ffffff;
    font-size: 28rpx;
    font-weight: bold;
  }
  
  .agreement-text {
    flex: 1;
    font-size: 26rpx;
    color: #795548;
    line-height: 1.6;
    
    .link-text {
      color: #ff7043;
      font-weight: 600;
      text-decoration: underline;
    }
  }
  
  .agreement-error {
    margin-top: 12rpx;
  }
}

.register-btn {
  width: 100%;
  height: 90rpx;
  background: linear-gradient(135deg, #ffab91 0%, #ff7043 100%);
  color: #ffffff;
  border: none;
  border-radius: 24rpx;
  font-size: 34rpx;
  font-weight: 700;
  box-shadow: 0 8rpx 24rpx rgba(255, 112, 67, 0.3);
  transition: all 0.3s ease;
  
  &:active:not(.register-btn-disabled) {
    transform: scale(0.98);
    box-shadow: 0 4rpx 12rpx rgba(255, 112, 67, 0.4);
  }
  
  &.register-btn-disabled {
    background: #e0e0e0;
    color: #bdbdbd;
    box-shadow: none;
  }
}

.other-actions {
  display: flex;
  justify-content: center;
  margin: 32rpx 0;
  
  .login-btn-wrapper {
    display: inline-block;
    padding: 16rpx 40rpx;
    background: #fff3e0;
    border-radius: 24rpx;
    transition: all 0.3s ease;
    
    &:active {
      background: #ffe0b2;
    }
  }
  
  .action-text {
    font-size: 28rpx;
    color: #ff7043;
    font-weight: 600;
  }
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 40rpx;
}

.modal-content {
  width: 100%;
  max-height: 80vh;
  background: #ffffff;
  border-radius: 32rpx;
  overflow: hidden;
  box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 40rpx;
  border-bottom: 2rpx solid #fff3e0;
  
  .modal-title {
    font-size: 36rpx;
    font-weight: 700;
    color: #5d4037;
  }
  
  .close-btn-wrapper {
    cursor: pointer;
  }
  
  .close-btn {
    font-size: 48rpx;
    color: #bdbdbd;
    line-height: 1;
    padding: 8rpx;
    transition: color 0.3s ease;
    
    &:hover {
      color: #ff7043;
    }
  }
}

.modal-body {
  box-sizing: border-box;
  padding: 40rpx;
  max-height: 60vh;
  
  .modal-text {
    font-size: 28rpx;
    color: #795548;
    line-height: 1.8;
    white-space: pre-wrap;
  }
}
</style>
