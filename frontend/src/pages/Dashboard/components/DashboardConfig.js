import { formatDateTime } from "../../../utils/FormatDatetime";

function getColorForNetworkType(networkType, theme) {
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

export function getNetworkTechPieOption(data, theme) {
  const counts = data.reduce((acc, item) => {
    const type = item.network_type || "Unknown";
    acc[type] = (acc[type] || 0) + 1;
    return acc;
  }, {});

  return {
    title: { text: "Network Types", left: "center" },
    tooltip: { trigger: "item" },
    grid: { top: "30%" },
    series: [
      {
        name: "Network Type",
        type: "pie",
        // radius: "50%",
        center: ["50%", "55%"],
        data: Object.entries(counts).map(([name, value]) => ({ name, value })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
    // toolbox: {
    //   show: true,
    //   feature: {
    //     dataZoom: {
    //       yAxisIndex: "none",
    //     },
    //     dataView: { readOnly: true },
    //     restore: {},
    //     saveAsImage: {},
    //   },
    // },
  };
}

// 2. ARFCN Pie Chart
export function getArfcnPieOption(data, theme) {
  const counts = data.reduce((acc, item) => {
    const arfcn = item.arfcn || "Unknown";
    acc[arfcn] = (acc[arfcn] || 0) + 1;
    return acc;
  }, {});

  return {
    title: { text: "ARFCN Distribution", left: "center" },
    tooltip: { trigger: "item" },
    series: [
      {
        name: "ARFCN",
        type: "pie",
        // radius: "50%",
        center: ["50%", "55%"],
        data: Object.entries(counts).map(([name, value]) => ({ name, value })),
      },
    ],
  };
}

export function getFrequencyBandBarOption(data, theme) {
  const counts = data.reduce((acc, item) => {
    const band = item.frequency_band || "Unknown";
    acc[band] = (acc[band] || 0) + 1;
    return acc;
  }, {});

  return {
    title: { text: "Frequency Band Usage", left: "center" },
    tooltip: {},
    xAxis: {
      type: "category",
      data: Object.keys(counts),
      axisLabel: { rotate: 45 },
    },
    yAxis: { type: "value" },
    series: [
      {
        data: Object.values(counts),
        type: "bar",
        itemStyle: {
          color: theme.palette.primary.main,
        },
      },
    ],
  };
}

export function getRsrpRsrqScatterOption(data, theme) {
  const points = data
    .filter((item) => item.rsrp && item.rsrq)
    .map((item) => [item.rsrp, item.rsrq]);

  return {
    title: { text: "RSRP vs RSRQ", left: "center" },
    tooltip: {
      trigger: "item",
      formatter: function (params) {
        const [rsrp, rsrq] = params.value;
        return `RSRP: ${rsrp}<br/>RSRQ: ${rsrq}`;
      },
    },
    xAxis: {
      name: "RSRP",
      nameLocation: "middle",
      nameGap: 25,
      type: "value",
    },
    yAxis: {
      name: "RSRQ",
      nameLocation: "middle",
      nameGap: 30,
      type: "value",
    },
    grid: {
      left: 45,
      right: 20,
    },
    series: [
      {
        symbolSize: 8,
        data: points,
        type: "scatter",
        itemStyle: {
          color: theme.palette.secondary.main,
        },
      },
    ],
  };
}
