<template>
  <view class="checkin-page">
    <!-- 状态区 -->
    <view class="status-section">
      <view class="status-card">
        <view class="status-header">
          <text class="title">每日签到</text>
          <text class="subtitle">签到领积分</text>
        </view>
        
        <view v-if="todayChecked" class="checked-status">
          <view class="checked-icon">
            <text class="icon">✓</text>
          </view>
          <view class="status-text">今日已签到</view>
          <view class="reward-info">获得 {{ todayIntegration }} 积分</view>
        </view>
        
        <view v-else class="checkin-status">
          <view class="checkin-icon">
            <text class="icon">🎁</text>
          </view>
          <button class="checkin-btn" @click="handleCheckin">立即签到</button>
        </view>
        
        <view class="continuous-days">
          <text class="days-number">{{ continuousDays }}</text>
          <text class="days-text">当前连续签到天数</text>
        </view>
      </view>
    </view>

    <!-- 奖励区 -->
    <view class="reward-section">
      <view class="reward-card">
        <view class="section-title">签到规则</view>
        <view class="reward-rules">
          <view class="rule-item" v-for="rule in rewardRules" :key="rule.days">
            <view class="rule-left">
              <text class="rule-days">{{ rule.days }}</text>
              <text class="rule-desc">{{ rule.desc }}</text>
            </view>
            <text class="rule-points">{{ rule.points }}积分</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 记录入口 -->
    <view class="history-section">
      <view class="history-card" @click="goToHistory">
        <text class="history-text">查看签到记录</text>
        <text class="history-icon">›</text>
      </view>
    </view>

    <!-- 签到日历 -->
    <view class="calendar-section">
      <view class="calendar-card">
        <view class="section-title">签到日历</view>
        <view class="calendar">
          <view class="calendar-header">
            <view class="week-day" v-for="day in weekDays" :key="day">{{ day }}</view>
          </view>
          <view class="calendar-body">
            <view 
              v-for="date in calendarDates" 
              :key="date.date"
              :class="['date-cell', {
                'current-month': date.isCurrentMonth,
                'today': date.isToday,
                'checked': date.checked
              }]"
            >
              <text class="date-number">{{ date.day }}</text>
              <text v-if="date.checked" class="check-indicator">✓</text>
              <text v-if="date.isToday && !date.checked" class="today-indicator">今</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { checkin, getContinuousDays, getTodayStatus, getCheckinHistory } from '@/api/checkin.js';

