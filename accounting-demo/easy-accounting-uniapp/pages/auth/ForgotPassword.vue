<template>
  <!-- 忘记密码页面 -->
  <view class="forgot-password-page">
    <!-- 页面头部 -->
    <view class="forgot-header">
      <view class="back-btn-wrapper" @click="goBack">
        <view class="back-btn">
          <text class="back-icon">←</text>
          <text class="back-text">返回登录</text>
        </view>
      </view>
      <view class="header-content">
        <view class="app-icon-wrapper">
          <view class="app-icon">
            <text class="icon-emoji">🔑</text>
          </view>
        </view>
        <view class="header-text">
          <text class="title">找回密码</text>
          <text class="subtitle">重置您的账户密码</text>
        </view>
      </view>
    </view>

    <!-- 找回密码表单 -->
    <view class="forgot-form-container">
      <view class="form-wrapper">
          <!-- 手机号输入框 -->
          <view class="input-group">
            <view class="input-label-wrapper">
              <text class="input-label">手机号</text>
              <text v-if="errors.phone" class="error-text">{{ errors.phone }}</text>
            </view>
            <view class="input-wrapper">
              <text class="input-icon">📱</text>
              <input 
                v-model="formData.phone"
                class="input-field"
                type="number"
                placeholder="请输入手机号"
                maxlength="11"
                @input="onPhoneInput"
              />
            </view>
          </view>

          <!-- 验证码输入框 -->
          <view class="input-group">
            <view class="input-label-wrapper">
              <text class="input-label">验证码</text>
              <text v-if="errors.smsCode" class="error-text">{{ errors.smsCode }}</text>
            </view>
            <view class="input-wrapper">
              <text class="input-icon">🔐</text>
              <input 
                v-model="formData.smsCode"
                class="input-field flex-1"
                :class="{ 'input-error': !!errors.smsCode }"
                type="text"
                placeholder="请输入验证码"
                maxlength="6"
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

          <!-- 发送重置链接按钮 -->
          <button 
            class="submit-button" 
            :class="{ 'submit-button-disabled': !canSubmit || isSubmitting }"
            :disabled="!canSubmit || isSubmitting"
            @click="handleSendResetLink"
          >
            {{ submitButtonText }}
          </button>

          <!-- 倒计时提示 -->
          <text v-if="countdown > 0" class="error-text" style="text-align: center; margin-top: 24rpx;">
            {{ countdown }}秒后可重新发送
          </text>

          <!-- 底部链接 -->
          <view class="other-actions">
            <view class="login-btn-wrapper" @click="toLogin">
              <text class="action-text">想起密码了？立即登录</text>
            </view>
          </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onUnmounted } from 'vue';
import { forgotPassword, sendSmsCode } from '@/utils/api/auth.js';

// 表单数据
const formData = reactive({
  phone: '',
  smsCode: ''
});

// 错误信息
const errors = reactive({
  phone: '',
  smsCode: ''
});

// 状态管理
const isSubmitting = ref(false);
const countdown = ref(0);
const countdownTimer = ref(null);
const smsCountdown = ref(0);
const smsTimer = ref(null);

// 计算属性
const canSubmit = computed(() => {
  return formData.phone.trim() !== '' && 
         isPhoneValid.value && 
         formData.smsCode.trim() !== '' && 
         formData.smsCode.length === 6 &&
         smsCountdown.value <= 0;
});

const submitButtonText = computed(() => {
  if (isSubmitting.value) {
    return '发送中...';
  }
  if (countdown.value > 0) {
    return `${countdown.value}秒后重发`;
  }
  return '发送重置链接';
});

// 手机号格式验证
const isPhoneValid = computed(() => {
  const phoneRegex = /^1[3-9]\d{9}$/;
  return phoneRegex.test(formData.phone);
});

// 验证码输入处理
const onSmsCodeInput = () => {
  errors.smsCode = '';
};

// 手机号输入处理
const onPhoneInput = () => {
  errors.phone = '';
};

// 是否可以发送短信验证码
const canSendSms = computed(() => {
  const phoneRegex = /^1[3-9]\d{9}$/;
  return phoneRegex.test(formData.phone) && smsCountdown.value === 0;
});

// 验证码按钮文本
const getSmsCodeButtonText = computed(() => {
  return smsCountdown.value > 0 ? `${smsCountdown.value}秒后重发` : '获取验证码';
});

