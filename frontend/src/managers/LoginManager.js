import { api } from "./ApiManager";
import cookie from "./CookieManager";

const LoginManager = {
  login: async (credentials) => {
    try {
      const response = await api.post("/users/login/", credentials);
      const userInfo = response.data;
      if (userInfo) {
        cookie.saveCookie(userInfo);
      }
      return response.data;
    } catch (error) {
      if (error.response?.status === 401) {
        throw {
          status: 401,
          type: "EMAIL_NOT_VERIFIED",
        };
      }
      throw JSON.stringify(error.response?.data) || error.message;
    }
  },
};

export default LoginManager;
