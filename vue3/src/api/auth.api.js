import request from "@/utils/request";

const AUTH_BASE_URL = "";

const AuthAPI = {
  /** 登录接口*/
  login(data) {
    const formData = new FormData();
    formData.append("username", data.username);
    formData.append("password", data.password);
    formData.append("captchaKey", data.captchaKey);
    formData.append("captchaCode", data.captchaCode);
    return request({
      url: `${AUTH_BASE_URL}/login`,
      method: "post",
      data: formData,
      // headers: {
      //   "Content-Type": "multipart/form-data",
      // },
    });
  },

  /** 刷新 token 接口*/
  refreshToken(refreshToken) {
    return request({
      url: `${AUTH_BASE_URL}/refresh-token`,
      method: "post",
      params: { refreshToken: refreshToken },
      headers: {
        Authorization: "no-auth",
      },
    });
  },

  /** 注销登录接口 */
  logout() {
    return request({
      url: `${AUTH_BASE_URL}/logout`,
      method: "delete",
    });
  },

  /** 获取验证码接口*/
  getCaptcha() {
    return request({
      url: `${AUTH_BASE_URL}/captchaImage`,
      method: "get",
    });
  },
};

export default AuthAPI; 
