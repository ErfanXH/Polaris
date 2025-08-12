import { api } from "./ApiManager";

const DashboardManager = {
  getAll: async () => {
    try {
      const response = await api.get("/mobile/measurement/");
      return response.data;
    } catch (error) {
      throw JSON.stringify(error.response?.data) || error.message;
    }
  },
};

export default DashboardManager;
