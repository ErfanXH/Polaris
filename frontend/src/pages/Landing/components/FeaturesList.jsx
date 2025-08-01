import {
  Speed,
  BarChart,
  Map,
  CellTower,
  NetworkCheck,
  Sms,
} from "@mui/icons-material";

const FeaturesList = [
  {
    icon: <CellTower fontSize="large" />,
    title: "Network Signal Analysis",
    description:
      "Measure RSRP, RSRQ and other critical signal parameters with professional accuracy",
  },
  {
    icon: <Speed fontSize="large" />,
    title: "Automated Speed Tests",
    description:
      "Schedule periodic speed tests and compare performance across locations and times",
  },
  {
    icon: <BarChart fontSize="large" />,
    title: "Historical Data",
    description:
      "Track and visualize network performance trends with detailed charts and graphs",
  },
  {
    icon: <Map fontSize="large" />,
    title: "Coverage Mapping",
    description:
      "Create signal strength maps with GPS-tagged measurements for comprehensive analysis",
  },
  {
    icon: <NetworkCheck fontSize="large" />,
    title: "Multi-Carrier Comparison",
    description:
      "Compare performance across different network operators and technologies",
  },
  {
    icon: <Sms fontSize="large" />,
    title: "SMS Latency Testing",
    description:
      "Measure SMS delivery times between devices for complete communication analysis",
  },
];

export default FeaturesList;
