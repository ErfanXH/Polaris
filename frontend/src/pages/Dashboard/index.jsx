import { Box, Typography, CircularProgress, useTheme } from "@mui/material";
import { useMediaQuery } from "@mui/material";
import { useRef } from "react";
import { useDashboardData } from "../../hooks/useDashboardData";
import {
  NetworkTechnologyChart,
  ArfcnDistributionChart,
  PingTrendChart,
  PingBoxplotChart,
  PingDistributionChart,
} from "./components/DashboardCharts";
import { DashboardFilters } from "./components/DashboardFilters";
import { DashboardTable } from "./components/DashboardTable";

export default function Dashboard() {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const chartRefs = useRef([]);

  const {
    loading,
    filteredData,
    networkTypeFilter,
    setNetworkTypeFilter,
    dateRange,
    setDateRange,
    chartOptions,
  } = useDashboardData();

  return (
    <Box sx={{ p: isMobile ? 1 : 3 }}>
      <Typography variant="h4" gutterBottom>
        Network Performance Dashboard
      </Typography>

      <DashboardFilters
        isMobile={isMobile}
        networkTypeFilter={networkTypeFilter}
        setNetworkTypeFilter={setNetworkTypeFilter}
        dateRange={dateRange}
        setDateRange={setDateRange}
      />

      {loading ? (
        <Box sx={{ display: "flex", justifyContent: "center", mt: 4 }}>
          <CircularProgress />
        </Box>
      ) : (
        <>
          <NetworkTechnologyChart
            option={chartOptions.networkTechOption}
            chartRef={(el) => (chartRefs.current[0] = el)}
          />
          <ArfcnDistributionChart
            option={chartOptions.arfcnLifetimeOption}
            chartRef={(el) => (chartRefs.current[1] = el)}
          />
          <PingTrendChart
            option={chartOptions.pingLineChartOption}
            chartRef={(el) => (chartRefs.current[2] = el)}
          />
          <PingBoxplotChart
            option={chartOptions.pingBoxplotOption}
            chartRef={(el) => (chartRefs.current[3] = el)}
          />
          <PingDistributionChart
            option={chartOptions.pingGaussianOption}
            chartRef={(el) => (chartRefs.current[4] = el)}
          />

          <DashboardTable data={filteredData} isMobile={isMobile} />
        </>
      )}
    </Box>
  );
}
