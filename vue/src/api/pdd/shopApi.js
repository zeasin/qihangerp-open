import request from '@/utils/request'


export function getOAuthUrl(query) {
  return request({
    url: '/api/open-api/pdd/getOauthUrl',
    method: 'get',
    params: query
  })
}
export function getPddToken(data) {
  return request({
    url: '/api/open-api/pdd/getToken',
    method: 'post',
    data: data
  })
}
