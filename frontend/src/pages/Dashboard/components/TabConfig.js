import { columnConfig } from "./ColumnConfig";

export const tabConfig = {
  Overview: Object.keys(columnConfig),
  Measurements: [
    "timestamp",
    "location",
    "network_type",
    "frequency_band",
    "arfcn",
    "plmn_id",
    "tac",
    "lac",
    "rac",
    "cell_id",
    "frequency",
    "rsrp",
    "rsrq",
    "rscp",
    "ecIo",
    "rxLev",
  ],
  SMS: ["timestamp", "location", "network_type", "sms_delivery_time"],
  Web: ["timestamp", "location", "network_type", "web_response"],
  Upload: ["timestamp", "location", "network_type", "http_upload"],
  Download: ["timestamp", "location", "network_type", "http_download"],
  Ping: ["timestamp", "location", "network_type", "ping_time"],
  DNS: ["timestamp", "location", "network_type", "dns_response"],
};
