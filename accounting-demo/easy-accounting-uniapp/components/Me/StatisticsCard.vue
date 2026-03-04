<template>
  <view class="statistics-card">
    <view class="card-title">记账统计</view>
    <view class="stats-grid">
      <view class="stat-item">
        <view class="stat-value">{{ statistics.totalDays || 0 }}</view>
        <view class="stat-label">累计记账天数</view>
      </view>
      <view class="stat-item">
        <view class="stat-value">{{ statistics.totalRecords || 0 }}</view>
        <view class="stat-label">记账总笔数</view>
      </view>
      <view class="stat-item">
        <view class="stat-value">{{ statistics.checkInDays || 0 }}</view>
        <view class="stat-label">打卡天数</view>
      </view>
      <view class="stat-item">
        <view class="stat-value">{{ statistics.continuousDays || 0 }}</view>
        <view class="stat-label">连续打卡</view>
      </view>
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  statistics: {
    type: Object,
    default: () => ({
      totalDays: 0,
      totalRecords: 0,
      checkInDays: 0,
      continuousDays: 0
    })
  }
});
</script>

<style lang="scss" scoped>
.statistics-card {
  background-color: white;
  border-radius: 20rpx;
  padding: 30rpx;
  margin: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

  .card-title {
    font-size: 32rpx;
    font-weight: bold;
    margin-bottom: 30rpx;
    color: #333;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 30rpx;

    .stat-item {
      text-align: center;

      .stat-value {
        font-size: 40rpx;
        font-weight: bold;
        color: #ff6b6b;
        margin-bottom: 10rpx;
      }

      .stat-label {
        font-size: 26rpx;
        color: #999;
      }
    }
  }
}
</style>