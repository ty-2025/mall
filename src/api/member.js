import request from '@/utils/request'

// 会员相关API
export function fetchList(params) {
  return request({
    url: '/member/list',
    method: 'get',
    params: params
  })
}

export function getMemberDetail(id) {
  return request({
    url: `/member/${id}`,
    method: 'get'
  })
}

export function createMember(data) {
  return request({
    url: '/member/create',
    method: 'post',
    data: data
  })
}

export function updateMember(data) {
  return request({
    url: `/member/update/${data.id}`,
    method: 'post',
    data: data
  })
}

export function deleteMember(id) {
  return request({
    url: `/member/delete/${id}`,
    method: 'post'
  })
}

export function updateMemberStatus(id, status) {
  return request({
    url: `/member/updateStatus/${id}`,
    method: 'post',
    params: { status: status }
  })
}
