<template>
  <view class="me-page">
    <!-- 个人信息区域 -->
    <UserInfoCard :user-info="userInfo" />

    <!-- 记账统计数据 -->
    <StatisticsCard :statistics="statistics" />

    <!-- 设置功能入口 -->
    <SettingsList @item-click="handleSettingItemClick" />
    
    <!-- 加载遮罩 -->
    <view v-if="loading" class="loading-overlay">
      <view class="loading-content">
        <text class="loading-text">加载中...</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { getCurrentUser, getUserStats } from '@/utils/api/user.js';
import UserInfoCard from '@/components/Me/UserInfoCard.vue';
import StatisticsCard from '@/components/Me/StatisticsCard.vue';
import SettingsList from '@/components/Me/SettingsList.vue';

// 用户信息
const userInfo = ref({
  id: null,
  avatarUrl: '/static/images/default-avatar.png',
  nickname: '',
  phone: '',
  createdAt: ''
});

// 记账统计数据
const statistics = ref({
  totalDays: 0,
  totalRecords: 0,
  continuousDays: 0
});

// 加载状态
const loading = ref(true);

// 设置项点击处理
const handleSettingItemClick = (item) => {
  switch (item.key) {
    case 'category':
      // 分类管理
      uni.navigateTo({
        url: '/pages/tabBar/me/Category'
      });
      break;
    case 'backup':
      // 数据备份与恢复
      uni.navigateTo({
        url: '/pages/tabBar/me/Backup'
      });
      break;
    case 'about':
      // 关于我们
      uni.navigateTo({
        url: '/pages/tabBar/me/About'
      });
      break;
    case 'logout':
      // 退出登录
      handleLogout();
      break;
    default:
      break;
  }
};

// 退出登录处理
const handleLogout = () => {
  uni.showModal({
    title: '确认退出',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        // 这里应该调用实际的退出登录接口
        console.log('用户退出登录');
        // 跳转到登录页面
        uni.redirectTo({
          url: '/pages/auth/Login'
        });
      }
    }
  });
};

// 加载用户信息和统计数据
const loadUserData = async () => {
  try {
    // 并行加载用户信息和统计数据
    const [userResponse, statsResponse] = await Promise.all([
      getCurrentUser(),
      getUserStats()
    ]);

    if (userResponse.code === 200) {
      const userData = userResponse.data;
      userInfo.value.id = userData.id;
      userInfo.value.avatarUrl = userData.avatarUrl || '/static/images/default-avatar.png';
      userInfo.value.nickname = userData.nickname || '未设置昵称';
      userInfo.value.phone = userData.phone || '未绑定手机号';
      userInfo.value.createdAt = userData.createdAt || '';
    } else {
      console.error('获取用户信息失败:', userResponse.message);
      // 设置默认值
      userInfo.value.nickname = '未设置昵称';
      userInfo.value.phone = '未绑定手机号';
    }

    if (statsResponse.code === 200) {
      const statsData = statsResponse.data;
      statistics.value.totalDays = statsData.totalDays || 0;
      statistics.value.totalRecords = statsData.totalRecords || 0;
      statistics.value.continuousDays = statsData.continuousDays || 0;
    } else {
      console.error('获取统计数据失败:', statsResponse.message);
      // 设置默认值
      statistics.value.totalDays = 0;
      statistics.value.totalRecords = 0;
      statistics.value.continuousDays = 0;
    }
  } catch (error) {
    console.error('加载用户数据失败:', error);
    uni.showToast({
      title: '数据加载失败',
      icon: 'none'
    });
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  // 初始加载，页面挂载时执行一次
  loadUserData();
});

onShow(async () => {
  // 每次页面显示时都重新加载数据
  await loadUserData();
});
</script>

<style lang="scss" scoped>
.me-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 100rpx;
  position: relative;
}

.loading-overlay {
  position: fixed;
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
    padding: 40rpx;
    border-radius: 20rpx;
    display: flex;
    align-items: center;
    box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.2);

    .loading-text {
      font-size: 32rpx;
      color: #333;
    }
  }
}
</style>