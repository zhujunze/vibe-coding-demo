<template>
  <!-- 重置密码页面 -->
  <view class="reset-password-page">
    <!-- 自定义导航栏 -->
    <view class="page-header">
      <view class="back-button" @click="goBack">
        <text class="iconfont icon-back">◄</text>
      </view>
      <text class="header-title">重置密码</text>
      <view class="empty-space"></view>
    </view>

    <!-- 主要内容区域 -->
    <scroll-view v-if="isTokenValid" class="main-content" scroll-y="true">
      <view class="container">
        <view class="title-section">
          <text class="title-icon">🔒</text>
          <text class="title-text">设置新密码</text>
          <text class="subtitle">请输入新密码</text>
        </view>

        <ResetPasswordForm
          :formData="formData"
          :errors="errors"
          :isSubmitting="isSubmitting"
          :passwordStrength="passwordStrength"
          :canSubmit="canSubmit"
          @update-password="updatePassword"
          @update-confirm-password="updateConfirmPassword"
          @submit="handleResetPassword"
        />
      </view>
    </scroll-view>

    <!-- 令牌无效页面 -->
    <view v-else class="invalid-token-page">
      <view class="error-content">
        <text class="error-icon">❌</text>
        <text class="error-title">链接已失效</text>
        <text class="error-subtitle">{{ tokenErrorMessage }}</text>
        <button class="retry-button" @click="goToForgotPassword">
          <text>重新获取重置链接</text>
        </button>
      </view>
    </view>

    <!-- 加载遮罩 -->
    <view v-if="isLoading" class="loading-overlay">
      <view class="loading-content">
        <text class="loading-text">正在验证...</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { resetPassword } from '@/utils/api/auth.js';
import { onLoad } from '@dcloudio/uni-app';
import ResetPasswordForm from '@/components/Auth/ResetPasswordForm.vue';

// 表单数据
const formData = reactive({
  newPassword: '',
  confirmPassword: '',
  token: ''
});



// 错误信息
const errors = reactive({
  newPassword: '',
  confirmPassword: ''
});

// 令牌验证状态
const isTokenValid = ref(true);
const tokenErrorMessage = ref('');

// 状态管理
const isSubmitting = ref(false);
const isLoading = ref(false);

// 密码强度
const passwordStrength = reactive({
  level: 'none', // none, weak, medium, strong, very-strong
  width: '0%',
  text: ''
});

// 计算属性
const canSubmit = computed(() => {
  return formData.newPassword.trim() !== '' && 
         formData.confirmPassword.trim() !== '' && 
         formData.newPassword === formData.confirmPassword &&
         !isSubmitting.value &&
         passwordStrength.level !== 'weak';
});

// 验证密码强度
const validatePasswordStrength = (password) => {
  if (password.length < 8) {
    return { level: 'weak', width: '25%', text: '密码强度：弱' };
  }
  
  const hasLower = /[a-z]/.test(password);
  const hasUpper = /[A-Z]/.test(password);
  const hasNumber = /\d/.test(password);
  const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(password);
  
  let score = 0;
  if (hasLower) score++;
  if (hasUpper) score++;
  if (hasNumber) score++;
  if (hasSpecial) score++;
  if (password.length >= 12) score++;
  
  if (score <= 2) {
    return { level: 'weak', width: '25%', text: '密码强度：弱' };
  } else if (score === 3) {
    return { level: 'medium', width: '50%', text: '密码强度：中' };
  } else if (score === 4) {
    return { level: 'strong', width: '75%', text: '密码强度：强' };
  } else {
    return { level: 'very-strong', width: '100%', text: '密码强度：很强' };
  }
};

// 更新密码
const updatePassword = (value) => {
  formData.newPassword = value;
  // 清除错误信息
  errors.newPassword = '';
  // 更新密码强度
  Object.assign(passwordStrength, validatePasswordStrength(formData.newPassword));
};

// 更新确认密码
const updateConfirmPassword = (value) => {
  formData.confirmPassword = value;
  // 清除错误信息
  errors.confirmPassword = '';
  // 验证两次密码是否一致
  if (formData.newPassword && formData.confirmPassword && 
      formData.newPassword !== formData.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致';
  }
};