// 发送短信验证码
const sendSmsCodeRequest = async () => {
  const phoneRegex = /^1[3-9]\d{9}$/;
  if (!phoneRegex.test(formData.phone)) {
    errors.phone = '请输入正确的手机号';
    return;
  }

  try {
    uni.showLoading({
      title: '发送中...'
    });

    const response = await sendSmsCode({
      phone: formData.phone,
      scene: 'reset'
    });

    uni.hideLoading();

    if (response.code === 200) {
      uni.showToast({
        title: '验证码已发送',
        icon: 'success',
        duration: 2000
      });

      startSmsCountdown();

    } else {
      uni.showToast({
        title: response.message || '验证码发送失败',
        icon: 'none',
        duration: 2000
      });
    }
  } catch (error) {
    console.error('发送验证码失败:', error);
    uni.hideLoading();
    uni.showToast({
      title: '验证码发送失败，请稍后重试',
      icon: 'none',
      duration: 2000
    });
  }
};

// 开始短信验证码倒计时
const startSmsCountdown = () => {
  smsCountdown.value = 60;
  smsTimer.value = setInterval(() => {
    smsCountdown.value--;
    if (smsCountdown.value <= 0) {
      clearInterval(smsTimer.value);
      smsTimer.value = null;
    }
  }, 1000);
};

// 发送重置链接
const handleSendResetLink = async () => {
  if (!formData.phone.trim()) {
    errors.phone = '请输入手机号';
    return;
  }

  if (!isPhoneValid.value) {
    errors.phone = '请输入正确的手机号';
    return;
  }

  if (!formData.smsCode.trim()) {
    errors.smsCode = '请输入验证码';
    return;
  }

  if (formData.smsCode.length !== 6) {
    errors.smsCode = '验证码为6位数字';
    return;
  }

  if (isSubmitting.value) {
    return;
  }

  isSubmitting.value = true;

  try {
    const response = await forgotPassword({
      phone: formData.phone.trim(),
      smsCode: formData.smsCode.trim()
    });

    if (response.code === 200) {
      startCountdown();
      
      uni.showToast({
        title: '重置链接已发送，请查收邮件',
        icon: 'success',
        duration: 2000
      });
      
      uni.navigateTo({
        url: '/pages/auth/ResetPassword?token=' + response.data
      });

    } else {
      let errorMessage = response.message || '发送失败，请稍后重试';
      
      if (response.code === 20002) {
        errorMessage = '手机号未注册，请检查后重试';
        errors.phone = errorMessage;
      } else if (response.code === 20004) {
        errorMessage = '验证码错误，请重新输入';
        errors.smsCode = errorMessage;
      } else if (response.code === 20005) {
        errorMessage = '发送过于频繁，请稍后再试';
        startCountdown(60);
      }
      
      uni.showToast({
        title: errorMessage,
        icon: 'none',
        duration: 2000
      });
    }
  } catch (error) {
    console.error('发送重置链接失败:', error);
    uni.showToast({
      title: '网络错误，请稍后重试',
      icon: 'none',
      duration: 2000
    });
    isSubmitting.value = false;
  }
};

// 启动倒计时
const startCountdown = (seconds = 60) => {
  countdown.value = seconds;
  
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value);
  }
  
  countdownTimer.value = setInterval(() => {
    countdown.value--;
    
    if (countdown.value <= 0) {
      clearInterval(countdownTimer.value);
      countdownTimer.value = null;
    }
  }, 1000);
};

// 返回上一页
const goBack = () => {
  uni.navigateBack();
};

// 跳转到登录页
const toLogin = () => {
  uni.navigateTo({
    url: '/pages/auth/Login'
  });
};

// 页面卸载时清理定时器
onUnmounted(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value);
  }
  if (smsTimer.value) {
    clearInterval(smsTimer.value);
  }
});
</script>

<style lang="scss" scoped>
.forgot-password-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fdfbf7 0%, #fff5f0 100%);
  padding: 40rpx;
  box-sizing: border-box;
}

.forgot-header {
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

.forgot-form-container {
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

.submit-button {
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
  
  &:active:not(.submit-button-disabled) {
    transform: scale(0.98);
    box-shadow: 0 4rpx 12rpx rgba(255, 112, 67, 0.4);
  }
  
  &.submit-button-disabled {
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
</style>
