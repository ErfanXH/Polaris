import {
  createBrowserRouter,
  RouterProvider,
  Navigate,
  Outlet,
} from "react-router-dom";
import { useState, useEffect } from "react";
import SignUp from "./pages/SignUp/index";
import Login from "./pages/Login/index";
import ResetPassword from "./pages/ResetPassword/index";
import Verify from "./pages/Verify/index";
import Dashboard from "./pages/Dashboard/index";
import Map from "./pages/Map/index";
import "./index.css";
import "react-toastify/dist/ReactToastify.css";
import { isAuthenticated } from "./utils/AuthManager.js";

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

const router = createBrowserRouter([
  {
    path: "/",
    element: <Root />,
  },
  {
    path: "/login",
    element: <Login />,
  },
  {
    path: "/sign_up",
    element: <SignUp />,
  },
  {
    path: "/reset_password",
    element: <ResetPassword />,
  },
  {
    path: "/verify",
    element: <Verify />,
  },
  {
    path: "/user",
  },
  // Protected routes group
  {
    element: <ProtectedRoute isAuthenticated={isAuthenticated} />,
    children: [
      {
        path: "/user/profile",
        element: <Profile />,
      },
      {
        path: "/user/dashboard",
        element: <Dashboard />,
        index,
      },
      {
        path: "/user/map",
        element: <Map />,
      },
    ],
  },
  {
    path: "*",
    element: <ErrorPage />,
  },
]);

export default function App() {
  return (
    <ThemeContext.Provider value={{ isDarkMode }}>
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
    </ThemeContext.Provider>
  );
}
