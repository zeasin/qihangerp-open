import request from "@/utils/request";
// 菜单基础URL
const MENU_BASE_URL = "/api/v1/menus";

const MenuAPI = {
  /**
   * 获取当前用户的路由列表
   * <p/>
   * 无需传入角色，后端解析token获取角色自行判断是否拥有路由的权限
   * @returns {Promise} 路由列表
   */
  getRoutes() {
    return request({
      url: `${MENU_BASE_URL}/routes`,
      method: "get",
    });
  },

  /**
   * 获取菜单树形列表
   * @param {Object} queryParams 查询参数
   * @returns {Promise} 菜单树形列表
   */
  getList(queryParams) {
    return request({
      url: `${MENU_BASE_URL}`,
      method: "get",
      params: queryParams,
    });
  },

  /**
   * 获取菜单下拉数据源
   * @param {boolean} [onlyParent] 是否只获取父级菜单
   * @returns {Promise} 菜单下拉数据源
   */
  getOptions(onlyParent) {
    return request({
      url: `${MENU_BASE_URL}/options`,
      method: "get",
      params: { onlyParent: onlyParent },
    });
  },

  /**
   * 获取菜单表单数据
   * @param {string} id 菜单ID
   * @returns {Promise} 菜单表单数据
   */
  getFormData(id) {
    return request({
      url: `${MENU_BASE_URL}/${id}/form`,
      method: "get",
    });
  },

  /**
   * 添加菜单
   * @param {Object} data 菜单表单数据
   * @returns {Promise} 请求结果
   */
  create(data) {
    return request({
      url: `${MENU_BASE_URL}`,
      method: "post",
      data: data,
    });
  },

  /**
   * 修改菜单
   * @param {string} id 菜单ID
   * @param {Object} data 菜单表单数据
   * @returns {Promise} 请求结果
   */
  update(id, data) {
    return request({
      url: `${MENU_BASE_URL}/${id}`,
      method: "put",
      data: data,
    });
  },

  /**
   * 删除菜单
   * @param {string} id 菜单ID
   * @returns {Promise} 请求结果
   */
  deleteById(id) {
    return request({
      url: `${MENU_BASE_URL}/${id}`,
      method: "delete",
    });
  },
};

export default MenuAPI;
