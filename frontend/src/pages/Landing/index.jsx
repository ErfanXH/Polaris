import {
  Box,
  Button,
  Container,
  Grid,
  Typography,
  useTheme,
  Divider,
  AppBar,
  Toolbar,
  IconButton,
  Menu,
  MenuItem,
} from "@mui/material";
import { Download, Menu as MenuIcon } from "@mui/icons-material";
import { useState } from "react";
import Features from "./components/Features";
import Hero from "./components/Hero";
import Faq from "./components/Faq";
import Downloads from "./components/Downloads";
import logo from "/logo.svg";

const PolarisLanding = () => {
  const theme = useTheme();
  const isDark = theme.palette.mode === "dark";
  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  return (
    <Box sx={{ bgcolor: theme.palette.background.default }}>
      <AppBar
        position="sticky"
        elevation={0}
        sx={{
          bgcolor: theme.palette.custom.navbarBg,
          borderBottom: `1px solid ${theme.palette.divider}`,
          backdropFilter: "blur(10px)",
          background: isDark
            ? "rgba(30, 30, 30, 0.8)"
            : "rgba(245, 245, 245, 0.8)",
        }}
      >
        <Container maxWidth="lg">
          <Toolbar disableGutters>
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                flexGrow: 1,
              }}
            >
              <Box
                component="img"
                src={logo}
                alt="Polaris Logo"
                sx={{
                  height: { xs: 28, md: 36 },
                  width: "auto",
                  mr: 4,
                }}
              />

              <Box sx={{ display: { xs: "none", md: "flex" }, gap: 1 }}>
                <Button color="inherit" href="#features">
                  Features
                </Button>
                <Button color="inherit" href="#faq">
                  FAQ
                </Button>
                <Button color="inherit" href="#download">
                  Download
                </Button>
                <Button color="inherit" href="/login">
                  Login
                </Button>
                <Button color="inherit" href="/dashboard">
                  Dashboard
                </Button>
              </Box>
            </Box>

            <Box>
              <Button
                variant="contained"
                color="primary"
                startIcon={<Download />}
                component="a"
                href="#download"
                sx={{
                  px: 3,
                  fontWeight: 600,
                  display: { xs: "none", sm: "none", md: "flex" },
                }}
              >
                Download APK
              </Button>

              <IconButton
                size="large"
                edge="end"
                color="inherit"
                aria-label="menu"
                onClick={handleMenuOpen}
                sx={{ display: { md: "none" } }}
              >
                <MenuIcon />
              </IconButton>

              <Menu
                anchorEl={anchorEl}
                open={open}
                onClose={handleMenuClose}
                PaperProps={{
                  elevation: 0,
                  sx: {
                    bgcolor: theme.palette.custom.cardBg,
                    border: `1px solid ${theme.palette.divider}`,
                    mt: 1.5,
                    minWidth: 200,
                  },
                }}
              >
                <MenuItem onClick={handleMenuClose} href="#features">
                  Features
                </MenuItem>
                <MenuItem onClick={handleMenuClose} href="#faq">
                  FAQ
                </MenuItem>
                <MenuItem onClick={handleMenuClose} href="#download">
                  Download
                </MenuItem>
                <MenuItem onClick={handleMenuClose} href="/login">
                  Login
                </MenuItem>
                <MenuItem onClick={handleMenuClose} href="/dashboard">
                  Dashboard
                </MenuItem>
              </Menu>
            </Box>
          </Toolbar>
        </Container>
      </AppBar>

      <Hero theme={theme} isDark={isDark} />

      <Features theme={theme} isDark={isDark} />

      <Faq theme={theme} isDark={isDark} />

      <Downloads theme={theme} isDark={isDark} />

      <Box
        sx={{
          py: 4,
          bgcolor: theme.palette.custom.navbarBg,
          borderTop: `1px solid ${theme.palette.divider}`,
        }}
      >
        <Container maxWidth="lg">
          <Grid>
            <Box
              component="img"
              src={logo}
              alt="Polaris Logo"
              sx={{
                height: 32,
                width: "auto",
                mb: 2,
              }}
            />
            <Typography variant="body2" color="text.secondary">
              Professional mobile network analysis tools for engineers,
              technicians, and enthusiasts.
            </Typography>
          </Grid>
          <Divider sx={{ my: 4 }} />
          <Typography variant="body2" color="text.secondary" align="center">
            © {new Date().getFullYear()} Polaris Network Analyzer. All rights
            reserved.
          </Typography>
        </Container>
      </Box>
    </Box>
  );
};

export default PolarisLanding;
