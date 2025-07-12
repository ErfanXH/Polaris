import { createContext, useState, useContext, useCallback } from "react";
import CookieManager from "../managers/CookieManager";
import { Navigate } from "react-router-dom";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [authState, setAuthState] = useState({
    isAuthenticated: !!CookieManager.loadToken(),
    isAdmin: CookieManager.loadIsAdmin() || false,
    isLoading: false,
  });

  const setAuthentication = useCallback(() => {
    const isAdmin = CookieManager.loadIsAdmin();
    setAuthState({
      isAuthenticated: true,
      isAdmin: CookieManager.loadIsAdmin() || false,
      isLoading: false,
    });
  }, []);

  const resetAuthentication = useCallback(() => {
    setAuthState({
      isAuthenticated: false,
      isAdmin: false,
      isLoading: false,
    });
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

export const ProtectedRoute = ({
  children,
  redirectPath = "/login",
  adminOnly = false,
}) => {
  const { isAuthenticated, isAdmin, isLoading } = useAuth();

  if (isLoading) return <div>Loading...</div>;

  if (adminOnly && isAdmin && isAuthenticated) {
    return children || <Outlet />;
  } else if (!adminOnly && isAuthenticated) {
    return children || <Outlet />;
  } else {
    return <Navigate to={redirectPath} replace />;
  }
};
