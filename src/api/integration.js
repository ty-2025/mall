import request from '@/utils/request'

// 查询积分记录列表
export function listIntegrationHistory(query) {
  return request({
    url: '/member/integration/list',
    method: 'get',
    params: query
  })
}

// 添加积分记录
export function addIntegrationHistory(data) {
  return request({
    url: '/member/integration',
    method: 'post',
    data: data
  })
}

// 获取积分记录详情
export function getIntegrationHistory(id) {
  return request({
    url: `/member/integration/${id}`,
    method: 'get'
  })
}

// 修改积分记录
export function updateIntegrationHistory(data) {
  return request({
    url: '/member/integration',
    method: 'put',
    data: data
  })
}

// 删除积分记录
export function delIntegrationHistory(ids) {
  return request({
    url: `/member/integration/${ids}`,
    method: 'delete'
  })
}
