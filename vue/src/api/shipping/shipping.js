import request from '@/utils/request'

// 备货清单-仓库发货
export function listShipStockupWarehouse(query) {
  return request({
    url: '/ship/stock_up_list_by_warehouse',
    method: 'get',
    params: query
  })
}


// 备货清单-供应商发货
export function listShipStockupSupplier(query) {
  return request({
    url: '/ship/stock_up_list_by_supplier',
    method: 'get',
    params: query
  })
}


// 生成出库单（备货清单生成出库单）
export function generateStockOutEntry(data) {
  return request({
    url: '/ship/generate_stock_out_entry',
    method: 'post',
    data: data
  })
}


// 供应商发货手动确认
export function supplierShipConfirm(data) {
  return request({
    url: '/ship/supplier_ship_confirm',
    method: 'post',
    data: data
  })
}

// 修改备货单item skuId
export function shipOrderItemSkuIdUpdate(data) {
  return request({
    url: '/ship/order_item_sku_id_update',
    method: 'post',
    data: data
  })
}

// 订单待出库列表
export function listOrderStockOutEntry(query) {
  return request({
    url: '/shipping/order_stock_out_entry_list',
    method: 'get',
    params: query
  })
}

// 订单待出库明细列表
export function listOrderStockOutEntryItem(query) {
  return request({
    url: '/shipping/order_stock_out_entry_item_list',
    method: 'get',
    params: query
  })
}
