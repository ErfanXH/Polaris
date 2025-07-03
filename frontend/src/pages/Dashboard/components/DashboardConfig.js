import {
  formatDateTime,
  LocalizeDateTime,
} from "../../../utils/DatetimeUtility";
import * as echarts from "echarts";

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
    const type = item.network_type;
    if (type && type != "UNKNOWN") {
      acc[type] = (acc[type] || 0) + 1;
    }
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
    const arfcn = item.arfcn;
    if (arfcn && arfcn != "UNKNOWN") {
      acc[arfcn] = (acc[arfcn] || 0) + 1;
    }
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
    const band = item.frequency_band;
    if (band && band != "UNKNOWN") {
      acc[band] = (acc[band] || 0) + 1;
    }

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

export function getSignalStrengthOverTimeOption(data, theme) {
  const rsrpSeries = [];
  const rsrqSeries = [];
  const rscpSeries = [];
  const rxLevSeries = [];

  data.forEach((item) => {
    const timestamp = item.timestamp ? LocalizeDateTime(item.timestamp) : null;
    if (timestamp && item.rsrp != null) {
      rsrpSeries.push([timestamp, item.rsrp]);
    }
    if (timestamp && item.rsrq != null) {
      rsrqSeries.push([timestamp, item.rsrq]);
    }
    if (timestamp && item.rscp != null) {
      rscpSeries.push([timestamp, item.rscp]);
    }
    if (timestamp && item.rxLev != null) {
      rxLevSeries.push([timestamp, item.rxLev]);
    }
  });

  return {
    title: { text: "Signal Strength Over Time", left: "center" },
    tooltip: { trigger: "axis" },
    xAxis: {
      type: "time",
      name: "Timestamp",
      nameLocation: "middle",
      nameGap: 25,
    },
    yAxis: {
      type: "value",
      name: "Signal (dBm / dB)",
      nameLocation: "middle",
      nameGap: 35,
    },
    grid: {
      left: 55,
      right: 20,
    },
    series: [
      {
        name: "RSRP",
        type: "line",
        data: rsrpSeries,
        showSymbol: false,
      },
      {
        name: "RSRQ",
        type: "line",
        data: rsrqSeries,
        showSymbol: false,
      },
      {
        name: "RSCP",
        type: "line",
        data: rscpSeries,
        showSymbol: false,
      },
      {
        name: "RxLev",
        type: "line",
        data: rxLevSeries,
        showSymbol: false,
      },
    ],
  };
}

export function getMeasurementCountByHourOption(data, theme) {
  const hourMap = new Array(24).fill(0);

  data.forEach((item) => {
    const timestamp = LocalizeDateTime(item.timestamp);
    const hour = timestamp.getHours();
    hourMap[hour]++;
  });

  return {
    title: { text: "Measurements by Hour", left: "center" },
    tooltip: { trigger: "axis" },
    xAxis: {
      type: "category",
      data: hourMap.map((_, hour) => hour),
      name: "Hour",
      nameLocation: "middle",
      nameGap: 25,
    },
    yAxis: {
      type: "value",
      name: "Count",
      nameLocation: "middle",
      nameGap: 30,
    },
    grid: {
      left: 50,
      right: 20,
    },
    series: [
      {
        name: "Measurements",
        type: "bar",
        data: hourMap,
        itemStyle: {
          color: theme.palette.primary.main,
        },
      },
    ],
  };
}

export function getMostFrequentCellsOption(data, theme) {
  const cellMap = {};

  data.forEach((item) => {
    const cellId = item.cell_id;
    if (cellId) {
      cellMap[cellId] = (cellMap[cellId] || 0) + 1;
    }
  });

  const topCells = Object.entries(cellMap)
    .map(([cellId, count]) => ({ cellId, count }))
    .sort((a, b) => b.count - a.count)
    .slice(0, 5);

  return {
    title: { text: "Most Frequent 5 Cells", left: "center" },
    tooltip: { trigger: "axis" },
    xAxis: {
      type: "category",
      data: topCells.map((item) => item.cellId),
      name: "Cell ID",
      nameLocation: "middle",
      nameGap: 25,
    },
    yAxis: {
      type: "value",
      name: "Count",
      nameLocation: "middle",
      nameGap: 30,
    },
    grid: {
      left: 45,
      right: 20,
    },
    series: [
      {
        name: "Measurements",
        type: "bar",
        data: topCells.map((item) => item.count),
        itemStyle: {
          color: theme.palette.success.main,
        },
      },
    ],
  };
}

