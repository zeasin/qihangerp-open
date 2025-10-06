import request from '@/utils/request'

// 查询拼多多订单列表
export function listOrder(query) {
  return request({
    url: '/pdd/order/list',
    method: 'get',
    params: query
  })
}

// 查询拼多多订单详细
export function getOrder(id) {
  return request({
    url: '/pdd/order/' + id,
    method: 'get'
  })
}



export function pushOms(data) {
  return request({
    url: '/pdd/order/push_oms',
    method: 'post',
    data: data
  })
}

// 接口拉取拼多多订单
export function pullOrder(data) {
  return request({
    url: '/pdd/order/pull_order',
    method: 'post',
    data: data
  })
}

export function pullOrderDetail(data) {
  return request({
    url: '/pdd/order/pull_order_detail',
    method: 'post',
    data: data
  })
}
// 确认订单
export function confirmOrder(data) {
  return request({
    url: '/pdd/order/confirmOrder',
    method: 'post',
    data: data
  })
}
