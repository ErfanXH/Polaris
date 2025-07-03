import { formatDateTime } from "../../../utils/DatetimeUtility";

export const columnConfig = {
  timestamp: {
    label: "Timestamp",
    render: (row) => formatDateTime(row.timestamp),
    style: { minWidth: 180 },
  },
  location: {
    label: "Location",
    render: (row) =>
      `${row.latitude?.toFixed(4)}, ${row.longitude?.toFixed(4)}`,
  },
  network_type: {
    label: "Network",
    render: (row) => row.network_type,
  },
  frequency_band: {
    label: "Frequency Band",
    render: (row) => row.frequency_band || "-",
  },
  arfcn: {
    label: "ARFCN",
    render: (row) => row.arfcn || "-",
  },
  plmn_id: {
    label: "PLMN Id",
    render: (row) => row.plmn_id || "-",
  },
  tac: {
    label: "TAC",
    render: (row) => row.tac || "-",
  },
  lac: {
    label: "LAC",
    render: (row) => row.lac || "-",
  },
  rac: {
    label: "RAC",
    render: (row) => row.rac || "-",
  },
  cell_id: {
    label: "Cell Id",
    render: (row) => row.cell_id || "-",
  },
  frequency: {
    label: "Frequency (MHz)",
    render: (row) => row.frequency?.toFixed(3) || "-",
  },
  rsrp: { label: "RSRP", render: (row) => row.rsrp || "-" },
  rsrq: { label: "RSRQ", render: (row) => row.rsrq || "-" },
  rscp: { label: "RSCP", render: (row) => row.rscp || "-" },
  ecIo: { label: "EC/N0", render: (row) => row.ecIo || "-" },
  rxLev: { label: "RxLev", render: (row) => row.rxLev || "-" },
  http_download: {
    label: "Download (Mbps)",
    render: (row) => row.http_download?.toFixed(3) || "-",
  },
  http_upload: {
    label: "Upload (Mbps)",
    render: (row) => row.http_upload?.toFixed(3) || "-",
  },
  ping_time: {
    label: "Ping (ms)",
    render: (row) => row.ping_time?.toFixed(3) || "-",
  },
  dns_response: {
    label: "DNS (ms)",
    render: (row) => row.dns_response?.toFixed(3) || "-",
  },
  web_response: {
    label: "Web (ms)",
    render: (row) => row.web_response?.toFixed(3) || "-",
  },
  sms_delivery_time: {
    label: "SMS (ms)",
    render: (row) => row.sms_delivery_time?.toFixed(3) || "-",
  },
};
