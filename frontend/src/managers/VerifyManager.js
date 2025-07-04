import { api } from "./ApiManager";
import cookie from "./CookieManager";

const VerifyManager = {
  sendVerificationCode: async (credential) => {
    try {
      const response = await api.post("/users/get_verification_code/", {
        number_or_email: credential,
      });
      return response.data;
    } catch (error) {
      throw JSON.stringify(error.response?.data) || error.message;
    }
  },

  verify: async (numberOrEmail, password, code) => {
    try {
      const response = await api.post("/users/verification/", {
        number_or_email: numberOrEmail,
        password: password,
        code: code,
      });
      const token = response.data?.access;
      if (token) {
        cookie.SaveToken(token);
      }
      return response.data;
    } catch (error) {
      throw JSON.stringify(error.response?.data) || error.message;
    }
  },
};

export default VerifyManager;
