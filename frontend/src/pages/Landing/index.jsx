import {
  Box,
  Button,
  Container,
  Grid,
  Typography,
  useTheme,
  List,
  ListItem,
  Divider,
  Link,
  AppBar,
  Toolbar,
  IconButton,
  Menu,
  MenuItem,
} from "@mui/material";
import { Download, WifiTethering, Menu as MenuIcon } from "@mui/icons-material";
import { useState } from "react";
import Features from "./components/Features";
import Hero from "./components/Hero";
import Faq from "./components/Faq";
import Downloads from "./components/Downloads";

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
              <WifiTethering color="primary" sx={{ mr: 1 }} />
              <Typography
                variant="h6"
                sx={{
                  fontWeight: 700,
                  mr: 4,
                }}
              >
                Polaris
              </Typography>

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
                  display: { xs: "none", sm: "flex" },
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
            <Typography
              variant="h6"
              sx={{
                fontWeight: 700,
                mb: 1,
                display: "flex",
                alignItems: "center",
                gap: 1,
              }}
            >
              <WifiTethering color="primary" /> Polaris
            </Typography>
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
