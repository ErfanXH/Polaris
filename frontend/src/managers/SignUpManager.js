import { api } from "./ApiManager";

const SignUpManager = {
  signUp: async (credentials) => {
    try {
      const response = await api.post("/users/register/", credentials);
      return response.data;
    } catch (error) {
      console.error(error.response.data);
      throw error.response?.data || error.message;
    }
  },
};

export default SignUpManager;
