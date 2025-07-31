import { useTheme, Box } from "@mui/material";

const PhoneMockup = ({ imageUrl, altText }) => {
  const theme = useTheme();
  return (
    <Box
      sx={{
        position: "relative",
        mx: "auto",
        borderColor: theme.palette.mode === "dark" ? "grey.800" : "grey.800",
        bgcolor: theme.palette.mode === "dark" ? "grey.900" : "grey.900",
        borderWidth: "14px",
        borderRadius: "2.5rem",
        height: "600px",
        width: "300px",
        boxShadow: 3,
      }}
    >
      <Box
        sx={{
          width: "140px",
          height: "18px",
          bgcolor: "grey.800",
          position: "absolute",
          top: 0,
          borderRadius: "0 0 1rem 1rem",
          left: "50%",
          transform: "translateX(-50%)",
          zIndex: 1,
        }}
      ></Box>
      <Box
        sx={{
          height: "46px",
          width: "3px",
          bgcolor: "grey.800",
          position: "absolute",
          left: "-17px",
          top: "124px",
          borderRadius: "0.5rem 0 0 0.5rem",
        }}
      ></Box>
      <Box
        sx={{
          height: "46px",
          width: "3px",
          bgcolor: "grey.800",
          position: "absolute",
          left: "-17px",
          top: "178px",
          borderRadius: "0.5rem 0 0 0.5rem",
        }}
      ></Box>
      <Box
        sx={{
          height: "64px",
          width: "3px",
          bgcolor: "grey.800",
          position: "absolute",
          right: "-17px",
          top: "142px",
          borderRadius: "0 0.5rem 0.5rem 0",
        }}
      ></Box>
      <Box
        sx={{
          borderRadius: "2rem",
          overflow: "hidden",
          width: "100%",
          height: "100%",
          bgcolor: theme.palette.background.default,
        }}
      >
        <img
          src={imageUrl}
          style={{
            width: "100%",
            height: "100%",
            objectFit: "cover",
          }}
          alt={altText}
          onError={(e) => {
            e.target.onerror = null;
            e.target.src =
              "https://placehold.co/300x600/1e1e1e/e0e0e0?text=App+Preview";
          }}
        />
      </Box>
    </Box>
  );
};

export default PhoneMockup;
