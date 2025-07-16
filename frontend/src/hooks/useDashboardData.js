import { useState, useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { useTheme } from "@mui/material";
import DashboardManager from "../managers/DashboardManager";
import { toast } from "react-toastify";
import {
  getNetworkTechPieOption,
  getArfcnPieOption,
  getFrequencyBandBarOption,
  getRsrpRsrqScatterOption,
  // getTestPerformanceRadarOption,
  getSignalStrengthOverTimeOption,
  getMeasurementCountByHourOption,
  getMostFrequentCellsOption,
  getLineChartOption,
  getBoxPlotOption,
  getDistributionOption,
} from "../pages/Dashboard/components/DashboardConfig";
import { formatDateTime, LocalizeDateTime } from "../utils/DatetimeUtility";

export const useDashboardData = () => {
  const theme = useTheme();
  const [networkTypeFilter, setNetworkTypeFilter] = useState("all");
  const [dateRange, setDateRange] = useState({ start: null, end: null });

  const { data: measurements = [], isLoading: loading } = useQuery({
    queryKey: ["dashboard-measurements"],
    queryFn: async () => {
      try {
        return await DashboardManager.getAll();
      } catch (err) {
        toast.error("Failed to fetch measurements");
        throw err;
      }
    },
    refetchOnWindowFocus: false,
    refetchInterval: 60000,
  });

  const filteredData = useMemo(() => {
    let result = [...measurements];
    result = result.filter((m) => m.network_type !== "UNKNOWN");
    // console.log(result);
    // console.log(formatDateTime(result[0]?.timestamp));

    if (networkTypeFilter !== "all") {
      result = result.filter((m) => m.network_type === networkTypeFilter);
    }

    return result;
  }, [measurements, networkTypeFilter, dateRange]);

  const chartOptions = useMemo(() => {
    return {
      networkTechOption: getNetworkTechPieOption(filteredData, theme),
      arfcnOption: getArfcnPieOption(filteredData, theme),
      frequencyBandOption: getFrequencyBandBarOption(filteredData, theme),
      rsrpRsrqScatterOption: getRsrpRsrqScatterOption(filteredData, theme),
      SignalStrengthOverTimeOption: getSignalStrengthOverTimeOption(
        filteredData,
        theme
      ),
      MeasurementCountByHourOption: getMeasurementCountByHourOption(
        filteredData,
        theme
      ),
      MostFrequentCellsOption: getMostFrequentCellsOption(filteredData, theme),
      SMSLineOption: getLineChartOption(
        filteredData,
        "sms_delivery_time",
        theme,
        "SMS",
        "SMS delivery time"
      ),
      SMSBoxplotOption: getBoxPlotOption(
        filteredData,
        "sms_delivery_time",
        theme,
        "SMS",
        "SMS delivery time"
      ),
      SMSDistributionOption: getDistributionOption(
        filteredData,
        "sms_delivery_time",
        theme,
        "SMS"
      ),
      WebLineOption: getLineChartOption(
        filteredData,
        "web_response",
        theme,
        "Web",
        "Web Response"
      ),
      WebBoxplotOption: getBoxPlotOption(
        filteredData,
        "web_response",
        theme,
        "Web",
        "Web Response"
      ),
      WebDistributionOption: getDistributionOption(
        filteredData,
        "web_response",
        theme,
        "Web"
      ),
      UploadLineOption: getLineChartOption(
        filteredData,
        "http_upload",
        theme,
        "Upload",
        "Upload throughput"
      ),
      UploadBoxplotOption: getBoxPlotOption(
        filteredData,
        "http_upload",
        theme,
        "Upload",
        "Upload throughput"
      ),
      UploadDistributionOption: getDistributionOption(
        filteredData,
        "http_upload",
        theme,
        "Upload"
      ),
      DownloadLineOption: getLineChartOption(
        filteredData,
        "http_download",
        theme,
        "Download",
        "Download throughput"
      ),
      DownloadBoxplotOption: getBoxPlotOption(
        filteredData,
        "http_download",
        theme,
        "Download",
        "Download throughput"
      ),
      DownloadDistributionOption: getDistributionOption(
        filteredData,
        "http_download",
        theme,
        "Download"
      ),
      PingLineOption: getLineChartOption(
        filteredData,
        "ping_time",
        theme,
        "Ping",
        "Ping Response"
      ),
      PingBoxplotOption: getBoxPlotOption(
        filteredData,
        "ping_time",
        theme,
        "Ping",
        "Ping Response"
      ),
      PingDistributionOption: getDistributionOption(
        filteredData,
        "ping_time",
        theme,
        "Ping"
      ),
      DNSLineOption: getLineChartOption(
        filteredData,
        "dns_response",
        theme,
        "DNS",
        "DNS Response"
      ),
      DNSBoxplotOption: getBoxPlotOption(
        filteredData,
        "dns_response",
        theme,
        "DNS",
        "DNS Response"
      ),
      DNSDistributionOption: getDistributionOption(
        filteredData,
        "dns_response",
        theme,
        "DNS"
      ),
    };
  }, [filteredData, theme]);

  return {
    loading,
    filteredData,
    networkTypeFilter,
    setNetworkTypeFilter,
    dateRange,
    setDateRange,
    chartOptions,
  };
};
