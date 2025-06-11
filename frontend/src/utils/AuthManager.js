export const isAuthenticated = () => (cookieManager.LoadToken() ? true : false);
