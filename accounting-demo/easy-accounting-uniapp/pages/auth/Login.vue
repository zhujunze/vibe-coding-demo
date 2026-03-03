<template>
  <view class="login-page">
    <!-- 页面头部 -->
    <view class="login-header">
      <view class="header-content">
        <view class="app-icon-wrapper">
          <view class="app-icon">
            <text class="icon-emoji">💰</text>
          </view>
        </view>
        <view class="header-text">
          <text class="title">欢迎使用极简记账</text>
          <text class="subtitle">轻松管理您的每一笔收支</text>
        </view>
      </view>
    </view>

    <!-- 登录表单 -->
    <view class="login-form-container">
      <view class="form-wrapper">
        <!-- 账号输入 -->
        <view class="input-group">
          <view class="input-label-wrapper">
            <text class="input-label">手机号/邮箱</text>
            <text v-if="errors.account" class="error-text">{{ errors.account }}</text>
          </view>
          <view class="input-wrapper">
            <text class="input-icon">👤</text>
            <input 
              class="input-field" 
              type="text" 
              placeholder="请输入手机号或邮箱" 
              v-model="formData.account"
              @input="onAccountInput"
            />
          </view>
        </view>

        <!-- 密码输入 -->
        <view class="input-group">
          <view class="input-label-wrapper">
            <text class="input-label">密码</text>
            <text v-if="errors.password" class="error-text">{{ errors.password }}</text>
          </view>
          <view class="input-wrapper">
            <text class="input-icon">🔒</text>
            <input 
              class="input-field flex-1" 
              :type="passwordVisible ? 'text' : 'password'" 
              placeholder="请输入密码" 
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
        </view>

        <!-- 用户协议 -->
        <view class="agreement-section">
          <label class="checkbox-label">
            <view class="checkbox-wrapper">
              <view 
                class="checkbox" 
                :class="{ 'checkbox-checked': formData.agreed }"
                @click="onAgreementChange"
              >
                <text v-if="formData.agreed" class="check-icon">✓</text>
              </view>
            </view>
            <text class="agreement-text">
              我已阅读并同意
              <text class="link-text" @click="showAgreement('service')">《服务协议》</text>
              和
              <text class="link-text" @click="showAgreement('privacy')">《隐私政策》</text>
            </text>
          </label>
          <text v-if="errors.agreed" class="error-text agreement-error">{{ errors.agreed }}</text>
        </view>

        <!-- 登录按钮 -->
        <button 
          class="login-btn" 
          :class="{ 'login-btn-disabled': !canSubmit }"
          :disabled="!canSubmit || isLoading"
          @click="handleLogin"
        >
          <text v-if="!isLoading">登录</text>
          <text v-else>登录中...</text>
        </button>

        <!-- 其他操作 -->
        <view class="other-actions">
          <text class="action-text" @click="toRegister">注册账号</text>
          <text class="action-text" @click="toForgotPassword">忘记密码?</text>
        </view>

        <!-- 游客模式入口 -->
        <view class="guest-section">
          <view class="guest-btn" @click="useGuestMode">
            <text class="guest-text">先体验一下 ></text>
          </view>
        </view>
      </view>
    </view>

    <!-- 协议弹窗 -->
    <view v-if="showAgreementModal" class="modal-overlay" @click="closeAgreement">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">{{ agreementTitle }}</text>
          <view class="close-btn-wrapper" @click="closeAgreement">
            <text class="close-btn">×</text>
          </view>
        </view>
        <scroll-view class="modal-body" scroll-y="true">
          <text class="agreement-content">{{ agreementContent }}</text>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { login } from '@/utils/api/auth.js'

// 表单数据
const formData = reactive({
  account: '',
  password: '',
  agreed: false
})

// 错误信息
const errors = reactive({
  account: '',
  password: '',
  agreed: ''
})

// 状态管理
const isLoading = ref(false)
const passwordVisible = ref(false)
const showAgreementModal = ref(false)
const agreementTitle = ref('')
const agreementContent = ref('')

// 计算属性
const isAccountValid = computed(() => {
  const phoneRegex = /^1[3-9]\d{9}$/
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return phoneRegex.test(formData.account) || emailRegex.test(formData.account)
})

const canSubmit = computed(() => {
  return isAccountValid.value && 
         formData.password.length >= 6 && 
         formData.agreed &&
         !isLoading.value
})

// 账号输入处理
const onAccountInput = (event) => {
  formData.account = event.detail.value
  if (errors.account) {
    errors.account = ''
  }
}

// 密码输入处理
const onPasswordInput = (event) => {
  formData.password = event.detail.value
  if (errors.password) {
    errors.password = ''
  }
}

// 协议勾选处理
const onAgreementChange = () => {
  formData.agreed = !formData.agreed
  if (errors.agreed) {
    errors.agreed = ''
  }
}

