import { api } from "./ApiManager";
import CookieManager from "./CookieManager";

const DashboardManager = {
  getAll: async () => {
    try {
      const response = CookieManager.loadIsAdmin()
        ? await api.get("/mobile/measurement/get_all/")
        : await api.get("/mobile/measurement/");
      return response.data;
    } catch (error) {
      throw JSON.stringify(error.response?.data) || error.message;
    }
  },
  getNetworkTypes: async () => {
    try {
      const response = await api.get("/mobile/measurement/get_network_types/");
      return response.data;
    } catch (error) {
      throw JSON.stringify(error.response?.data) || error.message;
    }
  },
};

export default DashboardManager;
