import {
  Box,
  Button,
  Container,
  Grid,
  Typography,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Paper,
} from "@mui/material";
import { Download, Android, CheckCircle } from "@mui/icons-material";
import versionInfo from "./VersionInfo";

const Downloads = ({ theme, isDark }) => {
  return (
    <Box
      id="download"
      sx={{
        py: 8,
        background: isDark
          ? "linear-gradient(135deg, #1a1a1a 0%, #121212 100%)"
          : "linear-gradient(135deg, #f0f0f0 0%, #e0e0e0 100%)",
        borderTop: `1px solid ${theme.palette.divider}`,
      }}
    >
      <Container maxWidth="lg">
        <Grid container spacing={4} alignItems="center">
          <Grid>
            <Typography
              variant="h3"
              component="h2"
              sx={{
                fontWeight: 700,
                mb: 2,
                color: theme.palette.text.primary,
              }}
            >
              Download Polaris
            </Typography>

            <Typography
              variant="body1"
              sx={{
                mb: 4,
                color: theme.palette.text.secondary,
              }}
            >
              Get the latest version of Polaris Network Analyzer for Android.
            </Typography>

            <Paper
              sx={{
                p: 3,
                mb: 4,
                bgcolor: theme.palette.custom.cardBg,
                borderRadius: 2,
                borderLeft: `4px solid ${theme.palette.primary.main}`,
              }}
            >
              <List dense>
                <ListItem>
                  <ListItemIcon>
                    <Android color="primary" />
                  </ListItemIcon>
                  <ListItemText
                    primary="Version"
                    secondary={versionInfo.currentVersion}
                    secondaryTypographyProps={{ color: "text.primary" }}
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <Android color="primary" />
                  </ListItemIcon>
                  <ListItemText
                    primary="Release Date"
                    secondary={versionInfo.releaseDate}
                    secondaryTypographyProps={{ color: "text.primary" }}
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <Android color="primary" />
                  </ListItemIcon>
                  <ListItemText
                    primary="File Size"
                    secondary={versionInfo.fileSize}
                    secondaryTypographyProps={{ color: "text.primary" }}
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon>
                    <Android color="primary" />
                  </ListItemIcon>
                  <ListItemText
                    primary="Requirements"
                    secondary={versionInfo.requirements}
                    secondaryTypographyProps={{ color: "text.primary" }}
                  />
                </ListItem>
              </List>
            </Paper>

            <Button
              variant="contained"
              color="primary"
              size="large"
              startIcon={<Download />}
              component="a"
              href="/download/polaris-network-analyzer.apk"
              download="polaris-network-analyzer.apk"
              sx={{
                px: 6,
                py: 2,
                fontWeight: 700,
                fontSize: "1.1rem",
                boxShadow: 3,
                "&:hover": {
                  transform: "scale(1.03)",
                  transition: "transform 0.3s ease-in-out",
                },
              }}
            >
              Download Now
            </Button>
          </Grid>

          <Grid>
            <Paper
              sx={{
                p: 4,
                bgcolor: theme.palette.custom.cardBg,
                borderRadius: 2,
                height: "100%",
              }}
            >
              <Typography
                variant="h5"
                component="h3"
                sx={{
                  mb: 3,
                  fontWeight: 600,
                  display: "flex",
                  alignItems: "center",
                  gap: 1,
                }}
              >
                <CheckCircle color="primary" /> Installation Guide
              </Typography>

              <List>
                <ListItem sx={{ alignItems: "flex-start", px: 0 }}>
                  <ListItemIcon sx={{ minWidth: 32, mt: "4px" }}>
                    <Typography color="primary">1.</Typography>
                  </ListItemIcon>
                  <ListItemText
                    primary="Download the APK file"
                    secondary="Tap the download button to get the installation file"
                  />
                </ListItem>
                <ListItem sx={{ alignItems: "flex-start", px: 0 }}>
                  <ListItemIcon sx={{ minWidth: 32, mt: "4px" }}>
                    <Typography color="primary">2.</Typography>
                  </ListItemIcon>
                  <ListItemText
                    primary="Enable Unknown Sources"
                    secondary="Go to Settings → Security → Enable 'Install unknown apps'"
                  />
                </ListItem>
                <ListItem sx={{ alignItems: "flex-start", px: 0 }}>
                  <ListItemIcon sx={{ minWidth: 32, mt: "4px" }}>
                    <Typography color="primary">3.</Typography>
                  </ListItemIcon>
                  <ListItemText
                    primary="Install the APK"
                    secondary="Open the downloaded file and follow the installation prompts"
                  />
                </ListItem>
                <ListItem sx={{ alignItems: "flex-start", px: 0 }}>
                  <ListItemIcon sx={{ minWidth: 32, mt: "4px" }}>
                    <Typography color="primary">4.</Typography>
                  </ListItemIcon>
                  <ListItemText
                    primary="Launch and Configure"
                    secondary="Open Polaris and configure your preferred settings"
                  />
                </ListItem>
              </List>

              <Typography
                variant="body2"
                color="text.secondary"
                sx={{ mt: 2, fontStyle: "italic" }}
              >
                Note: This app is not available on Google Play Store due to
                network measurement restrictions.
              </Typography>
            </Paper>
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
};

export default Downloads;
