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
} from "@mui/material";
import * as echarts from "echarts";
import ReactECharts from "echarts-for-react";
import DashboardManager from "../../managers/DashboardManager";

export default function Dashboard() {
  const [measurements, setMeasurements] = useState([]);
  const [filteredData, setFilteredData] = useState([]);
  const [networkTypeFilter, setNetworkTypeFilter] = useState("all");
  const [dateRange, setDateRange] = useState({ start: null, end: null });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const data = await DashboardManager.getAll();
        setMeasurements(data);
        setFilteredData(data);
      } catch (error) {
        console.error("Error fetching measurements:", error);
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

  // ARFCN Lifetime Pie Chart
  const arfcnLifetimeOption = {
    title: {
      text: "ARFCN Lifetime Distribution (4G)",
      left: "center",
    },
    tooltip: {
      trigger: "item",
    },
    legend: {
      orient: "vertical",
      left: "left",
    },
    series: [
      {
        name: "ARFCN",
        type: "pie",
        radius: "50%",
        data: filteredData
          .filter((m) => m.network_type === "LTE")
          .reduce((acc, curr) => {
            const arfcn = curr.arfcn || "Unknown";
            const existing = acc.find((item) => item.name === arfcn);
            if (existing) {
              existing.value++;
            } else {
              acc.push({ name: arfcn, value: 1 });
            }
            return acc;
          }, []),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };

  // Network Technology Pie Chart
  const networkTechOption = {
    title: {
      text: "Network Technology Distribution",
      left: "center",
    },
    tooltip: {
      trigger: "item",
    },
    legend: {
      orient: "vertical",
      left: "left",
    },
    series: [
      {
        name: "Technology",
        type: "pie",
        radius: "50%",
        data: Object.entries(
          filteredData.reduce((acc, curr) => {
            acc[curr.network_type] = (acc[curr.network_type] || 0) + 1;
            return acc;
          }, {})
        ).map(([name, value]) => ({ name, value })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };

  // Ping/Delay Boxplot
  const pingBoxplotOption = {
    title: {
      text: "Ping/Delay Statistics (Boxplot)",
      left: "center",
    },
    tooltip: {
      trigger: "item",
      axisPointer: {
        type: "shadow",
      },
    },
    grid: {
      left: "10%",
      right: "10%",
      bottom: "15%",
    },
    xAxis: {
      type: "category",
      data: ["Ping Time", "DNS Response", "Web Response", "SMS Delivery"],
      boundaryGap: true,
      nameGap: 30,
      splitArea: {
        show: false,
      },
      axisLabel: {
        interval: 0,
        rotate: 45,
      },
    },
    yAxis: {
      type: "value",
      name: "Time (ms)",
      splitArea: {
        show: true,
      },
    },
    series: [
      {
        name: "boxplot",
        type: "boxplot",
        data: [
          calculateBoxplotData(filteredData.map((m) => m.ping_time)),
          calculateBoxplotData(filteredData.map((m) => m.dns_response)),
          calculateBoxplotData(filteredData.map((m) => m.web_response)),
          calculateBoxplotData(filteredData.map((m) => m.sms_delivery_time)),
        ],
        tooltip: {
          formatter: function (param) {
            return [
              param.name + ": ",
              "Upper: " + param.data[4],
              "Q3: " + param.data[3],
              "Median: " + param.data[2],
              "Q1: " + param.data[1],
              "Lower: " + param.data[0],
            ].join("<br/>");
          },
        },
      },
    ],
  };

  // Gaussian Chart of Ping/Delay
  const pingGaussianOption = {
    title: {
      text: "Ping Time Distribution",
      left: "center",
    },
    tooltip: {
      trigger: "item",
      formatter: "{a} <br/>{b} : {c} ({d}%)",
    },
    legend: {
      left: "center",
      top: "bottom",
      data: ["Ping Time"],
    },
    toolbox: {
      show: true,
      feature: {
        mark: { show: true },
        dataView: { show: true, readOnly: false },
        restore: { show: true },
        saveAsImage: { show: true },
      },
    },
    series: [
      {
        name: "Ping Time",
        type: "pie",
        radius: [30, 110],
        center: ["50%", "50%"],
        roseType: "area",
        itemStyle: {
          borderRadius: 8,
        },
        data: createGaussianData(filteredData.map((m) => m.ping_time)),
      },
    ],
  };

  function calculateBoxplotData(data) {
    if (!data.length) return [0, 0, 0, 0, 0];

    const sorted = [...data].sort((a, b) => a - b);
    const q1 = sorted[Math.floor(sorted.length * 0.25)];
    const median = sorted[Math.floor(sorted.length * 0.5)];
    const q3 = sorted[Math.floor(sorted.length * 0.75)];
    const iqr = q3 - q1;
    const lower = Math.max(sorted[0], q1 - 1.5 * iqr);
    const upper = Math.min(sorted[sorted.length - 1], q3 + 1.5 * iqr);

    return [lower, q1, median, q3, upper];
  }

  function createGaussianData(values) {
    if (!values.length) return [];

    const min = Math.min(...values);
    const max = Math.max(...values);
    const step = (max - min) / 10;

    const buckets = {};
    for (let i = min; i <= max; i += step) {
      buckets[i.toFixed(2)] = 0;
    }

    values.forEach((value) => {
      const bucket = Math.floor(value / step) * step;
      buckets[bucket.toFixed(2)] = (buckets[bucket.toFixed(2)] || 0) + 1;
    });

    return Object.entries(buckets).map(([value, count]) => ({
      value: parseFloat(value),
      name: `${value} ms`,
      count,
    }));
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Network Performance Dashboard
      </Typography>

      {/* Filters */}
      <Box sx={{ mb: 4, display: "flex", gap: 2 }}>
        <TextField
          select
          label="Network Type"
          value={networkTypeFilter}
          onChange={(e) => setNetworkTypeFilter(e.target.value)}
          sx={{ minWidth: 200 }}
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
        />

        <TextField
          label="End Date"
          type="date"
          InputLabelProps={{ shrink: true }}
          onChange={(e) =>
            setDateRange({ ...dateRange, end: new Date(e.target.value) })
          }
        />
      </Box>

      {/* Charts */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 2, height: "400px" }}>
            <ReactECharts
              option={arfcnLifetimeOption}
              style={{ height: "100%" }}
            />
          </Paper>
        </Grid>
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 2, height: "400px" }}>
            <ReactECharts
              option={networkTechOption}
              style={{ height: "100%" }}
            />
          </Paper>
        </Grid>
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 2, height: "400px" }}>
            <ReactECharts
              option={pingBoxplotOption}
              style={{ height: "100%" }}
            />
          </Paper>
        </Grid>
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 2, height: "400px" }}>
            <ReactECharts
              option={pingGaussianOption}
              style={{ height: "100%" }}
            />
          </Paper>
        </Grid>
      </Grid>

      {/* Data Table */}
      <Box sx={{ mt: 4 }}>
        <Typography variant="h6" gutterBottom>
          Measurement Data
        </Typography>
        <TableContainer component={Paper}>
          <Table sx={{ minWidth: 650 }} aria-label="measurement data table">
            <TableHead>
              <TableRow>
                <TableCell>Timestamp</TableCell>
                <TableCell>Network Type</TableCell>
                <TableCell>Location</TableCell>
                <TableCell>ARFCN</TableCell>
                <TableCell>Frequency</TableCell>
                <TableCell>RSRP</TableCell>
                <TableCell>RSRQ</TableCell>
                <TableCell>HTTP Download</TableCell>
                <TableCell>HTTP Upload</TableCell>
                <TableCell>Ping Time</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredData.map((row) => (
                <TableRow key={row.id}>
                  <TableCell>
                    {new Date(row.timestamp).toLocaleString()}
                  </TableCell>
                  <TableCell>{row.network_type}</TableCell>
                  <TableCell>
                    {row.latitude}, {row.longitude}
                  </TableCell>
                  <TableCell>{row.arfcn}</TableCell>
                  <TableCell>{row.frequency}</TableCell>
                  <TableCell>{row.rsrp}</TableCell>
                  <TableCell>{row.rsrq}</TableCell>
                  <TableCell>{row.http_download} Mbps</TableCell>
                  <TableCell>{row.http_upload} Mbps</TableCell>
                  <TableCell>{row.ping_time} ms</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Box>
    </Box>
  );
}
