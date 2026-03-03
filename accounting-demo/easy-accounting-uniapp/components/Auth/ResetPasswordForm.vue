<template>
  <view class="form-container">
    <!-- 新密码输入框 -->
    <view class="input-group">
      <view class="input-with-toggle">
        <input 
          :modelValue="formData.newPassword"
          @input="onNewPasswordInput"
          class="password-input"
          :class="{ 'input-error': !!errors.newPassword }"
          :type="showNewPassword ? 'text' : 'password'"
          placeholder="请输入新密码"
          maxlength="20"
        />
        <view class="password-toggle" @click="toggleShowNewPassword">
          <text class="iconfont">{{ showNewPassword ? '👁️' : '👁️‍🗨️' }}</text>
        </view>
      </view>
      <text v-if="errors.newPassword" class="error-message">{{ errors.newPassword }}</text>
      
      <!-- 密码强度指示器 -->
      <view v-if="formData.newPassword" class="password-strength">
        <view class="strength-container">
          <view 
            class="strength-bar"
            :class="`strength-${passwordStrength.level}`"
            :style="{ width: passwordStrength.width }"
          ></view>
        </view>
        <text class="strength-text">{{ passwordStrength.text }}</text>
      </view>
    </view>

    <!-- 确认密码输入框 -->
    <view class="input-group">
      <view class="input-with-toggle">
        <input 
          :modelValue="formData.confirmPassword"
          @input="onConfirmPasswordInput"
          class="password-input"
          :class="{ 'input-error': !!errors.confirmPassword }"
          :type="showConfirmPassword ? 'text' : 'password'"
          placeholder="请再次输入新密码"
          maxlength="20"
        />
        <view class="password-toggle" @click="toggleShowConfirmPassword">
          <text class="iconfont">{{ showConfirmPassword ? '👁️' : '👁️‍🗨️' }}</text>
        </view>
      </view>
      <text v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</text>
    </view>

    <!-- 重置密码按钮 -->
    <button 
      class="reset-button" 
      :class="{ 'button-disabled': !canSubmit || isSubmitting }"
      :disabled="!canSubmit || isSubmitting"
      @click="onSubmit"
    >
      <text v-if="!isSubmitting">重置密码</text>
      <text v-else>重置中...</text>
    </button>
  </view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';

// 定义props
const props = defineProps({
  formData: {
    type: Object,
    required: true
  },
  errors: {
    type: Object,
    required: true
  },
  isSubmitting: {
    type: Boolean,
    default: false
  },
  passwordStrength: {
    type: Object,
    required: true
  },
  canSubmit: {
    type: Boolean,
    required: true
  }
});

// 定义emit
const emit = defineEmits(['update:formData', 'submit', 'update-password', 'update-confirm-password']);

// 显示控制
const showNewPassword = ref(false);
const showConfirmPassword = ref(false);

// 切换新密码显示
const toggleShowNewPassword = () => {
  showNewPassword.value = !showNewPassword.value;
};

// 切换确认密码显示
const toggleShowConfirmPassword = () => {
  showConfirmPassword.value = !showConfirmPassword.value;
};

// 新密码输入处理
const onNewPasswordInput = (event) => {
  const value = event.detail.value;
  emit('update-password', value);
};

// 确认密码输入处理
const onConfirmPasswordInput = (event) => {
  const value = event.detail.value;
  emit('update-confirm-password', value);
};

// 提交处理
const onSubmit = () => {
  emit('submit');
};
</script>

<style lang="scss" scoped>
.form-container {
  background-color: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  flex-shrink: 0;
  margin-bottom: auto;
}

.input-group {
  margin-bottom: 20px;
  
  .input-with-toggle {
    position: relative;
    display: flex;
    align-items: center;
  }
  
  .password-input {
    flex: 1;
    height: 48px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    padding: 0 48px 0 16px;
    font-size: 16px;
    transition: border-color 0.3s ease;
    box-sizing: border-box;
    
    &:focus {
      border-color: #ff6b6b;
    }
  }
  
  .input-error {
    border-color: #ff4757 !important;
  }
  
  .password-toggle {
    position: absolute;
    right: 16px;
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .iconfont {
      font-size: 16px;
      color: #999;
    }
  }
  
  .error-message {
    display: block;
    color: #ff4757;
    font-size: 12px;
    margin-top: 6px;
    margin-left: 2px;
  }
  
  .password-strength {
    margin-top: 10px;
    display: flex;
    align-items: center;
    gap: 10px;
    
    .strength-container {
      flex: 1;
      height: 4px;
      border-radius: 2px;
      background-color: #e0e0e0;
      overflow: hidden;
    }
    
    .strength-bar {
      height: 100%;
      width: 0%;
      border-radius: 2px;
      transition: width 0.3s ease, background-color 0.3s ease;
      
      &.strength-none,
      &.strength-weak {
        background-color: #ff4757;
      }
      
      &.strength-medium {
        background-color: #ffa502;
      }
      
      &.strength-strong {
        background-color: #2ed573;
      }
      
      &.strength-very-strong {
        background-color: #1e90ff;
      }
    }
    
    .strength-text {
      font-size: 12px;
      color: #666;
      white-space: nowrap;
    }
  }
}

.reset-button {
  width: 100%;
  height: 48px;
  background-color: #ff6b6b;
  color: #fff;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 500;
  border: none;
  transition: background-color 0.3s ease;
  box-sizing: border-box;
  
  &:hover {
    background-color: #ff5252;
  }
  
  &.button-disabled {
    background-color: #cccccc;
    color: #999999;
  }
}
</style>