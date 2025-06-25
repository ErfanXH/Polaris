import { useState, useEffect, useRef } from "react";
import { MapContainer, TileLayer, useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import {
  Box,
  Typography,
  Paper,
  CircularProgress,
  useTheme,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Slider,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  IconButton,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import MapManager from "../../managers/MapManager";
import { toast } from "react-toastify";
import SettingsIcon from "@mui/icons-material/Settings";
import SignalStrengthMarker from "./SignalStrengthMarker";
import {
  getMinRange,
  getMaxRange,
  getStepSize,
  getContrastColor,
} from "../../utils/MapUtils";

// Fix for default marker icons in Leaflet
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl:
    "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png",
});

// Default configuration
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

const MapCenterController = ({ center }) => {
  const map = useMap();
  useEffect(() => {
    if (center) {
      map.flyTo(center, map.getZoom());
    }
  }, [center, map]);
  return null;
};

export default function Map() {
  const theme = useTheme();
  const mapRef = useRef(null);
  const [center, setCenter] = useState([35.6892, 51.389]);
  const [config, setConfig] = useState(DEFAULT_CONFIG);
  const [settingsOpen, setSettingsOpen] = useState(false);
  const [colorPickerOpen, setColorPickerOpen] = useState(false);
  const [currentColor, setCurrentColor] = useState("low");

  // Fetch measurements data
  const {
    data: measurements,
    isLoading,
    error,
  } = useQuery({
    queryKey: ["measurements"],
    queryFn: () => MapManager.getMeasurements(),
    onSuccess: (data) => {
      if (data.length > 0) {
        setCenter([data[0].latitude, data[0].longitude]);
      }
    },
  });

  // Handle errors
  useEffect(() => {
    if (error) {
      toast.error("Failed to load measurement data");
    }
  }, [error]);

  // Handle metric category change
  const handleMetricTypeChange = (event) => {
    const metricType = event.target.value;
    setConfig({
      ...config,
      metricType,
      metric: DEFAULT_CONFIG.metricCategories[metricType].default,
    });
  };

  // Handle specific metric change
  const handleMetricChange = (event) => {
    setConfig({
      ...config,
      metric: event.target.value,
    });
  };

  // Handle threshold change
  const handleThresholdChange = (threshold, value) => {
    setConfig({
      ...config,
      thresholds: {
        ...config.thresholds,
        [config.metric]: {
          ...config.thresholds[config.metric],
          [threshold]: value,
        },
      },
    });
  };

  // Open color picker
  const openColorPicker = (colorType) => {
    setCurrentColor(colorType);
    setColorPickerOpen(true);
  };

  // Apply color change
  const handleColorChange = (color) => {
    setConfig({
      ...config,
      colorSpectrum: {
        ...config.colorSpectrum,
        [currentColor]: color,
      },
    });
  };

  // Reset to defaults
  const resetToDefaults = () => {
    setConfig(DEFAULT_CONFIG);
  };

  // Get current thresholds
  const currentThresholds = config.thresholds[config.metric];
  const currentCategory = config.metricCategories[config.metricType];
  const currentUnit = currentCategory.unit;
  const isLatency = ["latency", "sms"].includes(config.metricType);
  return (
    <Box
      sx={{
        height: "81.7vh",
        width: "100%",
        display: "flex",
        flexDirection: "column",
        backgroundColor: theme.palette.background.default,
      }}
    >
      {/* Settings Button */}
      <Box
        sx={{ position: "absolute", top: "7rem", right: "3rem", zIndex: 1000 }}
      >
        <IconButton
          aria-label="settings"
          sx={{
            backgroundColor: theme.palette.primary.main,
            borderRadius: "20%",
            "&:hover": {
              backgroundColor: "primary.dark",
            },
          }}
          variant="contained"
          onClick={() => setSettingsOpen(true)}
        >
          <SettingsIcon />
        </IconButton>
      </Box>

      {/* Map Container */}
      <Box sx={{ flex: 1, position: "relative" }}>
        {isLoading ? (
          <Box
            sx={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              height: "100%",
            }}
          >
            <CircularProgress color="primary" />
          </Box>
        ) : (
          <MapContainer
            center={center}
            zoom={13}
            zoomControl={false}
            style={{ height: "100%", width: "100%" }}
            whenCreated={(map) => {
              mapRef.current = map;
            }}
          >
            <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
            <MapCenterController center={center} />
            {measurements?.map((measurement) => (
              <SignalStrengthMarker
                key={measurement.id}
                measurement={measurement}
                config={config}
              />
            ))}
          </MapContainer>
        )}

        {/* Legend */}
        <Paper
          elevation={3}
          sx={{
            position: "absolute",
            bottom: 16,
            right: 16,
            p: 2,
            zIndex: 1000,
            backgroundColor: theme.palette.background.paper,
          }}
        >
          <Typography variant="subtitle2" fontWeight="bold" mb={1}>
            {config.metric} ({currentUnit}) Legend
          </Typography>
          <div className="space-y-2">
            <div className="flex items-center">
              <div
                className="w-4 h-4 rounded-full mr-2"
                style={{ backgroundColor: config.colorSpectrum.high }}
              />
              <span>
                Excellent (
                {isLatency
                  ? "≤"
                  : "≥"}{" "}
                {currentThresholds.max})
              </span>
            </div>
            <div className="flex items-center">
              <div
                className="w-4 h-4 rounded-full mr-2"
                style={{ backgroundColor: config.colorSpectrum.mid }}
              />
              <span>
                Moderate ({currentThresholds.min} to {currentThresholds.max})
              </span>
            </div>
            <div className="flex items-center">
              <div
                className="w-4 h-4 rounded-full mr-2"
                style={{ backgroundColor: config.colorSpectrum.low }}
              />
              <span>
                Poor (
                {isLatency
                  ? "≥"
                  : "≤"}{" "}
                {currentThresholds.min})
              </span>
            </div>
            {config.metric === "sms_delivery_time" && (
              <div className="flex items-center">
                <div
                  className="w-4 h-4 rounded-full mr-2"
                  style={{ backgroundColor: "#000000" }}
                />
                <span>Failed (-1)</span>
              </div>
            )}
          </div>
        </Paper>
      </Box>

      {/* Settings Dialog */}
      <Dialog
        open={settingsOpen}
        onClose={() => setSettingsOpen(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Map Visualization Settings</DialogTitle>
        <DialogContent>
          <FormControl fullWidth sx={{ mt: 2 }}>
            <InputLabel>Metric Category</InputLabel>
            <Select
              value={config.metricType}
              label="Metric Category"
              onChange={handleMetricTypeChange}
            >
              {Object.keys(config.metricCategories).map((category) => (
                <MenuItem key={category} value={category}>
                  {config.metricCategories[category].name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <FormControl fullWidth sx={{ mt: 2 }}>
            <InputLabel>Specific Metric</InputLabel>
            <Select
              value={config.metric}
              label="Specific Metric"
              onChange={handleMetricChange}
            >
              {config.metricCategories[config.metricType].metrics.map(
                (metric) => (
                  <MenuItem key={metric} value={metric}>
                    {metric} ({config.metricCategories[config.metricType].unit})
                  </MenuItem>
                )
              )}
            </Select>
          </FormControl>

          <Typography variant="subtitle1" sx={{ mt: 3 }}>
            Threshold Values ({currentUnit})
          </Typography>

          <Typography gutterBottom sx={{ mt: 2 }}>
            {isLatency
              ? "Maximum (Poor)"
              : "Minimum (Poor)"}
          </Typography>
          <Slider
            value={currentThresholds.min}
            onChange={(e, value) => handleThresholdChange("min", value)}
            min={isLatency ? currentThresholds.mid : getMinRange(config.metric)}
            max={isLatency ? getMinRange(config.metric) : currentThresholds.mid}
            step={getStepSize(config.metric)}
            valueLabelDisplay="auto"
          />

          <Typography gutterBottom sx={{ mt: 2 }}>
            Moderate Threshold
          </Typography>
          <Slider
              value={currentThresholds.mid}
              onChange={(e, value) => handleThresholdChange("mid", value)}
              min={isLatency ? currentThresholds.max : currentThresholds.min}
              max={isLatency ? currentThresholds.min : currentThresholds.max}
              step={getStepSize(config.metric)}
              valueLabelDisplay="auto"
            />
          <Typography gutterBottom sx={{ mt: 2 }}>
            {isLatency
              ? "Minimum (Excellent)"
              : "Maximum (Excellent)"}
          </Typography>
          <Slider
            value={currentThresholds.max}
            onChange={(e, value) => handleThresholdChange("max", value)}
            min={isLatency ? getMaxRange(config.metric) : currentThresholds.mid}
            max={isLatency ? currentThresholds.mid : getMaxRange(config.metric)}
            step={getStepSize(config.metric)}
            valueLabelDisplay="auto"
          />

          <Typography variant="subtitle1" sx={{ mt: 3 }}>
            Color Spectrum
          </Typography>
          <Box sx={{ display: "flex", gap: 2, mt: 2 }}>
            <Box>
              <Typography>Poor</Typography>
              <div
                style={{
                  width: 50,
                  height: 50,
                  backgroundColor: config.colorSpectrum.low,
                  cursor: "pointer",
                }}
                onClick={() => openColorPicker("low")}
              />
            </Box>
            <Box>
              <Typography>Moderate</Typography>
              <div
                style={{
                  width: 50,
                  height: 50,
                  backgroundColor: config.colorSpectrum.mid,
                  cursor: "pointer",
                }}
                onClick={() => openColorPicker("mid")}
              />
            </Box>
            <Box>
              <Typography>Excellent</Typography>
              <div
                style={{
                  width: 50,
                  height: 50,
                  backgroundColor: config.colorSpectrum.high,
                  cursor: "pointer",
                }}
                onClick={() => openColorPicker("high")}
              />
            </Box>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={resetToDefaults}>Reset to Defaults</Button>
          <Button onClick={() => setSettingsOpen(false)}>Apply</Button>
        </DialogActions>
      </Dialog>

      {/* Color Picker Dialog */}
      <Dialog open={colorPickerOpen} onClose={() => setColorPickerOpen(false)}>
        <DialogTitle>Select {currentColor} color</DialogTitle>
        <DialogContent sx={{ pt: 3, minWidth: 300 }}>
          <TextField
            fullWidth
            type="color"
            label={`${
              currentColor.charAt(0).toUpperCase() + currentColor.slice(1)
            } Color`}
            value={config.colorSpectrum[currentColor]}
            onChange={(e) => handleColorChange(e.target.value)}
            InputLabelProps={{
              shrink: true,
            }}
            inputProps={{
              style: {
                height: "80px",
                width: "100%",
                cursor: "pointer",
                padding: "8px",
                borderRadius: "4px",
                border: `1px solid ${theme.palette.divider}`,
              },
            }}
            variant="outlined"
            sx={{ mt: 2 }}
          />
          <Box
            sx={{
              mt: 2,
              p: 2,
              backgroundColor: config.colorSpectrum[currentColor],
              borderRadius: 1,
              border: `1px solid ${theme.palette.divider}`,
            }}
          >
            <Typography
              variant="body2"
              sx={{
                color: getContrastColor(config.colorSpectrum[currentColor]),
                textAlign: "center",
              }}
            >
              {config.colorSpectrum[currentColor]}
            </Typography>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setColorPickerOpen(false)}>Close</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
