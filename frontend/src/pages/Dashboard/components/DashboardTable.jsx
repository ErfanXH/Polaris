import React, { useState } from "react";
import {
  Box,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  TablePagination,
  Stack,
} from "@mui/material";
import { saveAs } from "file-saver";
import { formatDateTime } from "../../../utils/FormatDatetime";

export function DashboardTable({ data, isMobile }) {
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(isMobile ? 5 : 10);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const exportCSV = () => {
    const headers = [
      "Timestamp",
      "Location",
      "Network",
      "Frequency Band",
      "ARFCN",
      "PLMN Id",
      "TAC",
      "LAC",
      "RAC",
      "Cell Id",
      "Frequency",
      "RSRP",
      "RSRQ",
      "RSCP",
      "EC/N0",
      "RxLev",
      "Download",
      "Upload",
      "Ping",
      "DNS",
      "Web",
      "SMS",
    ];
    const rows = data.map((row) => [
      row.timestamp,
      `${row.latitude?.toFixed(4)}, ${row.longitude?.toFixed(4)}`,
      row.network_type,
      row.frequency_band,
      row.arfcn,
      row.plmn_id,
      row.tac,
      row.lac,
      row.rac,
      row.cell_id,
      row.frequency,
      row.rsrp,
      row.rsrq,
      row.rscp,
      row.ecIo,
      row.rxLev,
      row.http_download,
      row.http_upload,
      row.ping_time,
      row.dns_response,
      row.web_response,
      row.sms_delivery_time,
    ]);

    const csvContent = [headers.join(",")]
      .concat(rows.map((r) => r.join(",")))
      .join("\n");

    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
    saveAs(blob, "measurements.csv");
  };

  const exportKML = () => {
    const headers = [
      "Timestamp",
      "Location",
      "Network",
      "Frequency Band",
      "ARFCN",
      "PLMN Id",
      "TAC",
      "LAC",
      "RAC",
      "Cell Id",
      "Frequency",
      "RSRP",
      "RSRQ",
      "RSCP",
      "EC/N0",
      "RxLev",
      "Download",
      "Upload",
      "Ping",
      "DNS",
      "Web",
      "SMS",
    ];

    const rows = data.map((row) => [
      row.timestamp,
      `${row.latitude?.toFixed(4)}, ${row.longitude?.toFixed(4)}`,
      row.network_type,
      row.frequency_band,
      row.arfcn,
      row.plmn_id,
      row.tac,
      row.lac,
      row.rac,
      row.cell_id,
      row.frequency,
      row.rsrp,
      row.rsrq,
      row.rscp,
      row.ecIo,
      row.rxLev,
      row.http_download,
      row.http_upload,
      row.ping_time,
      row.dns_response,
      row.web_response,
      row.sms_delivery_time,
    ]);

    const kmlHeader = `<?xml version="1.0" encoding="UTF-8"?>\n<kml xmlns="http://www.opengis.net/kml/2.2">\n<Document>`;
    const kmlFooter = `</Document>\n</kml>`;

    const placemarks = rows
      .filter((row) => {
        const location = row[1];
        return location && location !== "undefined, undefined";
      })
      .map((row) => {
        const coord = row[1].split(", ");
        const description = headers
          .map((header, i) => `<b>${header}</b>: ${row[i] ?? "-"}<br/>`)
          .join("");
        return `  <Placemark>
    <name>${row[2]}</name>
    <description><![CDATA[${description}]]></description>
    <Point>
      <coordinates>${coord[1]},${coord[0]},0</coordinates>
    </Point>
  </Placemark>`;
      })
      .join("\n");

    const kmlContent = `${kmlHeader}\n${placemarks}\n${kmlFooter}`;
    const blob = new Blob([kmlContent], {
      type: "application/vnd.google-earth.kml+xml",
    });
    saveAs(blob, "measurements.kml");
  };

  const paginatedData = data.slice(
    page * rowsPerPage,
    page * rowsPerPage + rowsPerPage
  );

  return (
    <Box sx={{ mt: 4, overflowX: "auto" }}>
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          mb: 1,
        }}
      >
        <Typography variant="h6">Measurement Data</Typography>
        <Stack
          direction={isMobile ? "column" : "row"}
          gap={isMobile ? "0.1rem" : "0.5rem"}
        >
          <Button variant="outlined" size="small" onClick={exportCSV}>
            Export CSV
          </Button>
          <Button variant="outlined" size="small" onClick={exportKML}>
            Export KML
          </Button>
        </Stack>
      </Box>

      <TableContainer component={Paper}>
        <Table size={isMobile ? "small" : "medium"} stickyHeader>
          <TableHead>
            <TableRow>
              <TableCell>Timestamp</TableCell>
              <TableCell>Location</TableCell>
              <TableCell>Network</TableCell>
              <TableCell>Frequency Band</TableCell>
              <TableCell>ARFCN</TableCell>
              <TableCell>PLMN Id</TableCell>
              <TableCell>TAC</TableCell>
              <TableCell>LAC</TableCell>
              <TableCell>RAC</TableCell>
              <TableCell>Cell Id</TableCell>
              <TableCell>Frequency(MHz)</TableCell>
              <TableCell>RSRP</TableCell>
              <TableCell>RSRQ</TableCell>
              <TableCell>RSCP</TableCell>
              <TableCell>EC/N0</TableCell>
              <TableCell>RxLev</TableCell>
              <TableCell>Download(Mbps)</TableCell>
              <TableCell>Upload(Mbps)</TableCell>
              <TableCell>Ping(ms)</TableCell>
              <TableCell>DNS(ms)</TableCell>
              <TableCell>Web(ms)</TableCell>
              <TableCell>SMS(ms)</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {paginatedData.map((row) => (
              <TableRow key={row.id}>
                <TableCell>{formatDateTime(row.timestamp)}</TableCell>
                <TableCell>
                  {row.latitude?.toFixed(4)}, {row.longitude?.toFixed(4)}
                </TableCell>
                <TableCell>{row.network_type}</TableCell>
                <TableCell>{row.frequency_band || "-"}</TableCell>
                <TableCell>{row.arfcn || "-"}</TableCell>
                <TableCell>{row.plmn_id || "-"}</TableCell>
                <TableCell>{row.tac || "-"}</TableCell>
                <TableCell>{row.lac || "-"}</TableCell>
                <TableCell>{row.rac || "-"}</TableCell>
                <TableCell>{row.cell_id || "-"}</TableCell>
                <TableCell>{row.frequency?.toFixed(3) || "-"}</TableCell>
                <TableCell>{row.rsrp || "-"}</TableCell>
                <TableCell>{row.rsrq || "-"}</TableCell>
                <TableCell>{row.rscp || "-"}</TableCell>
                <TableCell>{row.ecIo || "-"}</TableCell>
                <TableCell>{row.rxLev || "-"}</TableCell>
                <TableCell>{row.http_download?.toFixed(3) || "-"}</TableCell>
                <TableCell>{row.http_upload?.toFixed(3) || "-"}</TableCell>
                <TableCell>{row.ping_time?.toFixed(3) || "-"}</TableCell>
                <TableCell>{row.dns_response?.toFixed(3) || "-"}</TableCell>
                <TableCell>{row.web_response?.toFixed(3) || "-"}</TableCell>
                <TableCell>
                  {row.sms_delivery_time?.toFixed(3) || "-"}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <TablePagination
        component="div"
        count={data.length}
        page={page}
        onPageChange={handleChangePage}
        rowsPerPage={rowsPerPage}
        onRowsPerPageChange={handleChangeRowsPerPage}
        rowsPerPageOptions={[5, 10, 20, 50]}
      />
    </Box>
  );
}
