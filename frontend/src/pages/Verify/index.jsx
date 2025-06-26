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
  useTheme,
  CircularProgress,
} from "@mui/material";
import { Link as RouterLink, useNavigate, useLocation } from "react-router-dom";
import Logo from "/logo.svg";
import { toast } from "react-toastify";
import VerifyManager from "../../managers/VerifyManager";
import { useAuth } from "../../context/Authorization";

// Zod validation schema for 5-digit code
const verificationSchema = z.object({
  verificationCode: z
    .string()
    .length(5, "Verification code must be exactly 5 digits")
    .regex(/^\d+$/, "Verification code must contain only numbers"),
});

export default function Verify() {
  const theme = useTheme();
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(false);
  const [resendLoading, setResendLoading] = useState(false);
  const [countdown, setCountdown] = useState(0);
  const { setAuthentication } = useAuth();

  // Get email from location state (where user came from signup)
  const numberOrEmail = location.state?.numberOrEmail || "";
  const password = location.state?.password || "";
  const backRoute = location.state?.from || "/login";

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(verificationSchema),
    defaultValues: {
      verificationCode: "",
    },
  });

  const onSubmit = async (data) => {
    setLoading(true);
    try {
      await VerifyManager.verify(
        numberOrEmail,
        password,
        data.verificationCode
      );
      setAuthentication();
      toast.success("Verification Successful!", {
        autoClose: 3000,
        onClose: () => navigate("/user/dashboard"),
        pauseOnHover: false,
      });
    } catch (error) {
      toast.error(error || "Verification failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleResendCode = async () => {
    setResendLoading(true);
    try {
      await VerifyManager.sendVerificationCode(numberOrEmail);
      toast.success("New verification code sent!");
      startCountdown(30); // 30-second countdown
    } catch (error) {
      toast.error(error || "Failed to resend code. Please try again.");
    } finally {
      setResendLoading(false);
    }
  };

  const startCountdown = (seconds) => {
    setCountdown(seconds);
    const timer = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
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
            mb: 2,
            fontWeight: "bold",
            color: "text.primary",
          }}
        >
          Verify Your Email
        </Typography>

        {/* Instructions */}
        <Typography
          variant="body1"
          sx={{
            textAlign: "center",
            mb: 4,
            color: "text.secondary",
          }}
        >
          We've sent a 5-digit verification code to{" "}
          <Box component="span" fontWeight="fontWeightMedium">
            {numberOrEmail || "your email"}
          </Box>
          . Please enter it below.
        </Typography>

        {/* Verification Code Field */}
        <TextField
          {...register("verificationCode")}
          label="Verification Code"
          variant="outlined"
          fullWidth
          margin="normal"
          autoComplete="off"
          error={!!errors.verificationCode}
          helperText={errors.verificationCode?.message}
          inputProps={{
            maxLength: 5,
            inputMode: "numeric",
            pattern: "[0-9]*",
          }}
          sx={{
            mb: 3,
            "& input": {
              textAlign: "center",
              letterSpacing: "0.5em",
              fontSize: "1.5rem",
              padding: "12px 14px",
            },
          }}
        />

        {/* Submit Button */}
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
            "&.Mui-disabled": {
              backgroundColor: "action.disabledBackground",
              color: "action.disabled",
            },
          }}
        >
          {loading ? (
            <CircularProgress size={24} color="inherit" />
          ) : (
            "Verify Email"
          )}
        </Button>

        {/* Resend Code Section */}
        <Stack
          direction="row"
          justifyContent="center"
          alignItems="center"
          spacing={1}
          sx={{ mt: 2 }}
        >
          <Typography variant="body2" sx={{ color: "text.secondary" }}>
            Didn't receive a code?
          </Typography>
          {countdown > 0 ? (
            <Typography variant="body2" sx={{ color: "text.secondary" }}>
              Resend in {countdown}s
            </Typography>
          ) : (
            <Link
              component="button"
              type="button"
              onClick={handleResendCode}
              disabled={resendLoading}
              sx={{
                color: "primary.main",
                "&:hover": {
                  color: "primary.dark",
                },
                "&.Mui-disabled": {
                  color: "text.disabled",
                },
              }}
            >
              {resendLoading ? "Sending..." : "Resend Code"}
            </Link>
          )}
        </Stack>

        {/* Back to Login Link */}
        <Stack
          direction="row"
          justifyContent="center"
          alignItems="center"
          spacing={1}
          sx={{ mt: 4 }}
        >
          <Link
            component={RouterLink}
            to={backRoute}
            sx={{
              color: "primary.main",
              "&:hover": {
                color: "primary.dark",
              },
            }}
          >
            Back
          </Link>
        </Stack>
      </Box>
    </Container>
  );
}
