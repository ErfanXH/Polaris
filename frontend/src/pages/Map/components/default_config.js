const DEFAULT_CONFIG = {
  metric: "signal_strength",
  metricType: "signal_strength",
  thresholds: {
    // Signal Strength
    signal_strength: { min: -120, mid: -90, max: -70 },
    ssRsrp: { min: -120, mid: -90, max: -70 },
    rsrp: { min: -110, mid: -85, max: -50 },
    rscp: { min: -110, mid: -85, max: -60 },
    rxLev: { min: -100, mid: -75, max: -50 },

    // Signal Quality
    signal_quality: { min: -20, mid: -12, max: -5 },
    rsrq: { min: -19, mid: -12, max: -5 },
    ecIo: { min: -20, mid: -12, max: -5 },

    // Throughput
    http_upload: { min: 0.1, mid: 4, max: 8 },
    http_download: { min: 0.1, mid: 30, max: 60 },

    // Latency
    ping_time: { min: 140, mid: 70, max: 20 },
    dns_response: { min: 80, mid: 50, max: 10 },
    web_response: { min: 1500, mid: 1000, max: 500 },

    // SMS
    sms_delivery_time: { min: 5000, mid: 2000, max: 500 },
  },
  colorSpectrum: {
    low: "#ff0000",
    mid: "#ffff00",
    high: "#00ff00",
  },
  metricCategories: {
    signal_strength: {
      name: "Signal Strength",
      metrics: ["signal_strength", "ssRsrp", "rsrp", "rscp", "rxLev"],
      default: "signal_strength",
      unit: "dBm",
    },
    signal_quality: {
      name: "Signal Quality",
      metrics: ["signal_quality", "rsrq", "ecIo"],
      default: "signal_quality",
      unit: "dB",
    },
    throughput: {
      name: "Throughput",
      metrics: ["http_upload", "http_download"],
      default: "http_download",
      unit: "Mbps",
    },
    latency: {
      name: "Latency",
      metrics: ["ping_time", "dns_response", "web_response"],
      default: "ping_time",
      unit: "ms",
    },
    sms: {
      name: "SMS Delivery",
      metrics: ["sms_delivery_time"],
      default: "sms_delivery_time",
      unit: "ms",
    },
  },
};

export default DEFAULT_CONFIG