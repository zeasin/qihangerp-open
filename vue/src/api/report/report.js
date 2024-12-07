import request from '@/utils/request'

// 首页今日销售统计
export function todayDaily(query) {
  return request({
    url: '/api/oms-api/report/todayDaily',
    method: 'get',
    params: query
  })
}

// 销售统计
export function salesDaily(query) {
  return request({
    url: '/api/oms-api/report/salesDaily',
    method: 'get',
    params: query
  })
}


export function salesTopSku(query) {
  return request({
    url: '/api/oms-api/report/salesTopSku',
    method: 'get',
    params: query
  })
}
