import request from '@/utils/request'

// 查询店铺订单列表
export function listOrder(query) {
  return request({
    url: '/order/list',
    method: 'get',
    params: query
  })
}
// 查询待自己发货的订单列表（待发货的）
export function waitSelfShipmentList(query) {
  return request({
    url: '/order/waitShipmentList',
    method: 'get',
    params: query
  })
}
// 查询已分配给供应商发货的订单
export function assignedShipmentList(query) {
  return request({
    url: '/order/assignedShipmentList',
    method: 'get',
    params: query
  })
}
// 查询己发货的订单列表(已发货的)
export function selfShippedList(query) {
  return request({
    url: '/order/shippedList',
    method: 'get',
    params: query
  })
}
// 手动发货
export function manualShipmentOrder(data) {
  return request({
    url: '/order/manualShipment',
    method: 'post',
    data: data
  })
}

// 分配供应商发货
export function allocateShipmentOrder(data) {
  return request({
    url: '/order/allocateShipmentOrder',
    method: 'post',
    data: data
  })
}

// 查询店铺订单详细
export function getOrder(id) {
  return request({
    url: '/order/' + id,
    method: 'get'
  })
}

// 订单明细list
export function listOrderItem(query) {
  return request({
    url: '/order/item_list',
    method: 'get',
    params: query
  })
}

export function updateErpSkuId(data) {
  return request({
    url: '/order/updateErpSkuId',
    method: 'post',
    data: data
  })
}

export function shipOrder(data) {
  return request({
    url: '/api/order/ship',
    method: 'post',
    data: data
  })
}

export function pushErp(id) {
  return request({
    url: '/order/pushErp/' + id,
    method: 'post'
  })
}

// 新增店铺订单
export function addOrder(data) {
  return request({
    url: '/order',
    method: 'post',
    data: data
  })
}
// 取消订单
export function cancelOrder(data) {
  return request({
    url: '/order/cancelOrder',
    method: 'post',
    data: data
  })
}
