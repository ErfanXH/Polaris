import {
  createBrowserRouter,
  RouterProvider,
  Navigate,
  Outlet,
} from "react-router-dom";
import {
  useState,
  useEffect,
  createContext,
  useContext,
  useCallback,
} from "react";
import { ThemeProvider, CssBaseline } from "@mui/material";
import SignUp from "./pages/SignUp/index";
import Login from "./pages/Login/index";
import ResetPassword from "./pages/ResetPassword/index";
import Verify from "./pages/Verify/index";
import Dashboard from "./pages/Dashboard/index";
import Map from "./pages/Map/index";
import Landing from "./pages/Landing/index.jsx";
import Profile from "./pages/Profile/index.jsx";
import NotFound from "./pages/NotFound/index.jsx";
import "./index.css";
import "react-toastify/dist/ReactToastify.css";
import { createAppTheme } from "./utils/ThemeManager.js";
import { ToastContainer } from "react-toastify";
import UserLayout from "./pages/UserLayout/index.jsx";
import CookieManager from "./managers/CookieManager.js";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [authState, setAuthState] = useState({
    isAuthenticated: CookieManager.LoadToken() ? true : false,
    isLoading: false,
  });

  const setAuthentication = useCallback((token) => {
    setAuthState({
      isAuthenticated: true,
      isLoading: false,
    });
  }, []);

  const resetAuthentication = useCallback(() => {
    setAuthState({
      isAuthenticated: false,
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

export const ProtectedRoute = ({ children, redirectPath = "/login" }) => {
  const { isAuthenticated, isLoading } = useAuth();
  console.log(isAuthenticated);
  console.log(isLoading);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to={redirectPath} replace />;
  }

  return children ? children : <Outlet />;
};

<<<<<<< Updated upstream
=======
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 2,
    },
  },
});

>>>>>>> Stashed changes
const router = createBrowserRouter([
  {
    path: "/",
    element: <Landing />,
  },
  {
    path: "/login",
    element: <Login />,
  },
  {
    path: "/sign-up",
    element: <SignUp />,
  },
  {
    path: "/reset-password",
    element: <ResetPassword />,
  },
  {
    path: "/verify",
    element: <Verify />,
  },
  {
    path: "/user",
    element: (
      <ProtectedRoute>
        <UserLayout />
      </ProtectedRoute>
    ),
    children: [
      {
        path: "dashboard",
        element: <Dashboard />,
        index: true,
      },
      {
        path: "profile",
        element: <Profile />,
      },
      {
        path: "map",
        element: <Map />,
      },
    ],
  },
  {
    path: "*",
    element: <NotFound />,
  },
]);

export default function App() {
  const [theme, setTheme] = useState(createAppTheme());

  // Listen for system color scheme changes
  useEffect(() => {
    const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");

    const handleChange = (e) => {
      const newMode = e.matches ? "dark" : "light";
      setTheme(createAppTheme(newMode));
    };

    mediaQuery.addEventListener("change", handleChange);
    return () => mediaQuery.removeEventListener("change", handleChange);
  }, []);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <ToastContainer
        position="top-center"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="colored"
      />
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
    </ThemeProvider>
  );
}
