import request from "@/utils/request";

const FileAPI = {
  /**
   * 上传文件
   *
   * @param formData
   */
  upload(formData) {
    return request({
      url: "/api/v1/files",
      method: "post",
      data: formData,
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },

  /**
   * 上传文件
   */
  uploadFile(file) {
    const formData = new FormData();
    formData.append("file", file);
    return request({
      url: "/api/v1/files",
      method: "post",
      data: formData,
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },

  /**
   * 删除文件
   *
   * @param filePath 文件完整路径
   */
  delete(filePath) {
    return request({
      url: "/api/v1/files",
      method: "delete",
      params: { filePath: filePath },
    });
  },

  /**
   * 下载文件
   * @param url
   * @param fileName
   */
  download(url, fileName) {
    return request({
      url: url,
      method: "get",
      responseType: "blob",
    }).then((res) => {
      const blob = new Blob([res.data]);
      const a = document.createElement("a");
      const url = window.URL.createObjectURL(blob);
      a.href = url;
      a.download = fileName || "下载文件";
      a.click();
      window.URL.revokeObjectURL(url);
    });
  },
};

export default FileAPI; 