<template>
  <view class="settings-list">
    <view class="settings-group">
      <view class="group-title">功能设置</view>
      <view 
        v-for="item in settingItems" 
        :key="item.key" 
        class="setting-item" 
        @click="handleItemClick(item)"
      >
        <text class="item-icon">{{ item.icon }}</text>
        <text class="item-text">{{ item.title }}</text>
        <text class="arrow">›</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';

const settingItems = ref([
  {
    key: 'category',
    title: '分类管理',
    icon: '🏷️',
    action: 'navigateToCategory'
  },
  {
    key: 'backup',
    title: '数据备份与恢复',
    icon: '💾',
    action: 'navigateToBackup'
  },
  {
    key: 'about',
    title: '关于我们',
    icon: 'ℹ️',
    action: 'navigateToAbout'
  },
  {
    key: 'logout',
    title: '退出登录',
    icon: '🚪',
    action: 'handleLogout'
  }
]);

const emit = defineEmits(['itemClick']);

const handleItemClick = (item) => {
  emit('itemClick', item);
};
</script>

<style lang="scss" scoped>
.settings-list {
  background-color: white;
  border-radius: 20rpx;
  padding: 20rpx;
  margin: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .settings-group {
    .group-title {
      font-size: 32rpx;
      font-weight: bold;
      margin: 20rpx 0;
      color: #333;
      padding-left: 20rpx;
    }

    .setting-item {
      display: flex;
      align-items: center;
      padding: 25rpx;
      border-radius: 12rpx;
      margin-bottom: 10rpx;
      background-color: #fafafa;

      &:last-child {
        margin-bottom: 0;
      }

      .item-icon {
        font-size: 36rpx;
        margin-right: 20rpx;
        width: 40rpx;
      }

      .item-text {
        flex: 1;
        font-size: 30rpx;
        color: #333;
      }

      .arrow {
        font-size: 32rpx;
        color: #ccc;
      }
    }
  }
}
</style>