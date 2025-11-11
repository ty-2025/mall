<template>
  <view class="checkin-history-page">
    <!-- 头部统计 -->
    <view class="stats-section">
      <view class="stats-card">
        <view class="stat-item">
          <text class="stat-number">{{ totalCheckins }}</text>
          <text class="stat-label">累计签到</text>
        </view>
        <view class="stat-item">
          <text class="stat-number">{{ totalIntegration }}</text>
          <text class="stat-label">累计积分</text>
        </view>
        <view class="stat-item">
          <text class="stat-number">{{ continuousDays }}</text>
          <text class="stat-label">连续签到</text>
        </view>
      </view>
    </view>

    <!-- 筛选区 -->
    <view class="filter-section">
      <view class="filter-card">
        <view class="date-pickers">
          <view class="date-picker-item">
            <text class="label">开始日期</text>
            <view class="date-input" @click="showStartDatePicker = true">
              <text>{{ startDateText || '选择开始日期' }}</text>
              <text class="icon">▼</text>
            </view>
          </view>
          <view class="date-picker-item">
            <text class="label">结束日期</text>
            <view class="date-input" @click="showEndDatePicker = true">
              <text>{{ endDateText || '选择结束日期' }}</text>
              <text class="icon">▼</text>
            </view>
          </view>
        </view>
        <button class="search-btn" @click="handleSearch">查询</button>
      </view>
    </view>

    <!-- 列表区 -->
    <view class="list-section">
      <view class="list-card">
        <view class="record-list">
          <view 
            v-for="record in recordList" 
            :key="record.id" 
            class="record-item"
          >
            <view class="record-left">
              <text class="record-date">{{ formatDate(record.checkinDate) }}</text>
              <text class="record-days">连续签到 {{ record.continuousDays }} 天</text>
            </view>
            <view class="record-right">
              <text class="record-points">+{{ record.integration }}积分</text>
            </view>
          </view>
        </view>
        
        <!-- 加载状态 -->
        <view v-if="loading" class="loading-text">
          <text>加载中...</text>
        </view>
        <view v-if="finished && recordList.length > 0" class="finished-text">
          <text>没有更多记录了</text>
        </view>
        <view v-if="recordList.length === 0 && !loading" class="empty-text">
          <text>暂无签到记录</text>
          <button class="empty-btn" @click="goToCheckin">立即签到</button>
        </view>
      </view>
    </view>

    <!-- 日期选择器 -->
    <view v-if="showStartDatePicker" class="date-picker-mask" @click="showStartDatePicker = false">
      <view class="date-picker-content" @click.stop>
        <picker 
          mode="date" 
          :value="startDateText" 
          @change="onStartDateChange"
          :start="minDate"
          :end="maxDate"
        >
          <view class="date-picker">选择开始日期</view>
        </picker>
      </view>
    </view>

    <view v-if="showEndDatePicker" class="date-picker-mask" @click="showEndDatePicker = false">
      <view class="date-picker-content" @click.stop>
        <picker 
          mode="date" 
          :value="endDateText" 
          @change="onEndDateChange"
          :start="minDate"
          :end="maxDate"
        >
          <view class="date-picker">选择结束日期</view>
        </picker>
      </view>
    </view>
  </view>
</template>

<script>
import { getCheckinHistory, getContinuousDays } from '@/api/checkin.js';
import { formatDate } from '@/utils/date.js';

