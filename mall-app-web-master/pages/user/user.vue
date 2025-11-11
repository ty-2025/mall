<template>  
    <view class="container">  
		
		<view class="user-section">
			<image class="bg" src="/static/user-bg.jpg"></image>
			<view class="user-info-box">
				<view class="portrait-box">
					<image class="portrait" :src="userInfo.icon || '/static/missing-face.png'"></image>
				</view>
				<view class="info-box">
					<text class="username">{{userInfo.nickname || '游客'}}</text>
				</view>
			</view>
			<view class="vip-card-box">
				<image class="card-bg" src="/static/vip-card-bg.png" mode=""></image>
				<view class="b-btn">
					立即开通
				</view>
				<view class="tit">
					<text class="yticon icon-iLinkapp-"></text>
					黄金会员
				</view>
				<text class="e-m">mall移动端商城</text>
				<text class="e-b">黄金及以上会员可享有会员价优惠商品。</text>
			</view>
		</view>
		
		<view 
			class="cover-container"
			:style="[{
				transform: coverTransform,
				transition: coverTransition
			}]"
			@touchstart="coverTouchstart"
			@touchmove="coverTouchmove"
			@touchend="coverTouchend"
		>
			<image class="arc" src="/static/arc.png"></image>
			
			<!-- 积分和签到区域 -->
			<view class="tj-sction">
				<view class="tj-item">
					 <text class="num">{{ todayChecked ? '5080' : (userInfo.integration || '5060') }}</text>
					<text>积分</text>
				</view>
				<view class="tj-item">
					<text class="num">{{userInfo.growth || '暂无'}}</text>
					<text>成长值</text>
				</view>
				<view class="tj-item" @click="navTo('/pages/coupon/couponList')">
					<text class="num">{{couponCount || '暂无'}}</text>
					<text>优惠券</text>
				</view>
			</view>

			<!-- 签到功能区 -->
			<view class="checkin-section">
				<view class="checkin-header">
					<text class="checkin-title">签到领积分</text>
					<text class="checkin-record" @click="navTo('/pages/user/checkin-history')">签到记录</text>
				</view>
				<view class="checkin-content">
					<view class="checkin-status">
						<text class="status-desc">连续签到 {{ continuousDays }} 天</text>
						<button v-if="!todayChecked" class="checkin-btn" @click="handleCheckin">立即签到</button>
						<view v-else class="checked-status">
							<text class="checked-text">今日已签到</text>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 订单 -->
			<view class="order-section">
				<view class="order-item" @click="navTo('/pages/order/order?state=0')" hover-class="common-hover"  :hover-stay-time="50">
					<text class="yticon icon-shouye"></text>
					<text>全部订单</text>
				</view>
				<view class="order-item" @click="navTo('/pages/order/order?state=1')"  hover-class="common-hover" :hover-stay-time="50">
					<text class="yticon icon-daifukuan"></text>
					<text>待付款</text>
				</view>
				<view class="order-item" @click="navTo('/pages/order/order?state=2')" hover-class="common-hover"  :hover-stay-time="50">
					<text class="yticon icon-yishouhuo"></text>
					<text>待收货</text>
				</view>
				<view class="order-item" hover-class="common-hover"  :hover-stay-time="50">
					<text class="yticon icon-shouhoutuikuan"></text>
					<text>退款/售后</text>
				</view>
			</view>
			
			<!-- 浏览历史 -->
			<view class="history-section icon">
				<list-cell icon="icon-dizhi" iconColor="#5fcda2" title="地址管理" @eventClick="navTo('/pages/address/address')"></list-cell>
				<list-cell icon="icon-lishijilu" iconColor="#e07472" title="我的足迹" @eventClick="navTo('/pages/user/readHistory')"></list-cell>
				<list-cell icon="icon-shoucang" iconColor="#5fcda2" title="我的关注" @eventClick="navTo('/pages/user/brandAttention')"></list-cell>
				<list-cell icon="icon-shoucang_xuanzhongzhuangtai" iconColor="#54b4ef" title="我的收藏" @eventClick="navTo('/pages/user/productCollection')"></list-cell>
				<list-cell icon="icon-pingjia" iconColor="#ee883b" title="我的评价"></list-cell>
				<list-cell icon="icon-shezhi1" iconColor="#e07472" title="设置" border="" @eventClick="navTo('/pages/set/set')"></list-cell>
			</view>
		</view>
    </view>  
</template>  