export function getLineChartOption(
  data,
  valueKey,
  theme,
  titleKey = "Value",
  yLabel = "Value"
) {
  data = data.sort(
    (a, b) => LocalizeDateTime(a.timestamp) - LocalizeDateTime(b.timestamp)
  );
  const timestamps = data
    .filter((item) => item[valueKey] > 0)
    .map((item) => formatDateTime(item.timestamp));
  const values = data
    .filter((item) => item[valueKey] > 0)
    .map((item) => item[valueKey]);

  return {
    title: { text: `${titleKey} Over Time`, left: "center" },
    tooltip: { trigger: "axis" },
    xAxis: {
      type: "category",
      data: timestamps,
      name: "Time",
      nameLocation: "middle",
      nameGap: 30,
    },
    yAxis: {
      type: "value",
      name: yLabel,
      nameLocation: "middle",
      nameGap: 50,
    },
    series: [
      {
        type: "line",
        data: values,
        lineStyle: { color: theme.palette.primary.main },
        areaStyle: { color: theme.palette.primary.light },
      },
    ],
  };
}

function percentile(arr, p) {
  const index = (p / 100) * (arr.length - 1);
  const lower = Math.floor(index);
  const upper = lower + 1;
  const weight = index % 1;
  return upper < arr.length
    ? arr[lower] * (1 - weight) + arr[upper] * weight
    : arr[lower];
}

export function getBoxPlotOption(
  data,
  valueKey,
  theme,
  titleKey = "Value",
  xLabel = "Category",
  yLabel = "Value"
) {
  if (!data || data.length === 0) {
    return {
      title: { text: "No Data", left: "center" },
      series: [],
    };
  }

  const values = data
    .map((item) => Number(item[valueKey]))
    .filter((val) => !isNaN(val) && val > 0)
    .sort((a, b) => a - b);

  if (values.length === 0) {
    return {
      title: { text: "No Numeric Data", left: "center" },
      series: [],
    };
  }

  // Compute box plot stats
  const q1 = percentile(values, 25);
  const median = percentile(values, 50);
  const q3 = percentile(values, 75);
  const iqr = q3 - q1;
  const lowerWhisker = Math.max(Math.min(...values), q1 - 1.5 * iqr);
  const upperWhisker = Math.min(Math.max(...values), q3 + 1.5 * iqr);

  return {
    title: { text: `${titleKey} Over Time`, left: "center" },
    tooltip: { trigger: "item" },
    xAxis: {
      type: "category",
      data: [xLabel],
      // name: xLabel,
      nameLocation: "middle",
      nameGap: 30,
    },
    yAxis: {
      type: "value",
      name: yLabel,
      nameLocation: "middle",
      nameGap: 40,
    },
    series: [
      {
        name: "Boxplot",
        type: "boxplot",
        data: [[lowerWhisker, q1, median, q3, upperWhisker]],
        itemStyle: {
          borderColor: theme.palette.primary.main,
          color: theme.palette.primary.light,
        },
      },
    ],
  };
}

export function getDistributionOption(
  data,
  valueKey,
  theme,
  titleKey = "Value Distribution",
  binCount = 10
) {
  const values = data
    .map((item) => item[valueKey])
    .filter((v) => typeof v === "number" && v > 0); // ✅ Only positive numbers

  if (values.length === 0) return { title: { text: "No valid data" } };

  const min = Math.min(...values);
  const max = Math.max(...values);
  const range = max - min;
  const binSize = range === 0 ? 1 : range / binCount;

  const bins = new Array(binCount).fill(0);
  const labels = new Array(binCount).fill("");

  values.forEach((value) => {
    const index = Math.min(Math.floor((value - min) / binSize), binCount - 1);
    bins[index]++;
  });

  for (let i = 0; i < binCount; i++) {
    const start = (min + i * binSize).toFixed(1);
    const end = (min + (i + 1) * binSize).toFixed(1);
    labels[i] = `${start}–${end}`;
  }

  return {
    title: { text: `Distribution of ${titleKey}`, left: "center" },
    tooltip: {},
    xAxis: {
      type: "category",
      data: labels,
      name: "Range",
      nameLocation: "middle",
      nameGap: 30,
    },
    yAxis: {
      type: "value",
      name: "Number of Samples",
      nameLocation: "middle",
      nameGap: 35,
    },
    series: [
      {
        type: "bar",
        data: bins,
        itemStyle: {
          color: theme.palette.primary.main,
        },
      },
    ],
  };
}
