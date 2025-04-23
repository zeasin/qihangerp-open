import request from '@/utils/request'


export function getOAuthUrl(query) {
  return request({
    url: '/pdd/getOauthUrl',
    method: 'get',
    params: query
  })
}
export function getPddToken(data) {
  return request({
    url: '/pdd/getToken',
    method: 'post',
    data: data
  })
}
