import { useState } from "react";
import {
  Grid,
  Paper,
  useTheme,
  useMediaQuery,
  IconButton,
  Dialog,
  DialogContent,
  Box,
} from "@mui/material";
import ReactECharts from "echarts-for-react";
import FullscreenIcon from "@mui/icons-material/Fullscreen";

function ChartContainer({ option, chartRef }) {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const [fullscreen, setFullscreen] = useState(false);

  return (
    <>
      <Paper
        sx={{
          p: 2,
          width: "100%",
          height: isMobile ? 300 : 400,
          position: "relative",
        }}
      >
        <IconButton
          onClick={() => setFullscreen(true)}
          sx={{ position: "absolute", top: 16, right: 16, zIndex: 1000 }}
        >
          <FullscreenIcon />
        </IconButton>
        <ReactECharts
          option={option}
          style={{ height: "100%", width: "100%" }}
          theme={theme.palette.mode}
          ref={chartRef}
        />
      </Paper>
      <Dialog fullScreen open={fullscreen} onClose={() => setFullscreen(false)}>
        <DialogContent
          sx={{ p: 2, backgroundColor: theme.palette.background.default }}
        >
          <Box
            sx={{
              position: "relative",
              height: "100vh",
              width: "100%",
            }}
          >
            <IconButton
              onClick={() => setFullscreen(false)}
              sx={{ position: "absolute", top: 4, right: 4, zIndex: 100000 }}
            >
              ✕
            </IconButton>

            <Box sx={{ height: "100%", width: "100%" }}>
              <ReactECharts
                option={option}
                style={{ height: "100%", width: "100%" }}
                theme={theme.palette.mode}
                ref={chartRef}
              />
            </Box>
          </Box>
        </DialogContent>
      </Dialog>
    </>
  );
}

export function NetworkTechnologyChart({ option, chartRef }) {
  return <ChartContainer option={option} chartRef={chartRef} />;
}

export function ArfcnDistributionChart({ option, chartRef }) {
  return <ChartContainer option={option} chartRef={chartRef} />;
}

export function FrequencyBandChart({ option, chartRef }) {
  return <ChartContainer option={option} chartRef={chartRef} />;
}

export function RsrpRsrqScatterChart({ option, chartRef }) {
  return <ChartContainer option={option} chartRef={chartRef} />;
}
