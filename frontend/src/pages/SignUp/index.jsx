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
import SignUpManager from "../../managers/SignUpManager";

const signUpSchema = z
  .object({
    email: z
      .string()
      .min(1, "Email is required")
      .email("Invalid email address"),
    phone_number: z
      .string()
      .min(1, "Phone number is required")
      .regex(/^\+?[0-9]{11}$/, "Must be a valid phone number (11 digits)"),
    password: z.string().min(8, "Password must be at least 8 characters"),
    confirm_password: z.string().min(1, "Please confirm your password"),
  })
  .refine((data) => data.password === data.confirm_password, {
    message: "Passwords don't match",
    path: ["confirm_password"],
  });

export default function SignUp() {
  const theme = useTheme();
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(signUpSchema),
  });

  const onSubmit = async (data) => {
    setLoading(true);
    try {
      const response = await SignUpManager.signUp({
        email: data.email,
        phone_number: data.phone_number,
        password: data.password,
      });

      toast.success("Verification Code Sent", {
        autoClose: 3000,
        onClose: () =>
          navigate("/verify", {
            state: {
              numberOrEmail: data.email,
              password: data.password,
              from: "sign-up",
            },
          }),
        pauseOnHover: false,
      });
    } catch (error) {
      toast.error(error || "Sign up failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const toggleConfirmPasswordVisibility = () => {
    setShowConfirmPassword(!showConfirmPassword);
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
          maxWidth: 500,
          mx: "auto",
          width: "100%",
        }}
      >
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

        <Typography
          variant="h5"
          sx={{
            textAlign: "center",
            mb: 4,
            fontWeight: "bold",
            color: "text.primary",
          }}
        >
          Create your account
        </Typography>

        <TextField
          {...register("phone_number")}
          label="Phone Number"
          variant="outlined"
          fullWidth
          margin="normal"
          autoComplete="tel"
          error={!!errors.phone_number}
          helperText={errors.phone_number?.message}
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

        <TextField
          {...register("email")}
          label="Email Address"
          variant="outlined"
          fullWidth
          margin="normal"
          autoComplete="email"
          error={!!errors.email}
          helperText={errors.email?.message}
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

        <TextField
          {...register("password")}
          label="Password"
          type={showPassword ? "text" : "password"}
          variant="outlined"
          fullWidth
          margin="normal"
          autoComplete="new-password"
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
                  onClick={togglePasswordVisibility}
                  edge="end"
                  tabIndex={-1}
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

        <TextField
          {...register("confirm_password")}
          label="Confirm Password"
          type={showConfirmPassword ? "text" : "password"}
          variant="outlined"
          fullWidth
          margin="normal"
          autoComplete="new-password"
          error={!!errors.confirm_password}
          helperText={errors.confirm_password?.message}
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
                  aria-label="toggle confirm password visibility"
                  onClick={toggleConfirmPasswordVisibility}
                  edge="end"
                  tabIndex={-1}
                >
                  {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                </IconButton>
              </InputAdornment>
            ),
          }}
          InputLabelProps={{
            sx: { color: "text.secondary" },
          }}
        />

        <Button
          type="submit"
          variant="contained"
          fullWidth
          size="large"
          disabled={loading}
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
          {loading ? "Creating Account..." : "Sign Up"}
        </Button>

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

        <Stack
          direction="row"
          justifyContent="center"
          alignItems="center"
          spacing={1}
          sx={{ mt: 3 }}
        >
          <Typography variant="body2" sx={{ color: "text.secondary" }}>
            Already have an account?
          </Typography>
          <Link
            component={RouterLink}
            to="/login"
            sx={{
              color: "primary.main",
              "&:hover": {
                color: "primary.dark",
              },
            }}
          >
            Sign In
          </Link>
        </Stack>
      </Box>
    </Container>
  );
}
