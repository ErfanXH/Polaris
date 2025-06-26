import {
  Box,
  Typography,
  CircularProgress,
  useTheme,
  Tabs,
  Tab,
} from "@mui/material";
import { useMediaQuery } from "@mui/material";
import { useRef, useState } from "react";
import { useDashboardData } from "../../hooks/useDashboardData";
import {
  NetworkTechnologyChart,
  ArfcnDistributionChart,
  FrequencyBandChart,
  RsrpRsrqScatterChart,
} from "./components/DashboardCharts";
import { DashboardFilters } from "./components/DashboardFilters";
import { DashboardTable } from "./components/DashboardTable";

const tabLabels = [
  "Overview",
  "Measurements",
  "SMS",
  "Web",
  "Upload",
  "Download",
  "Ping",
  "DNS",
];

export default function Dashboard() {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const chartRefs = useRef([]);
  const [activeTab, setActiveTab] = useState(0);

  const {
    loading,
    filteredData,
    networkTypeFilter,
    setNetworkTypeFilter,
    dateRange,
    setDateRange,
    chartOptions,
  } = useDashboardData();

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

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

      <Tabs
        value={activeTab}
        onChange={handleTabChange}
        variant="scrollable"
        scrollButtons="auto"
        sx={{ my: 2 }}
      >
        {tabLabels.map((label, index) => (
          <Tab key={index} label={label} />
        ))}
      </Tabs>

      {loading ? (
        <Box sx={{ display: "flex", justifyContent: "center", mt: 4 }}>
          <CircularProgress />
        </Box>
      ) : (
        <>
          {activeTab === 0 && (
            <>
              <NetworkTechnologyChart
                option={chartOptions.networkTechOption}
                chartRef={(el) => (chartRefs.current[0] = el)}
              />
              <ArfcnDistributionChart
                option={chartOptions.arfcnOption}
                chartRef={(el) => (chartRefs.current[1] = el)}
              />
              <FrequencyBandChart
                option={chartOptions.frequencyBandOption}
                chartRef={(el) => (chartRefs.current[2] = el)}
              />
              <RsrpRsrqScatterChart
                option={chartOptions.rsrpRsrqScatterOption}
                chartRef={(el) => (chartRefs.current[3] = el)}
              />
              <DashboardTable data={filteredData} isMobile={isMobile} />
            </>
          )}

          {/* Placeholder for future tabs */}
          {activeTab !== 0 && (
            <Typography sx={{ mt: 4 }}>
              Charts for "{tabLabels[activeTab]}" will be implemented soon.
            </Typography>
          )}
        </>
      )}
    </Box>
  );
}
