import { useState, useEffect, useRef } from "react";
import { MapContainer, TileLayer, Marker, Popup, useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import {
  Box,
  Typography,
  Paper,
  CircularProgress,
  useTheme,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import MapManager from "../../managers/MapManager";
import { toast } from "react-toastify";

// Fix for default marker icons in Leaflet
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl:
    "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png",
});
const MapCenterController = ({ center }) => {
  const map = useMap();
  useEffect(() => {
    if (center) {
      map.flyTo(center, map.getZoom());
    }
  }, [center, map]);
  return null;
};

const SignalStrengthMarker = ({ measurement }) => {
  const theme = useTheme();
  // Determine marker color based on signal strength
  const getMarkerColor = (strength) => {
    if (strength >= -70) return theme.palette.success.main; // Green for strong signal
    if (strength >= -85) return theme.palette.warning.main; // Yellow for moderate
    return theme.palette.error.main; // Red for weak
  };
  const signal_strength = measurement.ssRsrp || measurement.rsrp||measurement.rscp||measurement.rxLev
  return (
    <Marker
      position={[measurement.latitude, measurement.longitude]}
      icon={L.divIcon({
        className: "custom-marker",
        html: `<div style="background-color: ${getMarkerColor(
          signal_strength
        )}; 
               width: 20px; height: 20px; border-radius: 50%; 
               border: 2px solid ${theme.palette.background.paper}"></div>`,
        iconSize: [24, 24],
      })}
    >
      <Popup>
        <div className="space-y-1">
          <p className="font-semibold">
            Signal: {signal_strength} dBm
          </p>
          <p>Network: {measurement.network_type}</p>
          <p>
            Location: {measurement.latitude.toFixed(4)},{" "}
            {measurement.longitude.toFixed(4)}
          </p>
          <p>Time: {new Date(measurement.timestamp).toLocaleString()}</p>
        </div>
      </Popup>
    </Marker>
  );
};

export default function Map() {
  const theme = useTheme();
  const mapRef = useRef(null);
  const [center, setCenter] = useState([35.6892, 51.389]); // Default to Tehran coordinates

  // Fetch measurements data
  const {
    data: measurements,
    isLoading,
    error,
  } = useQuery({
    queryKey: ["measurements"],
    queryFn: () => MapManager.getMeasurements(),
    onSuccess: (data) => {
      console.log(data)
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

  return (
    <Box
      sx={{
        height: "100vh",
        width: "100%",
        display: "flex",
        flexDirection: "column",
        backgroundColor: theme.palette.background.default,
      }}
    >
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
            style={{ height: "100%", width: "100%" }}
            whenCreated={(map) => {
              mapRef.current = map;
            }}
          >
            <TileLayer
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            />
            <MapCenterController center={center} />
            {measurements?.map((measurement) => (
              <SignalStrengthMarker
                key={measurement.id}
                measurement={measurement}
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
            Signal Strength Legend
          </Typography>
          <div className="space-y-2">
            <div className="flex items-center">
              <div
                className="w-4 h-4 rounded-full mr-2"
                style={{ backgroundColor: theme.palette.success.main }}
              />
              <span>Strong (≥ -70 dBm)</span>
            </div>
            <div className="flex items-center">
              <div
                className="w-4 h-4 rounded-full mr-2"
                style={{ backgroundColor: theme.palette.warning.main }}
              />
              <span>Moderate (-71 to -85 dBm)</span>
            </div>
            <div className="flex items-center">
              <div
                className="w-4 h-4 rounded-full mr-2"
                style={{ backgroundColor: theme.palette.error.main }}
              />
              <span>Weak (≤ -86 dBm)</span>
            </div>
          </div>
        </Paper>
      </Box>
    </Box>
  );
}
