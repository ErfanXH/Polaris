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

function getNetworkTechOption(data, theme) {
  const counts = data.reduce((acc, curr) => {
    acc[curr.network_type] = (acc[curr.network_type] || 0) + 1;
    return acc;
  }, {});
  return {
    title: { text: "Network Technology" },
    tooltip: { trigger: "item" },
    legend: { orient: "vertical", left: "left" },
    series: [
      {
        name: "Technology",
        type: "pie",
        roseType: "radius",
        data: Object.entries(counts).map(([name, value]) => ({
          name,
          value,
          itemStyle: { color: getColorForNetworkType(name, theme) },
        })),
      },
    ],
  };
}

function getArfcnOption(data, theme) {
  const arfcnData = data
    .filter((d) => d.network_type === "LTE")
    .reduce((acc, d) => {
      const arfcn = d.arfcn || "Unknown";
      const existing = acc.find((a) => a.name === arfcn);
      if (existing) existing.value++;
      else
        acc.push({
          name: arfcn,
          value: 1,
          itemStyle: { color: getColorForArfcn(arfcn) },
        });
      return acc;
    }, []);
  return {
    title: { text: "ARFCN Distribution (4G)", left: "center" },
    series: [
      {
        name: "ARFCN",
        type: "pie",
        data: arfcnData,
      },
    ],
  };
}

function getPingLineOption(data, theme) {
  const sorted = [...data].sort(
    (a, b) => new Date(a.timestamp) - new Date(b.timestamp)
  );
  return {
    title: { text: "Ping Time Trend" },
    tooltip: { trigger: "axis" },
    xAxis: {
      type: "category",
      data: sorted.map((m) => m.timestamp),
    },
    yAxis: { type: "value", name: "Ping (ms)" },
    series: [
      {
        name: "Ping Time",
        type: "line",
        smooth: true,
        data: sorted.map((m) => m.ping_time),
        itemStyle: { color: theme.palette.primary.main },
      },
    ],
  };
}

function getBoxplotOption(data, theme) {
  const stats = (arr) => {
    if (!arr.length) return [0, 0, 0, 0, 0];
    const sorted = [...arr].filter((n) => n != null).sort((a, b) => a - b);
    const q1 = sorted[Math.floor(sorted.length * 0.25)] || 0;
    const median = sorted[Math.floor(sorted.length * 0.5)] || 0;
    const q3 = sorted[Math.floor(sorted.length * 0.75)] || 0;
    const iqr = q3 - q1;
    const lower = Math.max(sorted[0], q1 - 1.5 * iqr);
    const upper = Math.min(sorted[sorted.length - 1], q3 + 1.5 * iqr);
    return [lower, q1, median, q3, upper];
  };

  return {
    title: { text: "Latency Statistics", left: "center" },
    xAxis: { type: "category", data: ["Ping", "DNS", "Web", "SMS"] },
    yAxis: { type: "value", name: "Time (ms)" },
    series: [
      {
        name: "boxplot",
        type: "boxplot",
        data: [
          stats(data.map((m) => m.ping_time)),
          stats(data.map((m) => m.dns_response)),
          stats(data.map((m) => m.web_response)),
          stats(data.map((m) => m.sms_delivery_time)),
        ],
      },
    ],
  };
}

function getGaussianOption(data, theme) {
  const values = data.map((m) => m.ping_time).filter((v) => v != null);
  const min = Math.min(...values);
  const max = Math.max(...values);
  const step = (max - min) / 10 || 1;

  const buckets = {};
  for (let i = 0; i <= 10; i++) {
    const key = (min + i * step).toFixed(2);
    buckets[key] = 0;
  }

  values.forEach((v) => {
    const idx = Math.floor((v - min) / step);
    const key = (min + idx * step).toFixed(2);
    if (buckets[key] !== undefined) buckets[key]++;
  });

  return {
    title: { text: "Ping Time Distribution", left: "center" },
    xAxis: { type: "category", data: Object.keys(buckets) },
    yAxis: { type: "value", name: "Count" },
    series: [
      {
        type: "bar",
        data: Object.values(buckets),
        itemStyle: { color: theme.palette.secondary.main },
      },
    ],
  };
}

export {
  getNetworkTechOption,
  getArfcnOption,
  getPingLineOption,
  getBoxplotOption,
  getGaussianOption,
};
