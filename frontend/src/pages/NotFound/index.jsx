import { Box, Button, Container, Typography, useTheme } from "@mui/material";
import { Link as RouterLink } from "react-router-dom";
import SensorsOffRoundedIcon from "@mui/icons-material/SensorsOffRounded";

const NotFound = () => {
  const theme = useTheme();
  const isDark = theme.palette.mode === "dark";

  return (
    <Container
      maxWidth="md"
      sx={{
        height: "100vh",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        textAlign: "center",
        py: 8,
      }}
    >
      <SensorsOffRoundedIcon
        sx={{
          position: "relative",
          width: 200,
          height: 200,
          mb: 2.5,
          color: theme.palette.error.main,
        }}
      />
      <Typography
        variant="h1"
        sx={{
          fontSize: { xs: "2.25rem", sm: "2.75rem", md: "3.5rem" },
          fontWeight: 700,
          mb: 2,
          color: theme.palette.text.primary,
        }}
      >
        Signal Lost
      </Typography>

      <Typography
        variant="h5"
        sx={{
          mb: 3,
          color: theme.palette.text.secondary,
          maxWidth: 600,
        }}
      >
        We couldn't connect to the page you're looking for. It might have been
        moved or no longer exists.
      </Typography>

      <Box
        sx={{
          display: "flex",
          gap: 2,
          flexDirection: { xs: "column", sm: "row" },
        }}
      >
        <Button
          component={RouterLink}
          to="/"
          variant="contained"
          color="primary"
          size="large"
          sx={{
            px: 4,
            py: 1.5,
            fontWeight: 500,
          }}
        >
          Return to Dashboard
        </Button>
        <Button
          component={RouterLink}
          to="/map"
          variant="outlined"
          color="primary"
          size="large"
          sx={{
            px: 4,
            py: 1.5,
            fontWeight: 500,
          }}
        >
          View Network Map
        </Button>
      </Box>

      <Typography
        variant="body2"
        sx={{
          mt: 6,
          color: theme.palette.text.secondary,
        }}
      >
        Error code: 404 - Page not found
      </Typography>
    </Container>
  );
};

export default NotFound;
