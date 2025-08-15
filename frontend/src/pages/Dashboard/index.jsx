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
  SignalStrengthOverTimeChart,
  MeasurementCountByHourChart,
  MostFrequentCellsChart,
  TestLineChart,
  TestBoxPlotChart,
  TestDistributionChart,
} from "./components/DashboardCharts";
import { DashboardFilters } from "./components/DashboardFilters";
import { DashboardTable } from "./components/DashboardTable";
import ValueStatisticTable from "./components/ValueStatisticTable";

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
    networkTypes,
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
        networkTypes={networkTypes}
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
              <DashboardTable
                data={filteredData}
                isMobile={isMobile}
                activeTab={tabLabels[activeTab]}
              />
            </>
          )}

          {activeTab == 1 && (
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
              <SignalStrengthOverTimeChart
                option={chartOptions.SignalStrengthOverTimeOption}
                chartRef={(el) => (chartRefs.current[4] = el)}
              />
              <MeasurementCountByHourChart
                option={chartOptions.MeasurementCountByHourOption}
                chartRef={(el) => (chartRefs.current[5] = el)}
              />
              <MostFrequentCellsChart
                option={chartOptions.MostFrequentCellsOption}
                chartRef={(el) => (chartRefs.current[6] = el)}
              />
              <DashboardTable
                data={filteredData}
                isMobile={isMobile}
                activeTab={tabLabels[activeTab]}
              />
            </>
          )}
          {activeTab == 2 && (
            <>
              <TestLineChart
                option={chartOptions.SMSLineOption}
                chartRef={(el) => (chartRefs.current[0] = el)}
              />
              <TestBoxPlotChart
                option={chartOptions.SMSBoxplotOption}
                chartRef={(el) => (chartRefs.current[1] = el)}
              />
              <TestDistributionChart
                option={chartOptions.SMSDistributionOption}
                chartRef={(el) => (chartRefs.current[2] = el)}
              />
              <ValueStatisticTable
                values={filteredData
                  .map((item) => Number(item.sms_delivery_time))
                  .filter((val) => !isNaN(val) && val > 0)}
              />
              <DashboardTable
                data={filteredData}
                isMobile={isMobile}
                activeTab={tabLabels[activeTab]}
              />
            </>
          )}
          {activeTab == 3 && (
            <>
              <TestLineChart
                option={chartOptions.WebLineOption}
                chartRef={(el) => (chartRefs.current[0] = el)}
              />
              <TestBoxPlotChart
                option={chartOptions.WebBoxplotOption}
                chartRef={(el) => (chartRefs.current[1] = el)}
              />
              <TestDistributionChart
                option={chartOptions.WebDistributionOption}
                chartRef={(el) => (chartRefs.current[2] = el)}
              />
              <ValueStatisticTable
                values={filteredData
                  .map((item) => Number(item.web_response))
                  .filter((val) => !isNaN(val) && val > 0)}
              />
              <DashboardTable
                data={filteredData}
                isMobile={isMobile}
                activeTab={tabLabels[activeTab]}
              />
            </>
          )}
          {activeTab == 4 && (
            <>
              <TestLineChart
                option={chartOptions.UploadLineOption}
                chartRef={(el) => (chartRefs.current[0] = el)}
              />
              <TestBoxPlotChart
                option={chartOptions.UploadBoxplotOption}
                chartRef={(el) => (chartRefs.current[1] = el)}
              />
              <TestDistributionChart
                option={chartOptions.UploadDistributionOption}
                chartRef={(el) => (chartRefs.current[2] = el)}
              />
              <ValueStatisticTable
                values={filteredData
                  .map((item) => Number(item.http_upload))
                  .filter((val) => !isNaN(val) && val > 0)}
              />
              <DashboardTable
                data={filteredData}
                isMobile={isMobile}
                activeTab={tabLabels[activeTab]}
              />
            </>
          )}
          {activeTab == 5 && (
            <>
              <TestLineChart
                option={chartOptions.DownloadLineOption}
                chartRef={(el) => (chartRefs.current[0] = el)}
              />
              <TestBoxPlotChart
                option={chartOptions.DownloadBoxplotOption}
                chartRef={(el) => (chartRefs.current[1] = el)}
              />
              <TestDistributionChart
                option={chartOptions.DownloadDistributionOption}
                chartRef={(el) => (chartRefs.current[2] = el)}
              />
              <ValueStatisticTable
                values={filteredData
                  .map((item) => Number(item.http_download))
                  .filter((val) => !isNaN(val) && val > 0)}
              />
              <DashboardTable
                data={filteredData}
                isMobile={isMobile}
                activeTab={tabLabels[activeTab]}
              />
            </>
          )}
          {activeTab == 6 && (
            <>
              <TestLineChart
                option={chartOptions.PingLineOption}
                chartRef={(el) => (chartRefs.current[0] = el)}
              />
              <TestBoxPlotChart
                option={chartOptions.PingBoxplotOption}
                chartRef={(el) => (chartRefs.current[1] = el)}
              />
              <TestDistributionChart
                option={chartOptions.PingDistributionOption}
                chartRef={(el) => (chartRefs.current[2] = el)}
              />
              <ValueStatisticTable
                values={filteredData
                  .map((item) => Number(item.ping_time))
                  .filter((val) => !isNaN(val) && val > 0)}
              />
              <DashboardTable
                data={filteredData}
                isMobile={isMobile}
                activeTab={tabLabels[activeTab]}
              />
            </>
          )}
          {activeTab == 7 && (
            <>
              <TestLineChart
                option={chartOptions.DNSLineOption}
                chartRef={(el) => (chartRefs.current[0] = el)}
              />
              <TestBoxPlotChart
                option={chartOptions.DNSBoxplotOption}
                chartRef={(el) => (chartRefs.current[1] = el)}
              />
              <TestDistributionChart
                option={chartOptions.DNSDistributionOption}
                chartRef={(el) => (chartRefs.current[2] = el)}
              />
              <ValueStatisticTable
                values={filteredData
                  .map((item) => Number(item.dns_response))
                  .filter((val) => !isNaN(val) && val > 0)}
              />
              <DashboardTable
                data={filteredData}
                isMobile={isMobile}
                activeTab={tabLabels[activeTab]}
              />
            </>
          )}
        </>
      )}
    </Box>
  );
}
