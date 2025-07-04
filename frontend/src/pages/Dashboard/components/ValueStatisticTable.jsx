import React from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Typography,
} from "@mui/material";
import { mean, median, variance, standardDeviation } from "simple-statistics";

function computeStats(values) {
  const sorted = [...values].sort((a, b) => a - b);
  const μ = mean(sorted);
  const σ = standardDeviation(sorted);

  return {
    total: sorted.length,
    mean: μ,
    median: median(sorted),
    min: sorted[0],
    max: sorted[sorted.length - 1],
    variance: variance(sorted),
    std: σ,
    ci_68: [μ - σ, μ + σ],
    ci_95: [μ - 2 * σ, μ + 2 * σ],
    ci_997: [μ - 3 * σ, μ + 3 * σ],
  };
}

export default function ValueStatisticTable({
  values = [],
  title = "Statistics",
}) {
  if (!values || values.length === 0) return null;

  const stats = computeStats(values);

  const format = (v) => (typeof v === "number" ? v.toFixed(2) : v);

  return (
    <TableContainer component={Paper} sx={{ my: 2 }}>
      <Typography variant="h6" align="center" sx={{ pt: 2 }}>
        {title}
      </Typography>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Metric</TableCell>
            <TableCell align="right">Value</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          <TableRow>
            <TableCell>Total Tests</TableCell>
            <TableCell align="right">{stats.total}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell>Mean</TableCell>
            <TableCell align="right">{format(stats.mean)}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell>Median</TableCell>
            <TableCell align="right">{format(stats.median)}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell>Min</TableCell>
            <TableCell align="right">{format(stats.min)}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell>Max</TableCell>
            <TableCell align="right">{format(stats.max)}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell>Variance</TableCell>
            <TableCell align="right">{format(stats.variance)}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell>Standard Deviation</TableCell>
            <TableCell align="right">{format(stats.std)}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell>CI [68.2%]</TableCell>
            <TableCell align="right">{`${format(stats.ci_68[0])} to ${format(
              stats.ci_68[1]
            )}`}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell>CI [95%]</TableCell>
            <TableCell align="right">{`${format(stats.ci_95[0])} to ${format(
              stats.ci_95[1]
            )}`}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell>CI [99.7%]</TableCell>
            <TableCell align="right">{`${format(stats.ci_997[0])} to ${format(
              stats.ci_997[1]
            )}`}</TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </TableContainer>
  );
}
