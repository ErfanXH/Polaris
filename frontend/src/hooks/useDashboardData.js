import { useState, useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { useTheme } from "@mui/material";
import DashboardManager from "../managers/DashboardManager";
import { toast } from "react-toastify";
import {
  getNetworkTechOption,
  getArfcnOption,
  getPingLineOption,
  getBoxplotOption,
  getGaussianOption,
} from "../pages/Dashboard/components/DashboardConfig";

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
  });

  const filteredData = useMemo(() => {
    let result = [...measurements];

    if (networkTypeFilter !== "all") {
      result = result.filter((m) => m.network_type === networkTypeFilter);
    }

    if (dateRange.start && dateRange.end) {
      result = result.filter((m) => {
        const timestamp = new Date(m.timestamp);
        return timestamp >= dateRange.start && timestamp <= dateRange.end;
      });
    }

    return result;
  }, [measurements, networkTypeFilter, dateRange]);

  const chartOptions = useMemo(() => {
    return {
      networkTechOption: getNetworkTechOption(filteredData, theme),
      arfcnLifetimeOption: getArfcnOption(filteredData, theme),
      pingLineChartOption: getPingLineOption(filteredData, theme),
      pingBoxplotOption: getBoxplotOption(filteredData, theme),
      pingGaussianOption: getGaussianOption(filteredData, theme),
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
