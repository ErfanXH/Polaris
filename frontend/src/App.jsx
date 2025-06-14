import {
  createBrowserRouter,
  RouterProvider,
  Navigate,
  Outlet,
} from "react-router-dom";
import { useState, useEffect } from "react";
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
import { isAuthenticated } from "./utils/AuthManager.js";
import { createAppTheme } from "./utils/ThemeManager.js";
import { ToastContainer } from "react-toastify";
import UserLayout from "./pages/UserLayout/index.jsx";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
const ProtectedRoute = ({
  isAuthenticated,
  redirectPath = "/login",
  children,
}) => {
  if (!isAuthenticated) {
    return <Navigate to={redirectPath} replace />;
  }

  return children ? children : <Outlet />;
};

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 2, //retry failed queries twice
    },
  },
});

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
      <ProtectedRoute isAuthenticated={isAuthenticated}>
        <UserLayout />
      </ProtectedRoute>
    ),

    children: [
      {
        path: "/user/dashboard",
        element: <Dashboard />,
        index: true,
      },
      {
        path: "/user/profile",
        element: <Profile />,
      },
      {
        path: "/user/map",
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
    <QueryClientProvider client={queryClient}>
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
        <RouterProvider router={router} />
      </ThemeProvider>
    </QueryClientProvider>
  );
}
