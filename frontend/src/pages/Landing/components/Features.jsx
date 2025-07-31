import {
  Box,
  Container,
  Grid,
  Typography,
  Card,
  CardContent,
  useTheme,
} from "@mui/material";
import FeaturesList from "./FeaturesList";

const Features = () => {
  const theme = useTheme();
  const isDark = theme.palette.mode === "dark";

  return (
    <Box
      id="features"
      sx={{
        py: 8,
        bgcolor: isDark ? "#1a1a1a" : "#f8f8f8",
        borderTop: `1px solid ${theme.palette.divider}`,
        borderBottom: `1px solid ${theme.palette.divider}`,
      }}
    >
      <Container maxWidth="lg">
        <Typography
          variant="h3"
          component="h2"
          sx={{
            textAlign: "center",
            fontWeight: 700,
            mb: 6,
            color: theme.palette.text.primary,
          }}
        >
          Key Features
        </Typography>

        <Box
          sx={{
            display: "grid",
            gridTemplateColumns: {
              xs: "repeat(1, 1fr)",
              sm: "repeat(2, 1fr)",
              md: "repeat(3, 1fr)",
            },
            gap: 4,
          }}
        >
          {FeaturesList.map((feature, index) => (
            <Card
              key={index}
              sx={{
                height: "100%",
                display: "flex",
                flexDirection: "column",
                bgcolor: theme.palette.custom.cardBg,
                transition: "transform 0.2s, box-shadow 0.2s",
                "&:hover": {
                  transform: "translateY(-4px)",
                  boxShadow: 4,
                },
              }}
            >
              <CardContent
                sx={{
                  p: 3,
                  flexGrow: 1,
                  display: "flex",
                  flexDirection: "column",
                }}
              >
                <Box
                  sx={{
                    width: 60,
                    height: 60,
                    borderRadius: "50%",
                    bgcolor: theme.palette.primary.main,
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    mb: 1,
                    color: "#fff",
                  }}
                >
                  {feature.icon}
                </Box>
                <Typography
                  variant="h5"
                  component="h3"
                  sx={{
                    mb: 1,
                    fontWeight: 600,
                    minHeight: "72px",
                    display: "flex",
                    alignItems: "center",
                  }}
                >
                  {feature.title}
                </Typography>
                <Typography
                  variant="body1"
                  color="text.secondary"
                  sx={{
                    flexGrow: 1,
                    minHeight: "80px",
                  }}
                >
                  {feature.description}
                </Typography>
              </CardContent>
            </Card>
          ))}
        </Box>
      </Container>
    </Box>
  );
};

export default Features;
