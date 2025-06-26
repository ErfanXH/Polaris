export function getMinRange(metric) {
  const ranges = {
    signal_strength: -140,
    ssRsrp: -140,
    rsrp: -140,
    rscp: -140,
    rxLev: -120,
    rsrq: -30,
    ecIo: -30,
    http_upload: 100,
    http_download: 200,
    ping_time: 500,
    dns_response: 500,
    web_response: 10000,
    sms_delivery_time: 10000,
  };
  if (ranges[metric] === undefined || ranges[metric] === null) return 0;
  return ranges[metric];
}

export function getMaxRange(metric) {
  const ranges = {
    signal_quality: -50,
    ssRsrp: -50,
    rsrp: -50,
    rscp: -50,
    rxLev: -50,
    rsrq: 0,
    ecIo: 0,
    http_upload: 0,
    http_download: 0,
    ping_time: 0,
    dns_response: 0,
    web_response: 0,
    sms_delivery_time: 0,
  };
  if (ranges[metric] === undefined || ranges[metric] === null) return 100;
  return ranges[metric];
}

export function getStepSize(metric) {
  return ["http_upload", "http_download"].includes(metric) ? 0.1 : 1;
}

export function getContrastColor(hexColor) {
  const r = parseInt(hexColor.slice(1, 3), 16);
  const g = parseInt(hexColor.slice(3, 5), 16);
  const b = parseInt(hexColor.slice(5, 7), 16);
  const luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;
  return luminance > 0.5 ? "#000000" : "#FFFFFF";
}
