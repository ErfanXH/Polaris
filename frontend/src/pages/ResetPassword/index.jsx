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
  IconButton,
  InputAdornment,
  CircularProgress,
} from "@mui/material";
import { Link as RouterLink, useNavigate } from "react-router-dom";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import Logo from "/logo.svg";
import { toast } from "react-toastify";
import ResetPasswordManager from "../../managers/ResetPasswordManager";

const numberOrEmailSchema = z.object({
  number_or_email: z
    .string()
    .min(1, "Email or phone number is required")
    .refine(
      (value) => {
        const isEmail = z.string().email().safeParse(value).success;
        const isPhone = /^\+?[0-9]{11}$/.test(value);
        return isEmail || isPhone;
      },
      {
        message: "Must be a valid email or phone number",
      }
    ),
});

const codeSchema = z.object({
  code: z
    .string()
    .regex(/^\+?[0-9]{5}$/, "Must be a valid Verification code (5 digits)"),
});

const passwordSchema = z
  .object({
    newPassword: z.string().min(8, "Password must be at least 8 characters"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  });

export default function ResetPassword() {
  const theme = useTheme();
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [resendLoading, setResendLoading] = useState(false);
  const [countdown, setCountdown] = useState(0);

  const {
    register: registerNumberOrEmail,
    handleSubmit: handleSubmitNumberOrEmail,
    formState: { errors: numberOrEmailErrors },
    watch: watchNumberOrEmail,
  } = useForm({
    resolver: zodResolver(numberOrEmailSchema),
  });

  const {
    register: registerCode,
    handleSubmit: handleSubmitCode,
    formState: { errors: codeErrors },
    watch: watchCode,
  } = useForm({
    resolver: zodResolver(codeSchema),
  });

  const {
    register: registerPassword,
    handleSubmit: handleSubmitPassword,
    formState: { errors: passwordErrors },
  } = useForm({
    resolver: zodResolver(passwordSchema),
  });

  const onSubmitNumberOrEmail = async (data) => {
    setLoading(true);
    try {
      await ResetPasswordManager.sendResetCode(data.number_or_email);
      setStep(2);
      toast.success("Verification code sent to your email");
    } catch (error) {
      toast.error(error || "Failed to send verification code");
    } finally {
      setLoading(false);
    }
  };

  const onSubmitCode = async (data) => {
    setLoading(true);
    try {
      await ResetPasswordManager.verifyResetCode(
        watchNumberOrEmail("number_or_email"),
        data.code
      );
      setStep(3);
      toast.success("Code verified successfully");
    } catch (error) {
      toast.error(error || "Invalid verification code");
    } finally {
      setLoading(false);
    }
  };

  const onSubmitPassword = async (data) => {
    setLoading(true);
    try {
      await ResetPasswordManager.resetPassword(
        watchNumberOrEmail("number_or_email"),
        watchCode("code"),
        data.newPassword
      );
      toast.success("Password reset successfully", {
        autoClose: 3000,
        onClose: () => navigate("/login"),
      });
    } catch (error) {
      toast.error(error || "Failed to reset password");
    } finally {
      setLoading(false);
    }
  };

  const handleResendCode = async () => {
    setResendLoading(true);
    try {
      await ResetPasswordManager.sendResetCode(
        watchNumberOrEmail("number_or_email")
      );
      toast.success("New verification code sent!");
      startCountdown(30);
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

  const handleClickShowPassword = () => {
    setShowPassword(!showPassword);
  };

  const handleClickShowConfirmPassword = () => {
    setShowConfirmPassword(!showConfirmPassword);
  };

  const handleBack = () => {
    if (step > 1) {
      setStep(step - 1);
    } else {
      navigate("/login");
    }
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
        onSubmit={
          step === 1
            ? handleSubmitNumberOrEmail(onSubmitNumberOrEmail)
            : step === 2
            ? handleSubmitCode(onSubmitCode)
            : handleSubmitPassword(onSubmitPassword)
        }
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
          {step === 1
            ? "Reset Your Password"
            : step === 2
            ? "Enter Verification Code"
            : "Create New Password"}
        </Typography>

        <Box sx={{ display: "flex", justifyContent: "center", mb: 4 }}>
          {[1, 2, 3].map((stepNumber) => (
            <Box
              key={stepNumber}
              sx={{
                width: 32,
                height: 32,
                borderRadius: "50%",
                backgroundColor:
                  step === stepNumber
                    ? "primary.main"
                    : step > stepNumber
                    ? "success.main"
                    : "divider",
                color: step >= stepNumber ? "white" : "text.secondary",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                mx: 1,
                fontWeight: "bold",
              }}
            >
              {stepNumber}
            </Box>
          ))}
        </Box>

        {step === 1 && (
          <>
            <Typography variant="body1" sx={{ mb: 3, textAlign: "center" }}>
              Enter your email address to receive a verification code
            </Typography>

            <TextField
              {...registerNumberOrEmail("number_or_email")}
              label="Email Address or Phone Number"
              variant="outlined"
              name="number_or_email"
              fullWidth
              margin="normal"
              autoComplete="email tel-national"
              error={!!numberOrEmailErrors.number_or_email}
              helperText={numberOrEmailErrors.number_or_email?.message}
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
          </>
        )}

        {step === 2 && (
          <>
            <Typography variant="body1" sx={{ mb: 3, textAlign: "center" }}>
              We sent a 5-digit code to your email
            </Typography>

            <TextField
              {...registerCode("code")}
              label="Verification Code"
              variant="outlined"
              fullWidth
              margin="normal"
              error={!!codeErrors.code}
              helperText={codeErrors.code?.message}
              sx={{
                mb: 3,
                "& input": {
                  textAlign: "center",
                  letterSpacing: "0.5em",
                  fontSize: "1.5rem",
                  padding: "12px 14px",
                },
              }}
              inputProps={{
                maxLength: 5,
                inputMode: "numeric",
                pattern: "[0-9]*",
              }}
            />

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
          </>
        )}

        {step === 3 && (
          <>
            <Typography variant="body1" sx={{ mb: 3, textAlign: "center" }}>
              Create a new password for your account
            </Typography>

            <TextField
              {...registerPassword("newPassword")}
              label="New Password"
              type={showPassword ? "text" : "password"}
              variant="outlined"
              fullWidth
              margin="normal"
              error={!!passwordErrors.newPassword}
              helperText={passwordErrors.newPassword?.message}
              sx={{ mb: 2 }}
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="toggle confirm password visibility"
                      onClick={handleClickShowPassword}
                      edge="end"
                    >
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />

            <TextField
              {...registerPassword("confirmPassword")}
              label="Confirm Password"
              type={showConfirmPassword ? "text" : "password"}
              variant="outlined"
              fullWidth
              margin="normal"
              error={!!passwordErrors.confirmPassword}
              helperText={passwordErrors.confirmPassword?.message}
              sx={{ mb: 3 }}
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="toggle password visibility"
                      onClick={handleClickShowConfirmPassword}
                      edge="end"
                    >
                      {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />
          </>
        )}

        <Box sx={{ display: "flex", justifyContent: "space-between", mt: 3 }}>
          <Button
            variant="outlined"
            onClick={handleBack}
            sx={{
              color: "text.secondary",
              borderColor: "divider",
              "&:hover": {
                borderColor: "text.secondary",
              },
            }}
          >
            Back
          </Button>

          <Button
            type="submit"
            variant="contained"
            disabled={loading}
            sx={{
              backgroundColor: "primary.main",
              color: theme.palette.getContrastText(theme.palette.primary.main),
              "&:hover": {
                backgroundColor: "primary.dark",
              },
              minWidth: 120,
            }}
          >
            {loading ? (
              <CircularProgress size={24} color="inherit" />
            ) : step === 3 ? (
              "Reset Password"
            ) : (
              "Continue"
            )}
          </Button>
        </Box>
      </Box>
    </Container>
  );
}
