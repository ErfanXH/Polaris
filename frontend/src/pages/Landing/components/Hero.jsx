import { Container, Box, Chip, Typography, Button } from "@mui/material";
import { Download } from "@mui/icons-material";
import PhoneMockup from "./PhoneMockup";

const Hero = ({ theme, isDark }) => {
  return (
    <Container
      maxWidth="lg"
      sx={{
        py: { xs: 6, md: 10 },
        display: "flex",
        flexDirection: { xs: "column", md: "row" },
        alignItems: "center",
        gap: 6,
      }}
    >
      <Box
        sx={{
          flex: 1,
          textAlign: { xs: "center", md: "left" },
        }}
      >
        <Chip
          label="NOVEL NETWORK ANALYZER"
          color="primary"
          size="medium"
          sx={{ mb: 3 }}
        />
        <Typography
          variant="h2"
          component="h1"
          sx={{
            fontSize: { xs: "2.25rem", md: "3.5rem" },
            lineHeight: 1.2,
            fontWeight: 800,
            mb: 3,
            color: theme.palette.text.primary,
          }}
        >
          Advanced Mobile Network Diagnostics
        </Typography>
        <Typography
          variant="body1"
          sx={{
            fontSize: "1.125rem",
            color: theme.palette.text.secondary,
            mb: 4,
            maxWidth: "600px",
            mx: { xs: "auto", md: 0 },
          }}
        >
          Polaris provides carrier-grade network analysis tools with detailed
          signal metrics, automated testing, and comprehensive reporting for
          professionals and enthusiasts.
        </Typography>
        <Box
          sx={{
            display: "flex",
            gap: 2,
            justifyContent: { xs: "center", md: "flex-start" },
            flexWrap: "wrap",
          }}
        >
          <Button
            variant="contained"
            color="primary"
            href="#download"
            startIcon={<Download />}
            sx={{
              py: 1.5,
              px: 4,
              fontSize: "1.125rem",
              fontWeight: "bold",
              boxShadow: 3,
              bgcolor: theme.palette.primary.main,
              "&:hover": {
                bgcolor: theme.palette.primary.dark,
                transform: "scale(1.05)",
                transition: "transform 0.3s ease-in-out",
              },
            }}
          >
            Download APK
          </Button>
          <Button
            variant="outlined"
            color="primary"
            href="#features"
            sx={{
              py: 1.5,
              px: 4,
              fontSize: "1.125rem",
              fontWeight: 500,
            }}
          >
            Explore Features
          </Button>
        </Box>
      </Box>

      <Box
        sx={{
          flex: 1,
          display: "flex",
          justifyContent: "center",
          mt: { xs: 6, md: 0 },
        }}
      >
        <PhoneMockup
          imageUrl={isDark ? "/phone_dark.jpeg" : "/phone.jpeg"}
          altText="Polaris app main screen showing network analysis"
        />
      </Box>
    </Container>
  );
};

export default Hero;
