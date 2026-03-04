<template>
  <view class="user-info-card">
    <view class="user-avatar">
      <image :src="userInfo.avatarUrl || defaultAvatar" class="avatar-image" @click="changeAvatar"></image>
    </view>
    <view class="user-details">
      <view class="user-name" @click="editNickname">
        {{ userInfo.nickname || '未设置昵称' }}
        <text class="edit-icon">✎</text>
      </view>
      <view class="user-phone">{{ userInfo.phone || '未绑定手机号' }}</view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({
      avatarUrl: '',
      nickname: '',
      phone: ''
    })
  }
});

const defaultAvatar = '/static/images/default-avatar.png';

const changeAvatar = () => {
  // 更换头像逻辑
  console.log('Change avatar');
};

const editNickname = () => {
  // 编辑昵称逻辑
  console.log('Edit nickname');
};
</script>

<style lang="scss" scoped>
.user-info-card {
  display: flex;
  align-items: center;
  padding: 30rpx;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border-radius: 0 0 30rpx 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);

  .user-avatar {
    margin-right: 20rpx;

    .avatar-image {
      width: 120rpx;
      height: 120rpx;
      border-radius: 50%;
      border: 4rpx solid rgba(255, 255, 255, 0.3);
    }
  }

  .user-details {
    flex: 1;

    .user-name {
      font-size: 36rpx;
      font-weight: bold;
      margin-bottom: 10rpx;
      display: flex;
      align-items: center;

      .edit-icon {
        margin-left: 10rpx;
        font-size: 24rpx;
      }
    }

    .user-phone {
      font-size: 28rpx;
      opacity: 0.9;
    }
  }
}
</style>