import request from '@/utils/request'

export function fetchMemberList(params) {
  return request({
    url: '/member/list',
    method: 'get',
    params: params
  })
}

export function fetchMemberDetail(id) {
  return request({
    url: `/member/${id}`,
    method: 'get'
  })
}

export function fetchMemberIntegration(memberId) {
  return request({
    url: `/member/${memberId}/integration`,
    method: 'get'
  })
}

export function fetchMemberLevelList(params) {
  return request({
    url: '/member/level/list',
    method: 'get',
    params: params
  })
}

export function createMemberLevel(data) {
  return request({
    url: '/member/level/create',
    method: 'post',
    data: data
  })
}

export function updateMemberLevel(data) {
  return request({
    url: '/member/level/update',
    method: 'post',
    data: data
  })
}

export function deleteMemberLevel(id) {
  return request({
    url: `/member/level/delete/${id}`,
    method: 'post'
  })
}
