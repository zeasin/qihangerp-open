import request from "@/utils/request";

const DEPT_BASE_URL = "/api/v1/dept";

const DeptAPI = {
  /**
   * 获取部门列表
   * @param {Object} queryParams 查询参数（可选）
   * @returns {Promise} 部门树形表格数据
   */
  getList(queryParams) {
    return request({
      url: `${DEPT_BASE_URL}`,
      method: "get",
      params: queryParams,
    });
  },

  /**
   * 获取部门下拉列表
   * @returns {Promise} 部门下拉列表数据
   */
  getOptions() {
    return request({
      url: `${DEPT_BASE_URL}/options`,
      method: "get",
    });
  },

  /**
   * 获取部门表单数据
   * @param {string} id 部门ID
   * @returns {Promise} 部门表单数据
   */
  getFormData(id) {
    return request({
      url: `${DEPT_BASE_URL}/${id}/form`,
      method: "get",
    });
  },

  /**
   * 新增部门
   * @param {Object} data 部门表单数据
   * @returns {Promise} 请求结果
   */
  create(data) {
    return request({
      url: `${DEPT_BASE_URL}`,
      method: "post",
      data: data,
    });
  },

  /**
   * 修改部门
   * @param {string} id 部门ID
   * @param {Object} data 部门表单数据
   * @returns {Promise} 请求结果
   */
  update(id, data) {
    return request({
      url: `${DEPT_BASE_URL}/${id}`,
      method: "put",
      data: data,
    });
  },

  /**
   * 删除部门
   * @param {string} ids 部门ID，多个以英文逗号(,)分隔
   * @returns {Promise} 请求结果
   */
  deleteByIds(ids) {
    return request({
      url: `${DEPT_BASE_URL}/${ids}`,
      method: "delete",
    });
  },
};

export default DeptAPI;