export default {
  data() {
    return {
      todayChecked: false,
      continuousDays: 0,
      todayIntegration: 0,
      weekDays: ['日', '一', '二', '三', '四', '五', '六'],
      calendarDates: [],
      checkinRecords: [],
      rewardRules: [
        { days: '1-6天', desc: '连续签到', points: 10 },
        { days: '7-14天', desc: '连续签到', points: 20 },
        { days: '≥15天', desc: '连续签到', points: 30 }
      ]
    };
  },
  onLoad() {
    this.loadCheckinData();
  },
  onShow() {
    // 页面显示时重新加载数据
    this.loadCheckinData();
  },
  methods: {
    async loadCheckinData() {
      try {
        const [todayRes, continuousRes] = await Promise.all([
          getTodayStatus(),
          getContinuousDays()
        ]);
        
        this.todayChecked = todayRes.data;
        this.continuousDays = continuousRes.data || 0;
        
        // 加载签到记录生成日历
        await this.loadCheckinHistory();
        this.generateCalendar();
      } catch (error) {
        console.error('加载签到数据失败:', error);
      }
    },
    
    async handleCheckin() {
      try {
        uni.showLoading({
          title: '签到中...',
          mask: true
        });
        
        const res = await checkin();
        
        if (res.code === 200) {
          this.todayChecked = true;
          this.continuousDays = res.data.continuousDays;
          this.todayIntegration = res.data.integration;
          
          uni.showToast({
            title: `签到成功！获得${res.data.integration}积分`,
            icon: 'success',
            duration: 2000
          });
          
          // 重新加载数据更新日历
          await this.loadCheckinHistory();
          this.generateCalendar();
        } else {
          uni.showToast({
            title: res.message || '签到失败',
            icon: 'none'
          });
        }
      } catch (error) {
        uni.showToast({
          title: '签到失败，请重试',
          icon: 'none'
        });
        console.error('签到失败:', error);
      } finally {
        uni.hideLoading();
      }
    },
    
    goToHistory() {
      uni.navigateTo({
        url: '/pages/user/checkin-history'
      });
    },
    
    generateCalendar() {
      const now = new Date();
      const year = now.getFullYear();
      const month = now.getMonth();
      const today = now.getDate();
      
      // 获取当月第一天
      const firstDay = new Date(year, month, 1);
      const lastDay = new Date(year, month + 1, 0);
      const firstDayWeek = firstDay.getDay();
      
      const dates = [];
      
      // 添加上个月的日子
      const prevMonthLastDay = new Date(year, month, 0).getDate();
      for (let i = 0; i < firstDayWeek; i++) {
        dates.push({
          day: prevMonthLastDay - firstDayWeek + i + 1,
          isCurrentMonth: false,
          isToday: false,
          checked: false
        });
      }
      
      // 添加当月的日子
      for (let i = 1; i <= lastDay.getDate(); i++) {
        dates.push({
          day: i,
          isCurrentMonth: true,
          isToday: i === today,
          checked: this.isDateChecked(year, month, i)
        });
      }
      
      // 添加下个月的日子补全日历
      const totalCells = 42;
      const nextMonthDays = totalCells - dates.length;
      for (let i = 1; i <= nextMonthDays; i++) {
        dates.push({
          day: i,
          isCurrentMonth: false,
          isToday: false,
          checked: false
        });
      }
      
      this.calendarDates = dates;
    },
    
    isDateChecked(year, month, day) {
      const dateStr = `${year}-${(month + 1).toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
      return this.checkinRecords.some(record => {
        const recordDate = new Date(record.checkinDate);
        const recordDateStr = `${recordDate.getFullYear()}-${(recordDate.getMonth() + 1).toString().padStart(2, '0')}-${recordDate.getDate().toString().padStart(2, '0')}`;
        return recordDateStr === dateStr;
      });
    },
    
    async loadCheckinHistory() {
      try {
        const now = new Date();
        const startDate = new Date(now.getFullYear(), now.getMonth(), 1);
        const endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);
        
        const res = await getCheckinHistory(startDate, endDate, 1, 31);
        if (res.code === 200) {
          this.checkinRecords = res.data.list || res.data;
        }
      } catch (error) {
        console.error('加载签到记录失败:', error);
      }
    }
  }
};
</script>

<style scoped>
.checkin-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 30rpx;
}

.status-section {
  margin-bottom: 30rpx;
}

.status-card {
  background: white;
  border-radius: 30rpx;
  padding: 40rpx;
  text-align: center;
  box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.1);
}

.status-header {
  margin-bottom: 40rpx;
}

.status-header .title {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.status-header .subtitle {
  font-size: 28rpx;
  color: #666;
}

.checked-status,
.checkin-status {
  margin-bottom: 40rpx;
}

.checked-icon,
.checkin-icon {
  margin-bottom: 20rpx;
}

.checked-icon .icon {
  font-size: 120rpx;
  color: #07c160;
}

.checkin-icon .icon {
  font-size: 100rpx;
}

.status-text {
  font-size: 32rpx;
  color: #07c160;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.reward-info {
  font-size: 28rpx;
  color: #ff6b35;
}

.checkin-btn {
  width: 400rpx;
  height: 100rpx;
  border-radius: 50rpx;
  background: linear-gradient(135deg, #ff6b35, #ff8e53);
  border: none;
  font-size: 36rpx;
  font-weight: bold;
  color: white;
  line-height: 100rpx;
  box-shadow: 0 10rpx 20rpx rgba(255, 107, 53, 0.3);
}

.continuous-days {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 20rpx;
  padding: 30rpx;
  color: white;
}

.days-number {
  display: block;
  font-size: 60rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.days-text {
  font-size: 28rpx;
  opacity: 0.9;
}

.reward-section {
  margin-bottom: 30rpx;
}

.reward-card {
  background: white;
  border-radius: 30rpx;
  padding: 40rpx;
  box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.1);
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 30rpx;
  color: #333;
  border-left: 8rpx solid #667eea;
  padding-left: 20rpx;
}

.reward-rules {
  display: flex;
  flex-direction: column;
  gap: 30rpx;
}

.rule-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.rule-item:last-child {
  border-bottom: none;
}

.rule-left {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.rule-days {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
  min-width: 120rpx;
}

.rule-desc {
  font-size: 26rpx;
  color: #666;
}

.rule-points {
  font-size: 32rpx;
  color: #ff6b35;
  font-weight: bold;
}

.history-section {
  margin-bottom: 30rpx;
}

.history-card {
  background: white;
  border-radius: 30rpx;
  padding: 30rpx 40rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.1);
}

.history-text {
  font-size: 32rpx;
  color: #333;
}

.history-icon {
  font-size: 40rpx;
  color: #999;
}

.calendar-section {
  margin-bottom: 30rpx;
}

.calendar-card {
  background: white;
  border-radius: 30rpx;
  padding: 40rpx;
  box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.1);
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  margin-bottom: 30rpx;
}

.week-day {
  font-size: 28rpx;
  color: #666;
  padding: 15rpx 0;
  font-weight: bold;
}

.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 15rpx;
}

.date-cell {
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 15rpx;
  position: relative;
  font-size: 24rpx;
  transition: all 0.3s;
}

.date-cell.current-month {
  background: #f8f9fa;
}

.date-cell.today {
  background: #e6f7ff;
  border: 2rpx solid #1890ff;
}

.date-cell.checked {
  background: #f6ffed;
  border: 2rpx solid #52c41a;
}

.date-number {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.date-cell:not(.current-month) .date-number {
  color: #ccc;
}

.check-indicator {
  position: absolute;
  bottom: 8rpx;
  font-size: 20rpx;
  color: #52c41a;
  font-weight: bold;
}

.today-indicator {
  position: absolute;
  bottom: 8rpx;
  font-size: 20rpx;
  color: #1890ff;
  font-weight: bold;
}
</style>