// 切换密码可见性
const togglePasswordVisibility = () => {
  passwordVisible.value = !passwordVisible.value
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
  
  if (!formData.account) {
    errors.account = '请输入手机号或邮箱'
    isValid = false
  } else if (!isAccountValid.value) {
    errors.account = '请输入正确的手机号或邮箱'
    isValid = false
  }
  
  if (!formData.password) {
    errors.password = '请输入密码'
    isValid = false
  } else if (formData.password.length < 6) {
    errors.password = '密码至少需要6位'
    isValid = false
  }
  
  if (!formData.agreed) {
    errors.agreed = '请阅读并同意服务协议和隐私政策'
    isValid = false
  }
  
  return isValid
}

// 处理登录
const handleLogin = async () => {
  if (!validateForm()) {
    return
  }

  try {
    isLoading.value = true
    
    const response = await login({
      account: formData.account,
      password: formData.password
    })
    
    console.log('登录响应:', response)
    
    if (response.code === 200) {
      uni.setStorageSync('token', response.data.accessToken)
      uni.setStorageSync('userInfo', response.data.userInfo)
      
      uni.showToast({
        title: '登录成功',
        icon: 'success'
      })
      
      setTimeout(() => {
        uni.switchTab({
          url: '/pages/tabBar/bill/index'
        })
      }, 1000)
    } else {
      uni.showModal({
        title: '登录失败',
        content: response.message || '登录失败，请稍后重试',
        showCancel: false
      })
    }
    
  } catch (error) {
    console.error('登录失败:', error)
    
    if (error.errMsg) {
      uni.showModal({
        title: '网络错误',
        content: '网络连接失败，请检查网络后重试',
        showCancel: false
      })
    } else if (error.code) {
      let errorMsg = '登录失败'
      switch (error.code) {
        case 20002:
          errorMsg = '用户不存在'
          break
        case 20003:
          errorMsg = '密码错误'
          break
        case 401:
          errorMsg = '账号已锁定'
          break
        default:
          errorMsg = error.message || '登录失败，请稍后重试'
      }
      
      uni.showModal({
        title: '登录失败',
        content: errorMsg,
        showCancel: false
      })
    } else {
      uni.showModal({
        title: '登录失败',
        content: error.message || '登录过程中出现错误，请稍后重试',
        showCancel: false
      })
    }
  } finally {
    isLoading.value = false
  }
}

// 跳转到注册页面
const toRegister = () => {
  uni.navigateTo({
    url: '/pages/auth/Register'
  })
}

// 跳转到忘记密码页面
const toForgotPassword = () => {
  uni.navigateTo({
    url: '/pages/auth/ForgotPassword'
  })
}

// 使用游客模式
const useGuestMode = () => {
  uni.showModal({
    title: '提示',
    content: '您将以游客身份使用应用，部分功能将受限，是否继续？',
    success: (res) => {
      if (res.confirm) {
        uni.setStorageSync('isGuest', true)
        
        uni.switchTab({
          url: '/pages/tabBar/bill/index'
        })
      }
    }
  })
}

// 页面加载时处理参数
onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  if (currentPage.options && currentPage.options.account) {
    formData.account = decodeURIComponent(currentPage.options.account)
  }
})

// 组件卸载时清理定时器
onUnmounted(() => {
})
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fdfbf7 0%, #fff5f0 100%);
  padding: 40rpx;
  box-sizing: border-box;
}

.login-header {
  text-align: center;
  margin-bottom: 60rpx;
  
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

.login-form-container {
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
  
  .error-text {
    display: block;
    font-size: 24rpx;
    color: #ef5350;
    margin-top: 8rpx;
    min-height: 32rpx;
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

.login-btn {
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
  
  &:active:not(.login-btn-disabled) {
    transform: scale(0.98);
    box-shadow: 0 4rpx 12rpx rgba(255, 112, 67, 0.4);
  }
  
  &.login-btn-disabled {
    background: #e0e0e0;
    color: #bdbdbd;
    box-shadow: none;
  }
}

.other-actions {
  display: flex;
  justify-content: space-between;
  margin: 32rpx 0;
  
  .action-text {
    font-size: 28rpx;
    color: #ff7043;
    font-weight: 600;
  }
}

.guest-section {
  text-align: center;
  margin-top: 40rpx;
  
  .guest-btn {
    display: inline-block;
    padding: 20rpx 48rpx;
    background: #fff3e0;
    border-radius: 30rpx;
    transition: all 0.3s ease;
    
    &:active {
      background: #ffe0b2;
    }
  }
  
  .guest-text {
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
  
  .agreement-content {
    font-size: 28rpx;
    line-height: 1.8;
    color: #795548;
    white-space: pre-line;
  }
}
</style>
