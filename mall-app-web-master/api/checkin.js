// api/checkin.js - 使用项目统一的请求工具
import { request } from '@/utils/requestUtil.js';

// 签到API函数
// 使用新的模拟签到接口
export function checkin() {
  return request({
    url: '/member/checkin/mockCheckinWithPoints',
    method: 'POST'
  });
}
export function getCheckinHistory(startDate, endDate, pageNum, pageSize) {
  const params = {
    pageNum: pageNum || 1,
    pageSize: pageSize || 10
  };
  
  if (startDate) {
    params.startDate = formatDate(startDate, 'yyyy-MM-dd');
  }
  if (endDate) {
    params.endDate = formatDate(endDate, 'yyyy-MM-dd');
  }
  
  return request({
    url: '/member/checkin/history',
    method: 'GET',
    params: params
  });
}

export function getContinuousDays() {
  return request({
    url: '/member/checkin/continuousDays',
    method: 'GET'
  });
}


export function getTodayStatus() {
  return request({
    url: '/member/checkin/todayStatus',
    method: 'GET'
  });
}

// 日期格式化辅助函数
function formatDate(date, fmt) {
  if (!(date instanceof Date)) {
    date = new Date(date);
  }
  
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
  }
  let o = {
    'M+': date.getMonth() + 1,
    'd+': date.getDate(),
    'h+': date.getHours(),
    'm+': date.getMinutes(),
    's+': date.getSeconds()
  };
  for (let k in o) {
    if (new RegExp(`(${k})`).test(fmt)) {
      let str = o[k] + '';
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? str : padLeftZero(str));
    }
  }
  return fmt;
}

function padLeftZero(str) {
  return ('00' + str).substr(str.length);
}
