import request from '@/utils/request'

// 查询退换货列表
export function listReturned(query) {
  return request({
    url: '/api/oms-api/refund/list',
    method: 'get',
    params: query
  })
}

// 查询退换货详细
export function getReturned(id) {
  return request({
    url: '/api/oms-api/refund/' + id,
    method: 'get'
  })
}

export function pushRefundToErp(id) {
  return request({
    url: '/api/oms-api/refund/pushErp/' + id,
    method: 'post'
  })
}

export function refundProcessing(data) {
  return request({
    url: '/api/oms-api/refund/processing',
    method: 'post',
    data:data
  })
}
