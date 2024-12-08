import request from '@/utils/request'

// 查询采购订单列表
export function listPurchaseOrder(query) {
  return request({
    url: '/erp-api/scm/purchase/list',
    method: 'get',
    params: query
  })
}

// 查询采购订单详细
export function getPurchaseOrder(id) {
  return request({
    url: '/erp-api/scm/purchase/detail/' + id,
    method: 'get'
  })
}

// 新增采购订单
export function addPurchaseOrder(data) {
  return request({
    url: '/erp-api/scm/purchase/create',
    method: 'post',
    data: data
  })
}

// 修改采购订单
export function updatePurchaseOrder(data) {
  return request({
    url: '/erp-api/scm/purchase/updateStatus',
    method: 'put',
    data: data
  })
}

// 删除采购订单
export function delPurchaseOrder(id) {
  return request({
    url: '/purchase/purchaseOrder/' + id,
    method: 'delete'
  })
}
