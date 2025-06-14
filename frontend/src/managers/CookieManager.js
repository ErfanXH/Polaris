import TokenCookieKey from "./Constants";
import Cookies from "js-cookie";

const SaveToken = (token, expire = 30) => {
  token = token.slice(4);
  Cookies.set(TokenCookieKey.TokenCookieKey, token, { expires: expire });
};

const LoadToken = () => {
  return Cookies.get(TokenCookieKey.TokenCookieKey);
};

const RemoveToken = () => {
  Cookies.remove(TokenCookieKey.TokenCookieKey);
};

export default { SaveToken, LoadToken, RemoveToken };
