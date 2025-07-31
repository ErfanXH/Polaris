import { createBrowserRouter, Navigate, Outlet } from "react-router-dom";

import SignUp from "./pages/SignUp";
import Login from "./pages/Login";
import ResetPassword from "./pages/ResetPassword";
import Verify from "./pages/Verify";
import Dashboard from "./pages/Dashboard";
import Map from "./pages/Map";
import Landing from "./pages/Landing";
import NotFound from "./pages/NotFound";
import UserLayout from "./pages/UserLayout";
import { ProtectedRoute } from "./context/Authorization";
import UserList from "./pages/UserList";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Landing />,
  },
  {
    path: "/app",
    element: (
      <ProtectedRoute>
        <UserLayout />
      </ProtectedRoute>
    ),
    children: [
      { index: true, element: <Dashboard /> },
      { path: "dashboard", element: <Dashboard /> },
      { path: "map", element: <Map /> },
      {
        path: "user-list",
        element: (
          <ProtectedRoute redirectPath="/app" adminOnly={true}>
            <UserList />
          </ProtectedRoute>
        ),
      },
    ],
  },
  { path: "/login", element: <Login /> },
  { path: "/sign-up", element: <SignUp /> },
  { path: "/reset-password", element: <ResetPassword /> },
  { path: "/verify", element: <Verify /> },
  { path: "*", element: <NotFound /> },
]);
