import { createContext, useState, useContext, useCallback } from "react";
import CookieManager from "../managers/CookieManager";
import { Navigate } from "react-router-dom";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [authState, setAuthState] = useState({
    isAuthenticated: !!CookieManager.loadToken(),
    isLoading: false,
  });

  const setAuthentication = useCallback(() => {
    setAuthState({ isAuthenticated: true, isLoading: false });
  }, []);

  const resetAuthentication = useCallback(() => {
    setAuthState({ isAuthenticated: false, isLoading: false });
  }, []);

  return (
    <AuthContext.Provider
      value={{ ...authState, setAuthentication, resetAuthentication }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

export const ProtectedRoute = ({ children, redirectPath = "/login" }) => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) return <div>Loading...</div>;

  return isAuthenticated ? (
    children || <Outlet />
  ) : (
    <Navigate to={redirectPath} replace />
  );
};