export default {
  data() {
    const now = new Date();
    const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
    const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);
    
    return {
      recordList: [],
      loading: false,
      finished: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      
      // 统计数据
      totalCheckins: 0,
      totalIntegration: 0,
      continuousDays: 0,
      
      // 筛选条件
      startDate: startOfMonth,
      endDate: endOfMonth,
      showStartDatePicker: false,
      showEndDatePicker: false,
      minDate: '2020-01-01',
      maxDate: '2025-12-31'
    };
  },
  computed: {
    startDateText() {
      return this.formatDateForInput(this.startDate);
    },
    endDateText() {
      return this.formatDateForInput(this.endDate);
    }
  },
  onLoad() {
    this.loadRecords();
    this.loadStats();
  },
  onReachBottom() {
    if (!this.finished && !this.loading) {
      this.pageNum++;
      this.loadRecords();
    }
  },
  onPullDownRefresh() {
    this.pageNum = 1;
    this.recordList = [];
    this.finished = false;
    Promise.all([this.loadRecords(), this.loadStats()]).finally(() => {
      uni.stopPullDownRefresh();
    });
  },
  methods: {
    async loadRecords() {
      if (this.loading || this.finished) return;
      
      this.loading = true;
      try {
        const res = await getCheckinHistory(
          this.startDate, 
          this.endDate, 
          this.pageNum, 
          this.pageSize
        );
        
        console.log('签到记录响应:', res);
        
        if (res.code === 200) {
          const data = res.data.list || res.data;
          if (Array.isArray(data)) {
            if (this.pageNum === 1) {
              this.recordList = data;
            } else {
              this.recordList = [...this.recordList, ...data];
            }
            
            this.total = res.data.total || data.length;
            
            // 如果返回的数据少于请求的数量，说明没有更多数据了
            if (data.length < this.pageSize) {
              this.finished = true;
            }
          } else {
            this.finished = true;
          }
        } else {
          uni.showToast({
            title: res.message || '加载失败',
            icon: 'none'
          });
          this.finished = true;
        }
      } catch (error) {
        console.error('加载签到记录失败:', error);
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        });
        this.finished = true;
      }
      this.loading = false;
    },
    
    async loadStats() {
      try {
        // 加载连续签到天数
        const continuousRes = await getContinuousDays();
        if (continuousRes.code === 200) {
          this.continuousDays = continuousRes.data || 0;
        }
        
        // 计算累计签到和积分
        this.calculateTotalStats();
      } catch (error) {
        console.error('加载统计数据失败:', error);
      }
    },
    
    calculateTotalStats() {
      // 从记录列表中计算累计数据
      this.totalCheckins = this.recordList.length;
      this.totalIntegration = this.recordList.reduce((total, record) => {
        return total + (record.integration || 0);
      }, 0);
    },
    
    handleSearch() {
      this.pageNum = 1;
      this.recordList = [];
      this.finished = false;
      this.loadRecords();
    },
    
    onStartDateChange(e) {
      this.startDate = new Date(e.detail.value);
      this.showStartDatePicker = false;
    },
    
    onEndDateChange(e) {
      this.endDate = new Date(e.detail.value);
      this.showEndDatePicker = false;
    },
    
    goToCheckin() {
      uni.navigateTo({
        url: '/pages/user/checkin'
      });
    },
    
    formatDate(date) {
      return formatDate(new Date(date), 'yyyy年MM月dd日');
    },
    
    formatDateForInput(date) {
      return formatDate(date, 'yyyy-MM-dd');
    }
  },
  watch: {
    recordList() {
      this.calculateTotalStats();
    }
  }
};
</script>

<style scoped>
.checkin-history-page {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 统计区域 */
.stats-section {
  padding: 30rpx;
}

.stats-card {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 20rpx;
  padding: 40rpx;
  display: flex;
  justify-content: space-around;
  color: white;
  box-shadow: 0 10rpx 30rpx rgba(102, 126, 234, 0.3);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-number {
  font-size: 48rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.stat-label {
  font-size: 24rpx;
  opacity: 0.9;
}

/* 其他样式保持不变 */
.filter-section {
  padding: 0 30rpx 30rpx;
}

.filter-card {
  background: white;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.1);
}

.date-pickers {
  display: flex;
  gap: 20rpx;
  margin-bottom: 30rpx;
}

.date-picker-item {
  flex: 1;
}

.date-picker-item .label {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 10rpx;
  display: block;
}

.date-input {
  border: 1px solid #ddd;
  border-radius: 8rpx;
  padding: 20rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 28rpx;
}

.date-input .icon {
  color: #999;
  font-size: 24rpx;
}

.search-btn {
  border-radius: 16rpx;
  height: 88rpx;
  background: #007aff;
  color: white;
  font-size: 28rpx;
  line-height: 88rpx;
  border: none;
}

.list-section {
  padding: 0 30rpx 30rpx;
}

.list-card {
  background: white;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.1);
}

.record-list {
  padding: 0;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1px solid #f0f0f0;
}

.record-item:last-child {
  border-bottom: none;
}

.record-left {
  flex: 1;
}

.record-date {
  font-size: 32rpx;
  color: #333;
  margin-bottom: 8rpx;
  display: block;
}

.record-days {
  font-size: 28rpx;
  color: #666;
  display: block;
}

.record-right {
  text-align: right;
}

.record-points {
  font-size: 36rpx;
  color: #ff6b35;
  font-weight: bold;
}

.loading-text, .finished-text, .empty-text {
  text-align: center;
  padding: 60rpx 30rpx;
  color: #999;
  font-size: 28rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30rpx;
}

.empty-btn {
  background: linear-gradient(135deg, #ff6b35, #ff8e53);
  color: white;
  border: none;
  border-radius: 50rpx;
  padding: 20rpx 60rpx;
  font-size: 28rpx;
}

.date-picker-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: flex-end;
  z-index: 999;
}

.date-picker-content {
  width: 100%;
  background: white;
  padding: 30rpx;
}

.date-picker {
  padding: 30rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  text-align: center;
}
</style>