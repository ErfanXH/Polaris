import { api } from "./ApiManager";

const UserListManager = {
  getAll: async () => {
    try {
      const response = await api.get("/users/admin/all_users");
      return response.data;
    } catch (error) {
      throw JSON.stringify(error.response?.data) || error.message;
    }
  },
  ban: async (credential) => {
    try {
      const response = await api.post("/users/admin/ban_user/", {
        number_or_email: credential,
      });
      return response.data;
    } catch (error) {
      throw JSON.stringify(error.response?.data) || error.message;
    }
  },
  allow: async (credential) => {
    try {
      const response = await api.post("/users/admin/allow_user/", {
        number_or_email: credential,
      });
      return response.data;
    } catch (error) {
      throw JSON.stringify(error.response?.data) || error.message;
    }
  },
};

export default UserListManager;
