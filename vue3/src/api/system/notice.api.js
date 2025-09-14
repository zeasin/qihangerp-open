import request from "@/utils/request";

const NOTICE_BASE_URL = "/api/v1/notices";

const NoticeAPI = {
  /**
   * 获取通知公告分页数据
   * @param {Object} queryParams 查询参数
   * @returns {Promise} 通知公告分页数据
   */
  getPage(queryParams) {
    return request({
      url: `${NOTICE_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  },

  /**
   * 获取通知公告表单数据
   * @param {string} id 通知ID
   * @returns {Promise} 通知表单数据
   */
  getFormData(id) {
    return request({
      url: `${NOTICE_BASE_URL}/${id}/form`,
      method: "get",
    });
  },

  /**
   * 添加通知公告
   * @param {Object} data 通知表单数据
   * @returns {Promise} 添加结果
   */
  create(data) {
    return request({
      url: `${NOTICE_BASE_URL}`,
      method: "post",
      data: data,
    });
  },

  /**
   * 更新通知公告
   * @param {string} id 通知ID
   * @param {Object} data 通知表单数据
   * @returns {Promise} 更新结果
   */
  update(id, data) {
    return request({
      url: `${NOTICE_BASE_URL}/${id}`,
      method: "put",
      data: data,
    });
  },

  /**
   * 批量删除通知公告，多个以英文逗号(,)分割
   * @param {string} ids 通知公告ID字符串，多个以英文逗号(,)分割
   * @returns {Promise} 删除结果
   */
  deleteByIds(ids) {
    return request({
      url: `${NOTICE_BASE_URL}/${ids}`,
      method: "delete",
    });
  },

  /**
   * 发布通知
   * @param {string} id 被发布的通知公告id
   * @returns {Promise} 发布结果
   */
  publish(id) {
    return request({
      url: `${NOTICE_BASE_URL}/${id}/publish`,
      method: "put",
    });
  },

  /**
   * 撤回通知
   * @param {string} id 撤回的通知id
   * @returns {Promise} 撤回结果
   */
  revoke(id) {
    return request({
      url: `${NOTICE_BASE_URL}/${id}/revoke`,
      method: "put",
    });
  },

  /**
   * 查看通知
   * @param {string} id 通知ID
   * @returns {Promise} 通知详情
   */
  getDetail(id) {
    return request({
      url: `${NOTICE_BASE_URL}/${id}/detail`,
      method: "get",
    });
  },

  /**
   * 全部已读
   * @returns {Promise} 操作结果
   */
  readAll() {
    return request({
      url: `${NOTICE_BASE_URL}/read-all`,
      method: "put",
    });
  },

  /**
   * 获取我的通知分页列表
   * @param {Object} queryParams 查询参数
   * @returns {Promise} 通知分页列表
   */
  getMyNoticePage(queryParams) {
    return request({
      url: `${NOTICE_BASE_URL}/my-page`,
      method: "get",
      params: queryParams,
    });
  },
};

export default NoticeAPI;
