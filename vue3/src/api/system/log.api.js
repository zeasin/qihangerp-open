import request from "@/utils/request";

const LOG_BASE_URL = "/api/v1/logs";

const LogAPI = {
  /**
   * 获取日志分页列表
   * @param {Object} queryParams 查询参数
   * @returns {Promise} 日志分页结果
   */
  getPage(queryParams) {
    return request({
      url: `${LOG_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  },

  /**
   * 获取访问趋势
   * @param {Object} queryParams 查询参数
   * @returns {Promise} 访问趋势数据
   */
  getVisitTrend(queryParams) {
    return request({
      url: `${LOG_BASE_URL}/visit-trend`,
      method: "get",
      params: queryParams,
    });
  },

  /**
   * 获取访问统计
   * @returns {Promise} 访问统计数据
   */
  getVisitStats() {
    return request({
      url: `${LOG_BASE_URL}/visit-stats`,
      method: "get",
    });
  },
};

export default LogAPI;
