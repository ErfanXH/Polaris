import { createTheme } from "@mui/material/styles";

// Helper function to get browser color scheme
const getDefaultMode = () => {
  if (typeof window !== "undefined") {
    return window.matchMedia?.("(prefers-color-scheme: dark)").matches
      ? "dark"
      : "light";
  }
  return "light";
};

// Create theme based on mode
export const createAppTheme = (mode = getDefaultMode()) => {
  // Set the data-theme attribute on document element
  if (typeof document !== "undefined") {
    document.documentElement.setAttribute("data-theme", mode);
  }

  //   return createTheme({
  //     palette: {
  //       mode,
  //       primary: {
  //         main: "var(--accent-gold)",
  //         dark: "var(--accent-gold-hover)",
  //       },
  //       background: {
  //         default: "var(--primary-bg)",
  //         paper: "var(--secondary-bg)",
  //       },
  //       text: {
  //         primary: "var(--text-primary)",
  //         secondary: "var(--text-secondary)",
  //       },
  //     },
  //   });

  const themeVars = {
    light: {
      primaryBg: "#ffffff",
      secondaryBg: "#f8f8f8",
      textPrimary: "#333333",
      textSecondary: "#666666",
      accentGold: "#d4af37",
      accentGoldHover: "#b7950b",
      border: "#e0e0e0",
      cardBg: "#ffffff",
      statsBg: "#f8f8f8",
      error: "#d32f2f",
      tableHeaderBg: "#1e1e1e",
      tableRowHover: "#f0f0f0",
      navbarBg: "#1e1e1e",
      editorBg: "#ffffff",
      editorOutsideBg: "#eaecf1",
      toolbarBg: "#f0f0f0",
      buttonBg: "#d4af37",
      buttonHover: "#b7950b",
    },
    dark: {
      primaryBg: "#121212",
      secondaryBg: "#1e1e1e",
      textPrimary: "#e0e0e0",
      textSecondary: "#a0a0a0",
      accentGold: "#d4af37",
      accentGoldHover: "#ffd700",
      border: "#333333",
      cardBg: "#1e1e1e",
      statsBg: "#121212",
      error: "#f44336",
      tableHeaderBg: "#2a2a2a",
      tableRowHover: "#2a2a2a",
      navbarBg: "#2a2a2a",
      editorBg: "#262626",
      editorOutsideBg: "#121416",
      toolbarBg: "#555454",
      buttonBg: "#d4af37",
      buttonHover: "#ffd700",
    },
  };

  const vars = themeVars[mode];

  return createTheme({
    palette: {
      mode,
      primary: {
        main: vars.accentGold,
        dark: vars.accentGoldHover,
      },
      secondary: {
        main: vars.textSecondary,
      },
      error: {
        main: vars.error,
      },
      background: {
        default: vars.primaryBg,
        paper: vars.secondaryBg,
      },
      text: {
        primary: vars.textPrimary,
        secondary: vars.textSecondary,
      },
      // Custom variables for specific components
      custom: {
        cardBg: vars.cardBg,
        statsBg: vars.statsBg,
        tableHeaderBg: vars.tableHeaderBg,
        tableRowHover: vars.tableRowHover,
        navbarBg: vars.navbarBg,
        editorBg: vars.editorBg,
        editorOutsideBg: vars.editorOutsideBg,
        toolbarBg: vars.toolbarBg,
        border: vars.border,
      },
    },
    components: {
      MuiButton: {
        styleOverrides: {
          root: {
            backgroundColor: vars.buttonBg,
            "&:hover": {
              backgroundColor: vars.buttonHover,
            },
          },
        },
      },
      MuiPaper: {
        styleOverrides: {
          root: {
            backgroundColor: vars.cardBg,
          },
        },
      },
      MuiAppBar: {
        styleOverrides: {
          root: {
            backgroundColor: vars.navbarBg,
          },
        },
      },
      MuiTableHead: {
        styleOverrides: {
          root: {
            backgroundColor: vars.tableHeaderBg,
          },
        },
      },
      MuiTableRow: {
        styleOverrides: {
          root: {
            "&:hover": {
              backgroundColor: vars.tableRowHover,
            },
          },
        },
      },
    },
  });
};

// Default theme
export default createAppTheme();
