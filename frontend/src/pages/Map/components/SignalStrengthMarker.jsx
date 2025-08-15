import { Marker, Popup } from "react-leaflet";
import { useTheme } from "@mui/material";

const SignalStrengthMarker = ({ measurement, config }) => {
  const theme = useTheme();
  const metric_margin = {marginTop:"0.3rem",marginBottom:"0.3rem",paddingLeft:"0.4rem"};

  const getMarkerColor = (value) => {
    //const failed_color = theme.palette.mode === "dark" ? "#FFFFFF" : "#000000";
    const failed_color = "#000000";
    if (value == undefined || value == null || value ===-1 || value ==0) return failed_color;
    //if (["latency", "sms"].includes(config.metricType) && value === 0) return failed_color;
    //if (config.metric === "sms_delivery_time" && value === -1) return failed_color;

    let { min, mid, max } = config.thresholds[config.metric];
    let ratio;

    if (["latency", "sms"].includes(config.metricType)) {
      value *= -1;
      min *= -1;
      mid *= -1;
      max *= -1;
    }
    // Handle SMS failure case (-1)

    if (value <= mid) {
      ratio = Math.min(Math.max((value - min) / (mid - min), 0), 1);
      return interpolateColor(
        config.colorSpectrum.low,
        config.colorSpectrum.mid,
        ratio
      );
    } else {
      ratio = Math.min(Math.max((value - mid) / (max - mid), 0), 1);
      return interpolateColor(
        config.colorSpectrum.mid,
        config.colorSpectrum.high,
        ratio
      );
    }
  };

  const interpolateColor = (color1, color2, ratio) => {
    const r1 = parseInt(color1.substring(1, 3), 16);
    const g1 = parseInt(color1.substring(3, 5), 16);
    const b1 = parseInt(color1.substring(5, 7), 16);

    const r2 = parseInt(color2.substring(1, 3), 16);
    const g2 = parseInt(color2.substring(3, 5), 16);
    const b2 = parseInt(color2.substring(5, 7), 16);

    const r = Math.round(r1 + (r2 - r1) * ratio);
    const g = Math.round(g1 + (g2 - g1) * ratio);
    const b = Math.round(b1 + (b2 - b1) * ratio);

    return `#${r.toString(16).padStart(2, "0")}${g
      .toString(16)
      .padStart(2, "0")}${b.toString(16).padStart(2, "0")}`;
  };

  let value = measurement[config.metric];
  if (config.metric === "signal_strength") {
    value =
      measurement.ssRsrp ||
      measurement.rsrp ||
      measurement.rscp ||
      measurement.rxLev;
  } else if (config.metric === "signal_quality") {
    value = measurement.rsrq || measurement.ecIo;
  } else if (config.metric === "signal_quality") {
    value = measurement.rsrq || measurement.ecIo;
  }
  const markerColor = getMarkerColor(value);
  const unit = config.metricCategories[config.metricType].unit;
  return (
    <Marker
      position={[measurement.latitude, measurement.longitude]}
      icon={L.divIcon({
        className: "custom-marker",
        html: `<div style="background-color: ${markerColor}; 
               width: 20px; height: 20px; border-radius: 50%; 
               border: 2px solid ${theme.palette.background.paper}"></div>`,
        iconSize: [24, 24],
      })}
    >
      <Popup>
        <div >  
          <h1 className="font-extrabold m-4.5">
            {config.metric}: {value} {unit}
            <span
              style={{
                display: "inline-block",
                width: "10px",
                height: "10px",
                backgroundColor: markerColor,
                marginLeft: "5px",
                borderRadius: "50%",
              }}
            ></span>
          </h1>
          <p style ={metric_margin}>Time: {new Date(measurement.timestamp).toLocaleString()}</p>
          <p style ={metric_margin}>
            Location: {measurement.latitude.toFixed(4)},{" "}
            {measurement.longitude.toFixed(4)}
          </p>
          <hr />
          <h1 className="font-black mt-1.5 mb-4">Cell info</h1>
          <p style ={metric_margin}>LAC/TAC: {measurement.tac || measurement.lac}</p>
          <p style ={metric_margin}>RAC: {measurement.rac || "-"}</p>
          <p style ={metric_margin}>plmn-id: {measurement.plmn_id}</p>
          <p style ={metric_margin}>Cell-id: {measurement.cell_id}</p>
          <p style ={metric_margin}>Network: {measurement.network_type}</p>
          <hr />
          <h1 className="font-black mt-1.5 mb-4">signaling info</h1>
          <p style ={metric_margin}>Band: {measurement.frequency_band}</p>
          <p style ={metric_margin}>ARFCN: {measurement.arfcn}</p>
          <p style ={metric_margin}>Frequency: {measurement.frequency}</p>
          <p style ={metric_margin}>Power: {measurement.ssRsrp ||
              measurement.rsrp ||
              measurement.rscp ||
              measurement.rxLev}
          </p>
          <p style ={metric_margin}>Quality: {measurement.rsrq || measurement.ecIo}</p>
        </div>
      </Popup>
    </Marker>
  );
};

export default SignalStrengthMarker;
