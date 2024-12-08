import request from '@/utils/request'

// 查询采购订单物流列表
export function listPurchaseOrderShip(query) {
  return request({
    url: '/erp-api/scm/purchase/shipList',
    method: 'get',
    params: query
  })
}

// 查询采购订单物流详细
export function getPurchaseOrderShip(id) {
  return request({
    url: '/erp-api/scm/purchase/shipDetail/' + id,
    method: 'get'
  })
}



//创建入库单
export function createStockInEntry(data) {
  return request({
    url: '/erp-api/scm/purchase/ship/createStockInEntry',
    method: 'post',
    data: data
  })
}


// 修改采购订单物流
export function confirmReceipt(data) {
  return request({
    url: '/erp-api/scm/purchase/ship/confirmReceipt',
    method: 'put',
    data: data
  })
}


