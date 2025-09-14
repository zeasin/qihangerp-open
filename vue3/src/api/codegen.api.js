import request from "@/utils/request";

const GENERATOR_BASE_URL = "/api/v1/codegen";

const GeneratorAPI = {
  /** 获取数据表分页列表 */
  getTablePage(params) {
    return request({
      url: `${GENERATOR_BASE_URL}/table/page`,
      method: "get",
      params: params,
    });
  },

  /** 获取代码生成配置 */
  getGenConfig(tableName) {
    return request({
      url: `${GENERATOR_BASE_URL}/${tableName}/config`,
      method: "get",
    });
  },

  /** 获取代码生成配置 */
  saveGenConfig(tableName, data) {
    return request({
      url: `${GENERATOR_BASE_URL}/${tableName}/config`,
      method: "post",
      data: data,
    });
  },

  /** 获取代码生成预览数据 */
  getPreviewData(tableName) {
    return request({
      url: `${GENERATOR_BASE_URL}/${tableName}/preview`,
      method: "get",
    });
  },

  /** 重置代码生成配置 */
  resetGenConfig(tableName) {
    return request({
      url: `${GENERATOR_BASE_URL}/${tableName}/config`,
      method: "delete",
    });
  },

  /**
   * 下载 ZIP 文件
   * @param url
   * @param fileName
   */
  download(tableName) {
    return request({
      url: `${GENERATOR_BASE_URL}/${tableName}/download`,
      method: "get",
      responseType: "blob",
    }).then((response) => {
      const fileName = decodeURI(
        response.headers["content-disposition"].split(";")[1].split("=")[1]
      );

      const blob = new Blob([response.data], { type: "application/zip" });
      const a = document.createElement("a");
      const url = window.URL.createObjectURL(blob);
      a.href = url;
      a.download = fileName;
      a.click();
      window.URL.revokeObjectURL(url);
    });
  },
};

export default GeneratorAPI; 