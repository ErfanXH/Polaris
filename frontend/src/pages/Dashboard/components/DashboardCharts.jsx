import React from "react";
import { Grid, Paper, useTheme, useMediaQuery } from "@mui/material";
import ReactECharts from "echarts-for-react";

function ChartContainer({ option, chartRef }) {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  return (
    <Paper sx={{ p: 2, width: "100%", height: isMobile ? 300 : 400 }}>
      <ReactECharts
        option={option}
        style={{ height: "100%", width: "100%" }}
        theme={theme.palette.mode}
        ref={chartRef}
      />
    </Paper>
  );
}

export function NetworkTechnologyChart({ option, chartRef }) {
  return <ChartContainer option={option} chartRef={chartRef} />;
}

export function ArfcnDistributionChart({ option, chartRef }) {
  return <ChartContainer option={option} chartRef={chartRef} />;
}

export function PingTrendChart({ option, chartRef }) {
  return <ChartContainer option={option} chartRef={chartRef} />;
}

export function PingBoxplotChart({ option, chartRef }) {
  return <ChartContainer option={option} chartRef={chartRef} />;
}

export function PingDistributionChart({ option, chartRef }) {
  return <ChartContainer option={option} chartRef={chartRef} />;
}
