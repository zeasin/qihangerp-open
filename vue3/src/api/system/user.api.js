import request from "@/utils/request";

const USER_BASE_URL = "/api/v1/users";

const UserAPI = {
  /**
   * 获取当前登录用户信息
   * @returns {Promise} 登录用户昵称、头像信息，包括角色和权限
   */
  getInfo() {
    return request({
      url: `${USER_BASE_URL}/me`,
      method: "get",
    });
  },

  /**
   * 获取用户分页列表
   * @param {Object} queryParams 查询参数
   * @returns {Promise} 用户分页列表
   */
  getPage(queryParams) {
    return request({
      url: `${USER_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  },

  /**
   * 获取用户表单详情
   * @param {string} userId 用户ID
   * @returns {Promise} 用户表单详情
   */
  getFormData(userId) {
    return request({
      url: `${USER_BASE_URL}/${userId}/form`,
      method: "get",
    });
  },

  /**
   * 添加用户
   * @param {Object} data 用户表单数据
   * @returns {Promise} 添加结果
   */
  create(data) {
    return request({
      url: `${USER_BASE_URL}`,
      method: "post",
      data: data,
    });
  },

  /**
   * 修改用户
   * @param {string} id 用户ID
   * @param {Object} data 用户表单数据
   * @returns {Promise} 修改结果
   */
  update(id, data) {
    return request({
      url: `${USER_BASE_URL}/${id}`,
      method: "put",
      data: data,
    });
  },

  /**
   * 修改用户密码
   * @param {string} id 用户ID
   * @param {string} password 新密码
   * @returns {Promise} 修改结果
   */
  resetPassword(id, password) {
    return request({
      url: `${USER_BASE_URL}/${id}/password/reset`,
      method: "put",
      params: { password: password },
    });
  },

  /**
   * 批量删除用户，多个以英文逗号(,)分割
   * @param {string} ids 用户ID字符串，多个以英文逗号(,)分割
   * @returns {Promise} 删除结果
   */
  deleteByIds(ids) {
    return request({
      url: `${USER_BASE_URL}/${ids}`,
      method: "delete",
    });
  },

  /** 
   * 下载用户导入模板
   * @returns {Promise} 模板文件
   */
  downloadTemplate() {
    return request({
      url: `${USER_BASE_URL}/template`,
      method: "get",
      responseType: "blob",
    });
  },

  /**
   * 导出用户
   * @param {Object} queryParams 查询参数
   * @returns {Promise} 导出文件
   */
  export(queryParams) {
    return request({
      url: `${USER_BASE_URL}/export`,
      method: "get",
      params: queryParams,
      responseType: "blob",
    });
  },

  /**
   * 导入用户
   * @param {string} deptId 部门ID
   * @param {File} file 导入文件
   * @returns {Promise} 导入结果
   */
  import(deptId, file) {
    const formData = new FormData();
    formData.append("file", file);
    return request({
      url: `${USER_BASE_URL}/import`,
      method: "post",
      params: { deptId: deptId },
      data: formData,
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },

  /** 
   * 获取个人中心用户信息
   * @returns {Promise} 用户信息
   */
  getProfile() {
    return request({
      url: `${USER_BASE_URL}/profile`,
      method: "get",
    });
  },

  /** 
   * 修改个人中心用户信息
   * @param {Object} data 用户信息
   * @returns {Promise} 修改结果
   */
  updateProfile(data) {
    return request({
      url: `${USER_BASE_URL}/profile`,
      method: "put",
      data: data,
    });
  },

  /** 
   * 修改个人中心用户密码
   * @param {Object} data 密码信息
   * @returns {Promise} 修改结果
   */
  changePassword(data) {
    return request({
      url: `${USER_BASE_URL}/password`,
      method: "put",
      data: data,
    });
  },

  /** 
   * 发送短信验证码（绑定或更换手机号）
   * @param {string} mobile 手机号
   * @returns {Promise} 发送结果
   */
  sendMobileCode(mobile) {
    return request({
      url: `${USER_BASE_URL}/mobile/code`,
      method: "post",
      params: { mobile: mobile },
    });
  },

  /** 
   * 绑定或更换手机号
   * @param {Object} data 手机号信息
   * @returns {Promise} 绑定结果
   */
  bindOrChangeMobile(data) {
    return request({
      url: `${USER_BASE_URL}/mobile`,
      method: "put",
      data: data,
    });
  },

  /** 
   * 发送邮箱验证码（绑定或更换邮箱）
   * @param {string} email 邮箱
   * @returns {Promise} 发送结果
   */
  sendEmailCode(email) {
    return request({
      url: `${USER_BASE_URL}/email/code`,
      method: "post",
      params: { email: email },
    });
  },

  /** 
   * 绑定或更换邮箱
   * @param {Object} data 邮箱信息
   * @returns {Promise} 绑定结果
   */
  bindOrChangeEmail(data) {
    return request({
      url: `${USER_BASE_URL}/email`,
      method: "put",
      data: data,
    });
  },

  /** 
   * 获取用户选项列表
   * @returns {Promise} 用户选项列表
   */
  getOptions() {
    return request({
      url: `${USER_BASE_URL}/options`,
      method: "get",
    });
  }
};

export default UserAPI;
