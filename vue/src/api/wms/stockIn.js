import request from '@/utils/request'

// 查询入库单列表
export function listStockIn(query) {
  return request({
    url: '/wms-api/stockIn/list',
    method: 'get',
    params: query
  })
}

// 查询入库单详细
export function getWmsStockInEntry(id) {
  return request({
    url: '/wms-api/stockIn/' + id,
    method: 'get'
  })
}

// 新增入库单
export function stockInCreate(data) {
  return request({
    url: '/wms-api/stockIn/create',
    method: 'post',
    data: data
  })
}
export function stockIn(data) {
  return request({
    url: '/wms-api/stockIn/in',
    method: 'post',
    data: data
  })
}
export function complete(id) {
  return request({
    url: '/wms/WmsStockInEntry/complete/' + id,
    method: 'get'
  })
}

