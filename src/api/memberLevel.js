import request from '@/utils/request'

export function fetchList(params) {
  return request({
    url: '/memberLevel/list',
    method: 'get',
    params: params
  })
}

export function getLevelDetail(id) {
  return request({
    url: `/memberLevel/${id}`,
    method: 'get'
  })
}

export function createMemberLevel(data) {
  return request({
    url: '/memberLevel/create',
    method: 'post',
    data: data
  })
}

export function updateMemberLevel(data) {
  return request({
    url: `/memberLevel/update/${data.id}`,
    method: 'post',
    data: data
  })
}

export function deleteMemberLevel(id) {
  return request({
    url: `/memberLevel/delete/${id}`,
    method: 'post'
  })
}

export function updateMemberLevelStatus(id, defaultStatus) {
  return request({
    url: `/memberLevel/updateDefaultStatus/${id}`,
    method: 'post',
    params: { defaultStatus: defaultStatus }
  })
}
