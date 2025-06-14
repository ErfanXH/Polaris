import { useState } from "react";
import { Outlet, useNavigate } from "react-router-dom";
import {
  Box,
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  AppBar,
  IconButton,
  Typography,
  useTheme,
  useMediaQuery,
  CssBaseline,
  Divider,
} from "@mui/material";
import {
  Dashboard as DashboardIcon,
  Person as PersonIcon,
  Map as MapIcon,
  Logout as LogoutIcon,
  Menu as MenuIcon,
  Settings as SettingsIcon,
} from "@mui/icons-material";
import CookieManager from "../../managers/CookieManager";
import { useAuth } from "../../App";

const drawerWidth = 240;
const collapsedDrawerWidth = 72;

export default function UserLayout() {
  const theme = useTheme();
  const navigate = useNavigate();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const [mobileOpen, setMobileOpen] = useState(false);
  const [collapsed, setCollapsed] = useState(false);
  const { resetAuthentication } = useAuth();

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const toggleCollapse = () => {
    setCollapsed(!collapsed);
  };

  const menuItems = [
    { text: "Dashboard", icon: <DashboardIcon />, path: "/user/dashboard" },
    { text: "Profile", icon: <PersonIcon />, path: "/user/profile" },
    { text: "Map", icon: <MapIcon />, path: "/user/map" },
    {
      text: "Sign Out",
      icon: <LogoutIcon />,
      func: () => {
        CookieManager.RemoveToken();
        resetAuthentication();
        navigate("/login");
      },
    },
  ];

  const drawer = (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        height: "100%",
        overflow: "hidden",
        backgroundColor: theme.palette.background.paper,
        marginTop: isMobile ? 7 : 0,
      }}
    >
      {/* Logo Section */}
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: collapsed ? "center" : "flex-start",
          p: collapsed ? 2 : 3,
          borderBottom: `1px solid ${theme.palette.custom.border}`,
          minHeight: 64, // Fixed height for logo area
        }}
      >
        <Box
          component="img"
          src="/logo_icon.svg"
          alt="Company Logo"
          sx={{
            width: collapsed ? 40 : "auto",
            height: collapsed ? 40 : 48,
            maxWidth: collapsed ? "100%" : 120,
            objectFit: "contain",
            mr: collapsed ? 0 : 2,
          }}
        />
        {!collapsed && (
          <Box
            component="img"
            src="/content.svg"
            alt="Company Name"
            sx={{
              height: 24,
              width: "auto",
              maxWidth: 120,
              objectFit: "contain",
            }}
          />
        )}
      </Box>

      <Divider />

      <List sx={{ flexGrow: 1 }}>
        {menuItems.slice(0, -1).map((item) => (
          <ListItem key={item.text} disablePadding sx={{ display: "block" }}>
            <ListItemButton
              onClick={() => {
                item.func ? item.func() : navigate(item.path);
              }}
              sx={{
                minHeight: 48,
                justifyContent: collapsed ? "center" : "initial",
                px: 2.5,
                "&:hover": {
                  backgroundColor: theme.palette.action.hover,
                },
                "&.Mui-selected": {
                  backgroundColor: theme.palette.action.selected,
                },
              }}
            >
              <ListItemIcon
                sx={{
                  minWidth: 0,
                  mr: collapsed ? 0 : 3,
                  justifyContent: "center",
                  color: theme.palette.text.secondary,
                }}
              >
                {item.icon}
              </ListItemIcon>
              {!collapsed && (
                <ListItemText
                  primary={item.text}
                  primaryTypographyProps={{
                    color: theme.palette.text.primary,
                  }}
                />
              )}
            </ListItemButton>
          </ListItem>
        ))}
      </List>

      <Divider />

      <List>
        <ListItem disablePadding sx={{ display: "block" }}>
          <ListItemButton
            onClick={() => {
              menuItems[menuItems.length - 1].func();
            }}
            sx={{
              minHeight: 48,
              justifyContent: collapsed ? "center" : "initial",
              px: 2.5,
              "&:hover": {
                backgroundColor: theme.palette.action.hover,
              },
            }}
          >
            <ListItemIcon
              sx={{
                minWidth: 0,
                mr: collapsed ? 0 : 3,
                justifyContent: "center",
                color: theme.palette.error.main,
              }}
            >
              <LogoutIcon />
            </ListItemIcon>
            {!collapsed && (
              <ListItemText
                primary="Sign Out"
                primaryTypographyProps={{
                  color: theme.palette.error.main,
                }}
              />
            )}
          </ListItemButton>
        </ListItem>
      </List>
    </Box>
  );

  return (
    <Box sx={{ display: "flex" }}>
      <CssBaseline />

      {/* AppBar */}
      <AppBar
        position="fixed"
        sx={{
          width: {
            xs: "100%",
            sm: `calc(100% - ${
              collapsed ? collapsedDrawerWidth : drawerWidth
            }px)`,
          },
          ml: { sm: `${collapsed ? collapsedDrawerWidth : drawerWidth}px` },
          zIndex: theme.zIndex.drawer + 1,
          backgroundColor: theme.palette.custom.navbarBg,
          borderBottom: `1px solid ${theme.palette.custom.border}`,
          boxShadow: "none",
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={isMobile ? handleDrawerToggle : toggleCollapse}
            sx={{
              mr: 2,
              color: theme.palette.text.primary, // Ensures icon is visible in both modes
            }}
          >
            <MenuIcon />
          </IconButton>
          <Typography
            variant="h6"
            noWrap
            component="div"
            sx={{
              flexGrow: 1,
              fontWeight: 600,
              color: theme.palette.text.primary, // Ensures text is visible in both modes
            }}
          >
            Network Monitoring Panel
          </Typography>
        </Toolbar>
      </AppBar>

      {/* Sidebar */}
      <Box
        component="nav"
        sx={{
          width: {
            sm: collapsed ? collapsedDrawerWidth : drawerWidth,
          },
          flexShrink: { sm: 0 },
        }}
      >
        {/* Mobile Drawer */}
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{
            keepMounted: true,
          }}
          sx={{
            display: { xs: "block", sm: "none" },
            "& .MuiDrawer-paper": {
              boxSizing: "border-box",
              width: drawerWidth,
              backgroundColor: theme.palette.background.paper,
              borderRight: `1px solid ${theme.palette.custom.border}`,
            },
          }}
        >
          {drawer}
        </Drawer>

        {/* Desktop Drawer */}
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: "none", sm: "block" },
            "& .MuiDrawer-paper": {
              boxSizing: "border-box",
              width: collapsed ? collapsedDrawerWidth : drawerWidth,
              backgroundColor: theme.palette.background.paper,
              borderRight: `1px solid ${theme.palette.custom.border}`,
              transition: theme.transitions.create("width", {
                easing: theme.transitions.easing.sharp,
                duration: theme.transitions.duration.enteringScreen,
              }),
              overflowX: "hidden",
            },
          }}
          open
        >
          {drawer}
        </Drawer>
      </Box>

      {/* Main Content */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 1,
          width: {
            xs: "100%",
            sm: `calc(100% - ${
              collapsed ? collapsedDrawerWidth : drawerWidth
            }px)`,
          },
          backgroundColor: theme.palette.background.default,
          minHeight: "100vh",
        }}
      >
        <Toolbar />
        <Box
          sx={{
            backgroundColor: theme.palette.background.paper,
            borderRadius: 1,
            boxShadow: theme.shadows[1],
            p: { xs: 1, sm: 3 },
            minHeight: "calc(100vh - 64px - 48px)",
            border: `1px solid ${theme.palette.custom.border}`,
          }}
        >
          <Outlet />
        </Box>
      </Box>
    </Box>
  );
}
