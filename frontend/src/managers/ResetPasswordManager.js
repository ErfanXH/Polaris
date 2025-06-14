import { api } from "./ApiManager";

const ResetPasswordManager = {
  sendResetCode: async (credential) => {
    try {
      const response = await api.post("/users/get_verification_code/", {
        number_or_email: credential,
      });
      return response.data;
    } catch (error) {
      throw JSON.stringify(error.response?.data) || error.message;
    }
  },

  verifyResetCode: async (credential, code) => {
    try {
      const response = await api.post("/users/verify_code/", {
        number_or_email: credential,
        code: code,
      });
      return response.data;
    } catch (error) {
      throw JSON.stringify(error.response?.data) || error.message;
    }
  },

  resetPassword: async (numberOrEmail, code, password) => {
    try {
      const response = await api.post("/users/verification/", {
        number_or_email: numberOrEmail,
        code: code,
        password: password,
      });
      return response.data;
    } catch (error) {
      throw JSON.stringify(error.response?.data) || error.message;
    }
  },
};

export default ResetPasswordManager;
