import { api } from "./ApiManager";
import cookie from "./CookieManager";

const LoginManager = {
  login: async (credentials) => {
    try {
      const response = await api.post("/users/login/", credentials);
      const token = response.data?.access;
      if (token) {
        cookie.SaveToken(token);
      }
      return response.data;
    } catch (error) {
      console.error(error.response);
      throw error.response?.data || error.message;
    }
  },
};

export default LoginManager;
