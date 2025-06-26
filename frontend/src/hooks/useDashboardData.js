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
} from "../pages/Dashboard/components/DashboardConfig";
import { formatDateTime } from "../utils/FormatDatetime";

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
      networkTechOption: getNetworkTechPieOption(filteredData, theme),
      arfcnOption: getArfcnPieOption(filteredData, theme),
      frequencyBandOption: getFrequencyBandBarOption(filteredData, theme),
      rsrpRsrqScatterOption: getRsrpRsrqScatterOption(filteredData, theme),
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
