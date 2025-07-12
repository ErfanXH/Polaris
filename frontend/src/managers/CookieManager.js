import Constants from "./Constants";
import Cookies from "js-cookie";

const saveCookie = (credentials, expire = 30) => {
  const cookie = {
    token: credentials.access.slice(4),
    isAdmin: credentials.is_admin,
  };
  Cookies.set(Constants.CookieKey, JSON.stringify(cookie), {
    expires: expire,
  });
};

const loadCookie = () => {
  return JSON.parse(Cookies.get(Constants.CookieKey) || "{}");
};

const loadIsAdmin = () => {
  const cookie = loadCookie();
  return cookie?.isAdmin;
};

const loadToken = () => {
  const cookie = loadCookie();
  return cookie?.token;
};

const removeCookie = () => {
  Cookies.remove(Constants.CookieKey);
};

export default { saveCookie, loadToken, loadIsAdmin, removeCookie };
