import request from "@/utils/request";

const CONFIG_BASE_URL = "/api/v1/config";

const ConfigAPI = {
  /**
   * 系统配置分页
   * @param {Object} queryParams 查询参数
   * @returns {Promise} 系统配置分页结果
   */
  getPage(queryParams) {
    return request({
      url: `${CONFIG_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  },

  /**
   * 系统配置表单数据
   * @param {string} id 配置ID
   * @returns {Promise} 系统配置表单数据
   */
  getFormData(id) {
    return request({
      url: `${CONFIG_BASE_URL}/${id}/form`,
      method: "get",
    });
  },

  /**
   * 新增系统配置
   * @param {Object} data 系统配置表单数据
   * @returns {Promise} 添加结果
   */
  create(data) {
    return request({
      url: `${CONFIG_BASE_URL}`,
      method: "post",
      data: data,
    });
  },

  /**
   * 更新系统配置
   * @param {string} id 配置ID
   * @param {Object} data 系统配置表单数据
   * @returns {Promise} 更新结果
   */
  update(id, data) {
    return request({
      url: `${CONFIG_BASE_URL}/${id}`,
      method: "put",
      data: data,
    });
  },

  /**
   * 删除系统配置
   * @param {string} id 系统配置ID
   * @returns {Promise} 删除结果
   */
  deleteById(id) {
    return request({
      url: `${CONFIG_BASE_URL}/${id}`,
      method: "delete",
    });
  },

  /**
   * 刷新系统配置缓存
   * @returns {Promise} 刷新结果
   */
  refreshCache() {
    return request({
      url: `${CONFIG_BASE_URL}/refresh`,
      method: "PUT",
    });
  },
};

export default ConfigAPI;
