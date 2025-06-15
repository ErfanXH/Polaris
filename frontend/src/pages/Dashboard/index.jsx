import { useState, useEffect, useRef } from "react";
import {
  Box,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  MenuItem,
  Grid,
  useTheme,
  CircularProgress,
} from "@mui/material";
import ReactECharts from "echarts-for-react";
import { useMediaQuery } from "@mui/material";
import DashboardManager from "../../managers/DashboardManager";

export default function Dashboard() {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const isTablet = useMediaQuery(theme.breakpoints.between("sm", "md"));
  const chartRefs = useRef([]);

  const [measurements, setMeasurements] = useState([]);
  const [filteredData, setFilteredData] = useState([]);
  const [networkTypeFilter, setNetworkTypeFilter] = useState("all");
  const [dateRange, setDateRange] = useState({ start: null, end: null });
  const [loading, setLoading] = useState(true);

  // Handle chart resize
  useEffect(() => {
    const handleResize = () => {
      chartRefs.current.forEach((ref) => {
        if (ref && ref.getEchartsInstance) {
          ref.getEchartsInstance().resize();
        }
      });
    };

    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  // Apply theme colors to charts
  const chartTheme = {
    backgroundColor: theme.palette.background.paper,
    textStyle: {
      color: theme.palette.text.primary,
    },
    title: {
      textStyle: {
        color: theme.palette.text.primary,
      },
    },
    legend: {
      textStyle: {
        color: theme.palette.text.primary,
      },
    },
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const data = await DashboardManager.getAll();
        setMeasurements(data);
        setFilteredData(data);
      } catch (error) {
        console.error("Error fetching measurements:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  useEffect(() => {
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

    setFilteredData(result);
  }, [networkTypeFilter, dateRange, measurements]);

  // Common chart options
  const commonChartOptions = {
    ...chartTheme,
    animation: true,
    tooltip: {
      trigger: "item",
      backgroundColor: theme.palette.background.paper,
      borderColor: theme.palette.divider,
      textStyle: {
        color: theme.palette.text.primary,
      },
    },
  };

  // Ping Time Line Chart
  const pingLineChartOption = {
    ...commonChartOptions,
    title: {
      text: "Ping Time Trend",
    },
    legend: {
      show: false,
    },
    tooltip: {
      trigger: "axis",
    },
    xAxis: {
      type: "category",
      data: filteredData
        .sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp))
        .map((m) => m.timestamp),
      axisLabel: {
        rotate: 45,
        interval: Math.ceil(filteredData.length / 5), // Show 5 labels
      },
    },
    yAxis: {
      type: "value",
      name: "Ping (ms)",
    },
    series: [
      {
        name: "Ping Time",
        type: "line",
        smooth: true,
        data: filteredData
          .sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp))
          .map((m) => m.ping_time),
        itemStyle: {
          color: theme.palette.primary.main,
        },
        areaStyle: {
          color: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: `${theme.palette.primary.main}80`,
              },
              {
                offset: 1,
                color: `${theme.palette.primary.main}00`,
              },
            ],
          },
        },
      },
    ],
  };

  // ARFCN Lifetime Pie Chart
  const arfcnLifetimeOption = {
    ...commonChartOptions,
    title: {
      text: "ARFCN Distribution (4G)",
      left: "center",
    },
    legend: {
      orient: isMobile ? "horizontal" : "vertical",
      left: isMobile ? "center" : "left",
      top: isMobile ? "bottom" : "middle",
    },
    series: [
      {
        name: "ARFCN",
        type: "pie",
        radius: isMobile ? "50%" : ["40%", "70%"],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 4,
          borderColor: theme.palette.background.paper,
          borderWidth: 2,
        },
        label: {
          show: !isMobile,
          formatter: "{b}: {c} ({d}%)",
        },
        labelLine: {
          show: !isMobile,
        },
        data: filteredData
          .filter((m) => m.network_type === "LTE")
          .reduce((acc, curr) => {
            const arfcn = curr.arfcn || "Unknown";
            const existing = acc.find((item) => item.name === arfcn);
            if (existing) {
              existing.value++;
            } else {
              acc.push({
                name: arfcn,
                value: 1,
                itemStyle: {
                  color: getColorForArfcn(arfcn),
                },
              });
            }
            return acc;
          }, []),
      },
    ],
  };

  // Network Technology Pie Chart
  const networkTechOption = {
    ...commonChartOptions,
    title: {
      text: "Network Technology",
    },
    legend: {
      orient: isMobile ? "horizontal" : "vertical",
      left: isMobile ? "center" : "left",
      top: isMobile ? "bottom" : "middle",
    },
    series: [
      {
        name: "Technology",
        type: "pie",
        radius: isMobile ? "40%" : ["40%", "70%"],
        roseType: "radius",
        label: {
          show: !isMobile,
          formatter: "{b}: {c} ({d}%)",
        },
        labelLine: {
          show: !isMobile,
        },
        data: Object.entries(
          filteredData.reduce((acc, curr) => {
            acc[curr.network_type] = (acc[curr.network_type] || 0) + 1;
            return acc;
          }, {})
        ).map(([name, value]) => ({
          name,
          value,
          itemStyle: {
            color: getColorForNetworkType(name),
          },
        })),
      },
    ],
  };

  // Ping/Delay Boxplot
  const pingBoxplotOption = {
    ...commonChartOptions,
    title: {
      text: "Latency Statistics",
      left: "center",
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: isMobile ? "25%" : "15%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: ["Ping", "DNS", "Web", "SMS"],
      axisLabel: {
        interval: 0,
        rotate: isMobile ? 45 : 0,
      },
    },
    yAxis: {
      type: "value",
      name: "Time (ms)",
    },
    series: [
      {
        name: "boxplot",
        type: "boxplot",
        itemStyle: {
          color: theme.palette.primary.main,
          borderColor: theme.palette.primary.dark,
        },
        data: [
          calculateBoxplotData(filteredData.map((m) => m.ping_time)),
          calculateBoxplotData(filteredData.map((m) => m.dns_response)),
          calculateBoxplotData(filteredData.map((m) => m.web_response)),
          calculateBoxplotData(filteredData.map((m) => m.sms_delivery_time)),
        ],
      },
    ],
  };

  // Ping Time Distribution
  const pingGaussianOption = {
    ...commonChartOptions,
    title: {
      text: "Ping Time Distribution",
      left: "center",
    },
    grid: {
      top: "20%",
      left: "3%",
      right: "4%",
      bottom: "15%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: createGaussianData(filteredData.map((m) => m.ping_time)).map(
        (d) => d.name
      ),
      axisLabel: {
        rotate: isMobile ? 45 : 0,
      },
    },
    yAxis: {
      type: "value",
      name: "Count",
    },
    series: [
      {
        name: "Ping Time",
        type: "bar",
        barWidth: "60%",
        itemStyle: {
          color: theme.palette.secondary.main,
        },
        data: createGaussianData(filteredData.map((m) => m.ping_time)).map(
          (d) => d.count
        ),
      },
    ],
  };

  // Helper functions
  function calculateBoxplotData(data) {
    if (!data || !data.length) return [0, 0, 0, 0, 0];

    const filtered = data.filter((val) => val !== null && val !== undefined);
    if (!filtered.length) return [0, 0, 0, 0, 0];

    const sorted = [...filtered].sort((a, b) => a - b);
    const q1 = sorted[Math.floor(sorted.length * 0.25)];
    const median = sorted[Math.floor(sorted.length * 0.5)];
    const q3 = sorted[Math.floor(sorted.length * 0.75)];
    const iqr = q3 - q1;
    const lower = Math.max(sorted[0], q1 - 1.5 * iqr);
    const upper = Math.min(sorted[sorted.length - 1], q3 + 1.5 * iqr);

    return [lower, q1, median, q3, upper];
  }

  function createGaussianData(values) {
    if (!values || !values.length) return [];

    const filtered = values.filter((val) => val !== null && val !== undefined);
    if (!filtered.length) return [];

    const min = Math.min(...filtered);
    const max = Math.max(...filtered);
    const step = (max - min) / 10;

    const buckets = {};
    for (let i = min; i <= max; i += step) {
      buckets[i.toFixed(2)] = 0;
    }

    filtered.forEach((value) => {
      const bucket = Math.floor(value / step) * step;
      buckets[bucket.toFixed(2)] = (buckets[bucket.toFixed(2)] || 0) + 1;
    });

    return Object.entries(buckets).map(([value, count]) => ({
      value: parseFloat(value),
      name: `${value} ms`,
      count,
    }));
  }

  function getColorForNetworkType(networkType) {
    const colors = {
      GSM: theme.palette.error.main,
      GPRS: theme.palette.error.light,
      EDGE: theme.palette.warning.main,
      UMTS: theme.palette.warning.light,
      HSPA: theme.palette.info.main,
      "HSPA+": theme.palette.info.light,
      LTE: theme.palette.success.main,
      "5G": theme.palette.primary.main,
      "LTE-Adv": theme.palette.secondary.main,
    };
    return colors[networkType] || theme.palette.text.secondary;
  }

  function getColorForArfcn(arfcn) {
    const hue = ((parseInt(arfcn) || 0) * 137.508) % 360;
    return `hsl(${hue}, 70%, 65%)`;
  }

  return (
    <Box sx={{ p: isMobile ? 1 : 3 }}>
      <Typography
        variant="h4"
        gutterBottom
        sx={{ color: theme.palette.text.primary }}
      >
        Network Performance Dashboard
      </Typography>

      {/* Filters */}
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
          <MenuItem value="GSM">GSM</MenuItem>
          <MenuItem value="GPRS">GPRS</MenuItem>
          <MenuItem value="EDGE">EDGE</MenuItem>
          <MenuItem value="UMTS">UMTS</MenuItem>
          <MenuItem value="HSPA">HSPA</MenuItem>
          <MenuItem value="HSPA+">HSPA+</MenuItem>
          <MenuItem value="LTE">LTE</MenuItem>
          <MenuItem value="5G">5G</MenuItem>
          <MenuItem value="LTE-Adv">LTE-Adv</MenuItem>
        </TextField>

        <TextField
          label="Start Date"
          type="date"
          InputLabelProps={{ shrink: true }}
          onChange={(e) =>
            setDateRange({ ...dateRange, start: new Date(e.target.value) })
          }
          sx={{ flex: isMobile ? "100%" : 1 }}
          size={isMobile ? "small" : "medium"}
        />

        <TextField
          label="End Date"
          type="date"
          InputLabelProps={{ shrink: true }}
          onChange={(e) =>
            setDateRange({ ...dateRange, end: new Date(e.target.value) })
          }
          sx={{ flex: isMobile ? "100%" : 1 }}
          size={isMobile ? "small" : "medium"}
        />
      </Box>

      {loading ? (
        <Box sx={{ display: "flex", justifyContent: "center", mt: 4 }}>
          <CircularProgress />
        </Box>
      ) : (
        <>
          {/* Charts */}
          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <Paper
                sx={{
                  p: 2,
                  width: "100%",
                  height: isMobile ? 300 : 400,
                }}
              >
                <ReactECharts
                  option={pingLineChartOption}
                  style={{ height: "100%", width: "100%" }}
                  theme={theme.palette.mode}
                  ref={(el) => (chartRefs.current[0] = el)}
                />
              </Paper>
            </Grid>
            <Grid item xs={12} md={6}>
              <Paper
                sx={{
                  p: 2,
                  width: "100%",
                  height: isMobile ? 300 : 400,
                }}
              >
                <ReactECharts
                  option={arfcnLifetimeOption}
                  style={{ height: "100%", width: "100%" }}
                  theme={theme.palette.mode}
                  ref={(el) => (chartRefs.current[1] = el)}
                />
              </Paper>
            </Grid>
            <Grid item xs={12} md={6}>
              <Paper
                sx={{
                  p: 2,
                  width: "100%",
                  height: isMobile ? 300 : 400,
                }}
              >
                <ReactECharts
                  option={networkTechOption}
                  style={{ height: "100%", width: "100%" }}
                  theme={theme.palette.mode}
                  ref={(el) => (chartRefs.current[2] = el)}
                />
              </Paper>
            </Grid>
            <Grid item xs={12} md={6}>
              <Paper
                sx={{
                  p: 2,
                  width: "100%",
                  height: isMobile ? 300 : 400,
                }}
              >
                <ReactECharts
                  option={pingBoxplotOption}
                  style={{ height: "100%", width: "100%" }}
                  theme={theme.palette.mode}
                  ref={(el) => (chartRefs.current[3] = el)}
                />
              </Paper>
            </Grid>
            <Grid item xs={12}>
              <Paper
                sx={{
                  p: 2,
                  width: "100%",
                  height: isMobile ? 300 : 400,
                }}
              >
                <ReactECharts
                  option={pingGaussianOption}
                  style={{ height: "100%", width: "100%" }}
                  theme={theme.palette.mode}
                  ref={(el) => (chartRefs.current[4] = el)}
                />
              </Paper>
            </Grid>
          </Grid>

          {/* Data Table */}
          <Box sx={{ mt: 4, overflowX: "auto" }}>
            <Typography variant="h6" gutterBottom>
              Measurement Data
            </Typography>
            <TableContainer component={Paper}>
              <Table size={isMobile ? "small" : "medium"} stickyHeader>
                <TableHead>
                  <TableRow>
                    <TableCell>Timestamp</TableCell>
                    <TableCell>Network</TableCell>
                    <TableCell>Location</TableCell>
                    <TableCell>ARFCN</TableCell>
                    <TableCell>Freq</TableCell>
                    <TableCell>RSRP</TableCell>
                    <TableCell>RSRQ</TableCell>
                    <TableCell>Download</TableCell>
                    <TableCell>Upload</TableCell>
                    <TableCell>Ping</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {filteredData.slice(0, isMobile ? 5 : 10).map((row) => (
                    <TableRow key={row.id}>
                      <TableCell>{row.timestamp}</TableCell>
                      <TableCell>{row.network_type}</TableCell>
                      {!isMobile && (
                        <>
                          <TableCell>
                            {row.latitude?.toFixed(4)},{" "}
                            {row.longitude?.toFixed(4)}
                          </TableCell>
                          <TableCell>{row.arfcn}</TableCell>
                          <TableCell>
                            {row.frequency ? `${row.frequency} MHz` : "-"}
                          </TableCell>
                        </>
                      )}
                      <TableCell>{row.rsrp || "-"}</TableCell>
                      <TableCell>{row.rsrq || "-"}</TableCell>
                      <TableCell>
                        {row.http_download ? `${row.http_download} Mbps` : "-"}
                      </TableCell>{" "}
                      <TableCell>
                        {row.http_upload ? `${row.http_upload} Mbps` : "-"}
                      </TableCell>
                      <TableCell>
                        {row.ping_time ? `${row.ping_time} ms` : "-"}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Box>
        </>
      )}
    </Box>
  );
}