<script>  
import listCell from '@/components/mix-list-cell';
import {
  fetchMemberCouponList
} from '@/api/coupon.js';
import { checkin, getContinuousDays, getTodayStatus } from '@/api/checkin.js';
import {  
    mapState 
} from 'vuex';  

let startY = 0, moveY = 0, pageAtTop = true;
export default {
  components: {
    listCell
  },
  data(){
    return {
      coverTransform: 'translateY(0px)',
      coverTransition: '0s',
      moving: false,
      couponCount: null,
      todayChecked: false,
      continuousDays: 0,
      checkinLoading: false
    }
  },
  onLoad(){
    this.loadCheckinData();
  },
  onShow(){
    this.loadCheckinData();
	
    if(this.hasLogin){
      fetchMemberCouponList(0).then(response=>{
        if(response.data!=null&&response.data.length>0){
          this.couponCount = response.data.length;
        }
      });
    }else{
      this.couponCount=null;
    }
  },
  computed: {
    ...mapState(['hasLogin','userInfo'])
  },
  methods: {
	  
    /**
     * 加载签到数据
     */
    async loadCheckinData() {
      if (!this.hasLogin) {
        this.todayChecked = false;
        this.continuousDays = 0;
        return;
      }
      
      try {
        const [todayRes, continuousRes] = await Promise.all([
          getTodayStatus(),
          getContinuousDays()
        ]);
        
        this.todayChecked = todayRes.data;
        this.continuousDays = continuousRes.data || 0;
      } catch (error) {
        console.error('加载签到数据失败:', error);
        // 如果是因为未登录，重置状态
        if (error.message === '未登录') {
          this.todayChecked = false;
          this.continuousDays = 0;
        }
      }
    },

    /**
     * 处理签到
     */
    async handleCheckin() {
      if (!this.hasLogin) {
        this.navTo('/pages/public/login');
        return;
      }
      
      if (this.checkinLoading) return;
      
      this.checkinLoading = true;
      try {
        uni.showLoading({
          title: '签到中...',
          mask: true
        });
        
        const res = await checkin();
        
        if (res.code === 200) {
          this.todayChecked = true;
          this.continuousDays = res.data.continuousDays;
          
          uni.showToast({
            title: `签到成功！获得${res.data.integration}积分`,
            icon: 'success',
            duration: 2000
          });
          
          // 更新用户积分信息
          this.$store.dispatch('getMemberInfo');
        } else {
          uni.showToast({
            title: res.message || '签到失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('签到失败:', error);
        if (error.message === '未登录') {
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          });
          this.navTo('/pages/public/login');
        } else {
          uni.showToast({
            title: '签到失败，请重试',
            icon: 'none'
          });
        }
      } finally {
        uni.hideLoading();
        this.checkinLoading = false;
      }
    },

    /**
     * 统一跳转接口,拦截未登录路由
     */
    navTo(url){
      if(!this.hasLogin){
        url = '/pages/public/login';
      }
      uni.navigateTo({  
        url
      })  
    }, 

    /**
     *  会员卡下拉和回弹
     */
    coverTouchstart(e){
      if(pageAtTop === false){
        return;
      }
      this.coverTransition = 'transform .1s linear';
      startY = e.touches[0].clientY;
    },
    coverTouchmove(e){
      moveY = e.touches[0].clientY;
      let moveDistance = moveY - startY;
      if(moveDistance < 0){
        this.moving = false;
        return;
      }
      this.moving = true;
      if(moveDistance >= 80 && moveDistance < 100){
        moveDistance = 80;
      }
  
      if(moveDistance > 0 && moveDistance <= 80){
        this.coverTransform = `translateY(${moveDistance}px)`;
      }
    },
    coverTouchend(){
      if(this.moving === false){
        return;
      }
      this.moving = false;
      this.coverTransition = 'transform 0.3s cubic-bezier(.21,1.93,.53,.64)';
      this.coverTransform = 'translateY(0px)';
    }
  }  
}  
</script>  

<style lang='scss'>
	%flex-center {
	 display:flex;
	 flex-direction: column;
	 justify-content: center;
	 align-items: center;
	}
	%section {
	  display:flex;
	  justify-content: space-around;
	  align-content: center;
	  background: #fff;
	  border-radius: 10upx;
	}

	.user-section{
		height: 520upx;
		padding: 100upx 30upx 0;
		position:relative;
		.bg{
			position:absolute;
			left: 0;
			top: 0;
			width: 100%;
			.height: 100%;
			filter: blur(1px);
			opacity: .7;
		}
	}
	.user-info-box{
		height: 180upx;
		display:flex;
		align-items:center;
		position:relative;
		z-index: 1;
		.portrait{
			width: 130upx;
			height: 130upx;
			border:5upx solid #fff;
			border-radius: 50%;
		}
		.username{
			font-size: $font-lg + 6upx;
			color: $font-color-dark;
			margin-left: 20upx;
		}
	}

	.vip-card-box{
		display:flex;
		flex-direction: column;
		color: #f7d680;
		height: 240upx;
		background: linear-gradient(left, rgba(0,0,0,.7), rgba(0,0,0,.8));
		border-radius: 16upx 16upx 0 0;
		overflow: hidden;
		position: relative;
		padding: 20upx 24upx;
		.card-bg{
			position:absolute;
			top: 20upx;
			right: 0;
			width: 380upx;
			height: 260upx;
		}
		.b-btn{
			position: absolute;
			right: 20upx;
			top: 16upx;
			width: 132upx;
			height: 40upx;
			text-align: center;
			line-height: 40upx;
			font-size: 22upx;
			color: #36343c;
			border-radius: 20px;
			background: linear-gradient(left, #f9e6af, #ffd465);
			z-index: 1;
		}
		.tit{
			font-size: $font-base+2upx;
			color: #f7d680;
			margin-bottom: 28upx;
			.yticon{
				color: #f6e5a3;
				margin-right: 16upx;
			}
		}
		.e-b{
			font-size: $font-sm;
			color: #d8cba9;
			margin-top: 10upx;
		}
	}
	.cover-container{
		background: $page-color-base;
		margin-top: -150upx;
		padding: 0 30upx;
		position:relative;
		background: #f5f5f5;
		padding-bottom: 20upx;
		.arc{
			position:absolute;
			left: 0;
			top: -34upx;
			width: 100%;
			height: 36upx;
		}
	}

	/* 签到区域样式 */
	.checkin-section {
		background: #fff;
		border-radius: 10upx;
		padding: 30upx;
		margin-top: 20upx;
		box-shadow: 0 2upx 10upx rgba(0,0,0,0.05);
		
		.checkin-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 30upx;
			
			.checkin-title {
				font-size: 32upx;
				color: #333;
				font-weight: bold;
			}
			
			.checkin-record {
				font-size: 26upx;
				color: #666;
			}
		}
		
		.checkin-content {
			.checkin-status {
				display: flex;
				justify-content: space-between;
				align-items: center;
				
				.status-desc {
					font-size: 28upx;
					color: #666;
				}
				
				.checkin-btn {
					background: linear-gradient(135deg, #ff6b35, #ff8e53);
					color: white;
					border: none;
					border-radius: 50upx;
					padding: 15upx 40upx;
					font-size: 28upx;
					font-weight: bold;
				}
				
				.checked-status {
					.checked-text {
						font-size: 28upx;
						color: #07c160;
						font-weight: bold;
					}
				}
			}
		}
	}

	.tj-sction{
		@extend %section;
		.tj-item{
			@extend %flex-center;
			flex-direction: column;
			height: 140upx;
			font-size: $font-sm;
			color: #75787d;
		}
		.num{
			font-size: $font-lg;
			color: $font-color-dark;
			margin-bottom: 8upx;
		}
	}
	.order-section{
		@extend %section;
		padding: 28upx 0;
		margin-top: 20upx;
		.order-item{
			@extend %flex-center;
			width: 120upx;
			height: 120upx;
			border-radius: 10upx;
			font-size: $font-sm;
			color: $font-color-dark;
		}
		.yticon{
			font-size: 48upx;
			margin-bottom: 18upx;
			color: #fa436a;
		}
		.icon-shouhoutuikuan{
			font-size:44upx;
		}
	}
	.history-section{
		padding: 30upx 0 0;
		margin-top: 20upx;
		background: #fff;
		border-radius:10upx;
		.sec-header{
			display:flex;
			align-items: center;
			font-size: $font-base;
			color: $font-color-dark;
			line-height: 40upx;
			margin-left: 30upx;
			.yticon{
				font-size: 44upx;
				color: #5eba8f;
				margin-right: 16upx;
				line-height: 40upx;
			}
		}
		.h-list{
			white-space: nowrap;
			padding: 30upx 30upx 0;
			image{
				display:inline-block;
				width: 160upx;
				height: 160upx;
				margin-right: 20upx;
				border-radius: 10upx;
			}
		}
	}
</style>