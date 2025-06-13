import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import {
  Box,
  Button,
  Container,
  TextField,
  Typography,
  Link,
  Stack,
  Divider,
  useTheme,
  IconButton,
  InputAdornment,
} from "@mui/material";
import { Link as RouterLink, useNavigate } from "react-router-dom";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import Logo from "/logo.svg";
import { toast } from "react-toastify";
import LoginManager from "../../managers/LoginManager";

// Zod validation schema
const loginSchema = z.object({
  number_or_email: z
    .string()
    .min(1, "Email or phone number is required")
    .refine(
      (value) => {
        // Check if it's a valid email OR a valid phone number
        const isEmail = z.string().email().safeParse(value).success;
        const isPhone = /^\+?[0-9]{11}$/.test(value); // Basic international phone format
        return isEmail || isPhone;
      },
      {
        message: "Must be a valid email or phone number",
      }
    ),
  password: z.string().min(8, "Password must be at least 8 characters"),
});

export default function Login() {
  const theme = useTheme();
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data) => {
    setLoading(true);
    try {
      const response = await LoginManager.login(data);
      toast.success("Login Successful", {
        autoClose: 3000,
        onClose: () => navigate("/user/dashboard"),
        pauseOnHover: false,
      });
    } catch (error) {
      if (error?.status === 401) {
        navigate("/verify", {
          state: {
            numberOrEmail: data.number_or_email,
            password: data.password,
            from: "login", // Indicate this came from login
          },
        });
      } else {
        toast.error(error || "Login failed. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleClickShowPassword = () => {
    setShowPassword(!showPassword);
  };

  return (
    <Container
      maxWidth="sm"
      sx={{
        minHeight: "100vh",
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        py: 2,
        backgroundColor: "background.default",
        marginInline: "auto",
      }}
    >
      <Box
        component="form"
        onSubmit={handleSubmit(onSubmit)}
        sx={{
          backgroundColor: "background.paper",
          p: { xs: 3, sm: 4, md: 6 },
          borderRadius: theme.shape.borderRadius,
          boxShadow: theme.shadows[3],
          maxWidth: 500, // Added maxWidth for better centering
          mx: "auto", // Center horizontally when viewport is wider
          width: "100%", // Ensure it takes full width on mobile
        }}
      >
        {/* Logo Section */}
        <Box
          sx={{
            display: "flex",
            justifyContent: "center",
            mb: 4,
          }}
        >
          <img
            src={Logo}
            alt="Logo"
            style={{
              height: "auto",
              width: "100%",
              maxWidth: 200,
              objectFit: "contain",
            }}
          />
        </Box>

        {/* Title */}
        <Typography
          variant="h5"
          sx={{
            textAlign: "center",
            mb: 4,
            fontWeight: "bold",
            color: "text.primary",
          }}
        >
          Sign in to your account
        </Typography>

        {/* Email Field */}
        <TextField
          {...register("number_or_email")} // Must match schema field name
          label="Email Address or Phone Number"
          name="number_or_email"
          variant="outlined"
          fullWidth
          margin="normal"
          autoComplete="email tel-national" // Better for login fields
          error={!!errors.number_or_email} // Must match schema field name
          helperText={errors.number_or_email?.message} // Must match schema field name
          sx={{
            mb: 3,
            "& input:-webkit-autofill": {
              WebkitBoxShadow: `0 0 0 100px ${theme.palette.background.paper} inset`,
              WebkitTextFillColor: theme.palette.text.primary,
              borderRadius: "inherit",
            },
          }}
          InputLabelProps={{
            sx: { color: "text.secondary" },
          }}
        />

        {/* Password Field with Toggle */}
        <TextField
          {...register("password")}
          label="Password"
          name="password"
          type={showPassword ? "text" : "password"}
          variant="outlined"
          fullWidth
          margin="normal"
          autoComplete="current-password"
          error={!!errors.password}
          helperText={errors.password?.message}
          sx={{
            mb: 3,
            "& input:-webkit-autofill": {
              WebkitBoxShadow: `0 0 0 100px ${theme.palette.background.paper} inset`,
              WebkitTextFillColor: theme.palette.text.primary,
              borderRadius: "inherit",
            },
          }}
          InputProps={{
            endAdornment: (
              <InputAdornment position="end">
                <IconButton
                  aria-label="toggle password visibility"
                  onClick={handleClickShowPassword}
                  edge="end"
                >
                  {showPassword ? <VisibilityOff /> : <Visibility />}
                </IconButton>
              </InputAdornment>
            ),
          }}
          InputLabelProps={{
            sx: { color: "text.secondary" },
          }}
        />

        {/* Forgot Password Link */}
        <Box
          sx={{
            display: "flex",
            justifyContent: "flex-end",
            mb: 4,
          }}
        >
          <Link
            component={RouterLink}
            to="/reset-password"
            sx={{
              fontSize: "0.875rem",
              color: "primary.main",
              "&:hover": {
                color: "primary.dark",
              },
            }}
          >
            Forgot Password?
          </Link>
        </Box>

        {/* Submit Button */}
        <Button
          type="submit"
          variant="contained"
          fullWidth
          size="large"
          sx={{
            py: 2,
            mb: 3,
            backgroundColor: "primary.main",
            color: theme.palette.getContrastText(theme.palette.primary.main),
            "&:hover": {
              backgroundColor: "primary.dark",
            },
          }}
        >
          {loading ? "Signing In..." : "Sign In"}
        </Button>

        {/* Divider */}
        <Divider
          sx={{
            my: 3,
            color: "text.secondary",
            "&::before, &::after": {
              borderColor: "divider",
            },
          }}
        >
          OR
        </Divider>

        {/* Sign Up Link */}
        <Stack
          direction="row"
          justifyContent="center"
          alignItems="center"
          spacing={1}
          sx={{ mt: 3 }}
        >
          <Typography variant="body2" sx={{ color: "text.secondary" }}>
            Don't have an account?
          </Typography>
          <Link
            component={RouterLink}
            to="/sign-up"
            sx={{
              color: "primary.main",
              "&:hover": {
                color: "primary.dark",
              },
            }}
          >
            Sign Up
          </Link>
        </Stack>
      </Box>
    </Container>
  );
}
