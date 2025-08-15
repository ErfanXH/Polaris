import React from "react";
import { Box, TextField, MenuItem, useTheme } from "@mui/material";
import { LocalizeDateTime } from "../../../utils/DatetimeUtility";

export function DashboardFilters({
  isMobile,
  networkTypeFilter,
  setNetworkTypeFilter,
  dateRange,
  setDateRange,
  networkTypes,
}) {
  const theme = useTheme();

  const dateFieldStyles = {
    flex: isMobile ? "100%" : 1,
    '& input[type="date"]::-webkit-calendar-picker-indicator': {
      filter: theme.palette.mode === "dark" ? "invert(1)" : "invert(0)",
      opacity: 0.8,
      "&:hover": {
        opacity: 1,
      },
    },
    '& input[type="date"]': {
      "&::-webkit-datetime-edit-fields-wrapper": {
        color: theme.palette.text.primary,
      },
      "&::-webkit-datetime-edit-text": {
        color: theme.palette.text.secondary,
      },
    },
  };

  return (
    <Box sx={{ mb: 4, display: "flex", flexWrap: "wrap", gap: 2 }}>
      <TextField
        select
        label="Network Type"
        value={networkTypeFilter}
        onChange={(e) => setNetworkTypeFilter(e.target.value)}
        sx={{ minWidth: isMobile ? "100%" : 200 }}
        size={isMobile ? "small" : "medium"}
      >
        <MenuItem value="all">All Network Types</MenuItem>
        {networkTypes.map((item) =>{return(<MenuItem value={item}>{item}</MenuItem>)})}
        {/* <MenuItem value="GSM">GSM</MenuItem>
        <MenuItem value="GPRS">GPRS</MenuItem>
        <MenuItem value="EDGE">EDGE</MenuItem>
        <MenuItem value="UMTS">UMTS</MenuItem>
        <MenuItem value="HSPA">HSPA</MenuItem>
        <MenuItem value="HSPA+">HSPA+</MenuItem>
        <MenuItem value="LTE">LTE</MenuItem>
        <MenuItem value="5G">5G</MenuItem>
        <MenuItem value="LTE-Adv">LTE-Adv</MenuItem> */}
      </TextField>

      <TextField
        label="Start Date"
        type="date"
        InputLabelProps={{ shrink: true }}
        onChange={(e) =>
          setDateRange((prev) => ({
            ...prev,
            start: LocalizeDateTime(e.target.value),
          }))
        }
        sx={dateFieldStyles}
        size={isMobile ? "small" : "medium"}
      />

      <TextField
        label="End Date"
        type="date"
        InputLabelProps={{ shrink: true }}
        onChange={(e) =>
          setDateRange((prev) => ({
            ...prev,
            end: LocalizeDateTime(e.target.value),
          }))
        }
        sx={dateFieldStyles}
        size={isMobile ? "small" : "medium"}
      />
    </Box>
  );
}