// 重置密码
const handleResetPassword = async () => {
  // 表单验证
  if (!formData.newPassword.trim()) {
    errors.newPassword = '请输入新密码';
    return;
  }

  if (!formData.confirmPassword.trim()) {
    errors.confirmPassword = '请确认新密码';
    return;
  }

  if (formData.newPassword !== formData.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致';
    return;
  }

  // 验证密码强度
  if (passwordStrength.level === 'weak') {
    errors.newPassword = '密码强度太弱，请使用更复杂的密码';
    return;
  }

  // 防止重复提交
  if (isSubmitting.value) {
    return;
  }

  isSubmitting.value = true;

  try {
    // 根据接口文档，只传递token和newPassword参数
    const response = await resetPassword({
      token: formData.token,
      newPassword: formData.newPassword
    });

    if (response.code === 200) {
      // 显示成功提示
      uni.showModal({
        title: '重置成功',
        content: '密码重置成功，3秒后自动跳转到登录页面',
        showCancel: false,
        success: () => {
          // 3秒后跳转到登录页面
          setTimeout(() => {
            uni.redirectTo({
              url: '/pages/auth/Login'
            });
          }, 3000);
        }
      });
    } else {
      // 根据错误码显示不同提示
      let errorMessage = response.message || '密码重置失败，请稍后重试';
      
      // 特定错误码处理
      if (response.code === 401) {
        isTokenValid.value = false;
        tokenErrorMessage.value = '重置链接已失效，请重新获取';
      } else if (response.code === 400) {
        errorMessage = '新密码不能与旧密码相同';
        errors.newPassword = errorMessage;
      }
      
      uni.showToast({
        title: errorMessage,
        icon: 'none',
        duration: 2000
      });
    }
  } catch (error) {
    console.error('重置密码失败:', error);
    uni.showToast({
      title: '网络错误，请稍后重试',
      icon: 'none',
      duration: 2000
    });
  } finally {
    isSubmitting.value = false;
  }
};

// 返回上一页
const goBack = () => {
  uni.navigateBack();
};

// 跳转到忘记密码页面
const goToForgotPassword = () => {
  uni.redirectTo({
    url: '/pages/auth/ForgotPassword'
  });
};

onLoad((options) => {
  formData.token = options.token || '';
});
</script>
<style lang="scss" scoped>
.reset-password-page {
  height: 100vh;
  background-color: #f8f8f8;
  position: relative;
  display: flex;
  flex-direction: column;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44px;
  padding: 0 16px;
  background-color: #fff;
  border-bottom: 1px solid #eee;
  flex-shrink: 0;
  
  .back-button {
    width: 30px;
    height: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .icon-back {
      font-size: 18px;
      color: #666;
    }
  }
  
  .header-title {
    font-size: 16px;
    font-weight: 500;
    color: #333;
  }
  
  .empty-space {
    width: 30px;
  }
}

.main-content {
  flex: 1;
  padding: 20px;
  box-sizing: border-box;
}

.container {
  min-height: calc(100vh - 44px - 40px);
  display: flex;
  flex-direction: column;
}

.title-section {
  text-align: center;
  margin-bottom: 40px;
  flex-shrink: 0;
  
  .title-icon {
    font-size: 48px;
    display: block;
    margin-bottom: 12px;
  }
  
  .title-text {
    font-size: 24px;
    font-weight: bold;
    color: #333;
    display: block;
    margin-bottom: 8px;
  }
  
  .subtitle {
    font-size: 14px;
    color: #999;
  }
}



.invalid-token-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  box-sizing: border-box;
  
  .error-content {
    text-align: center;
    max-width: 300px;
    
    .error-icon {
      font-size: 64px;
      display: block;
      margin-bottom: 20px;
    }
    
    .error-title {
      font-size: 20px;
      font-weight: bold;
      color: #333;
      display: block;
      margin-bottom: 10px;
    }
    
    .error-subtitle {
      font-size: 14px;
      color: #999;
      display: block;
      margin-bottom: 30px;
      line-height: 1.5;
    }
  }
  
  .retry-button {
    width: 100%;
    height: 48px;
    background-color: #ff6b6b;
    color: #fff;
    border-radius: 24px;
    font-size: 16px;
    font-weight: 500;
    border: none;
    
    &:hover {
      background-color: #ff5252;
    }
  }
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  
  .loading-content {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    gap: 10px;
    
    .loading-text {
      font-size: 16px;
      color: #333;
    }
  }
}
</style>