import { createBrowserRouter, Navigate, Outlet } from "react-router-dom";

import SignUp from "./pages/SignUp";
import Login from "./pages/Login";
import ResetPassword from "./pages/ResetPassword";
import Verify from "./pages/Verify";
import Dashboard from "./pages/Dashboard";
import Map from "./pages/Map";
import Landing from "./pages/Landing";
import Profile from "./pages/Profile";
import NotFound from "./pages/NotFound";
import UserLayout from "./pages/UserLayout";
import { ProtectedRoute } from "./context/Authorization";

export const router = createBrowserRouter([
  { path: "/", element: <Landing /> },
  { path: "/login", element: <Login /> },
  { path: "/sign-up", element: <SignUp /> },
  { path: "/reset-password", element: <ResetPassword /> },
  { path: "/verify", element: <Verify /> },
  {
    path: "/user",
    element: (
      <ProtectedRoute>
        <UserLayout />
      </ProtectedRoute>
    ),
    children: [
      { path: "dashboard", element: <Dashboard />, index: true },
      { path: "profile", element: <Profile /> },
      { path: "map", element: <Map /> },
    ],
  },
  { path: "*", element: <NotFound /> },
]);
