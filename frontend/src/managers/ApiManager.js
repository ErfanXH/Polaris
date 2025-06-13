import axios from "axios";
import cookie from "./CookieManager";
import constants from "./Constants";

export const api = axios.create({
  baseURL: constants.baseUrl,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use(
  (config) => {
    const token = cookie.LoadToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      cookie.RemoveToken();
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);
