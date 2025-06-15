import { api } from "./ApiManager";
import cookie from "./CookieManager";

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
