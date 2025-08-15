import React, { useState, useMemo } from "react";
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
import { formatDateTime } from "../../../utils/DatetimeUtility";
import { tabConfig } from "./TabConfig";
import { columnConfig } from "./ColumnConfig";

export function DashboardTable({ data, isMobile, activeTab }) {
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(isMobile ? 5 : 10);

  const columnsToShow = useMemo(() => tabConfig[activeTab] || [], [activeTab]);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const exportCSV = () => {
    const headers = columnsToShow.map((key) => columnConfig[key]?.label || key);
    const rows = data.map((row) =>
      columnsToShow.map((key) =>
        columnConfig[key]?.render
          ? columnConfig[key].render(row)
          : row[key] ?? "-"
      )
    );

    const csvContent = [headers.join(",")]
      .concat(rows.map((r) => r.join(",")))
      .join("\n");

    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
    saveAs(blob, `${activeTab}_data.csv`);
  };

  const exportKML = () => {
    const headers = columnsToShow.map((key) => columnConfig[key]?.label || key);
    const rows = data.map((row) =>
      columnsToShow.map((key) =>
        columnConfig[key]?.render
          ? columnConfig[key].render(row)
          : row[key] ?? "-"
      )
    );

    const kmlHeader = `<?xml version="1.0" encoding="UTF-8"?>\n<kml xmlns="http://www.opengis.net/kml/2.2">\n<Document>`;
    const kmlFooter = `</Document>\n</kml>`;

    const placemarks = rows
      .filter((row) => {
        const location = row[1];

        if (!location || typeof location !== "string") return false;

        const [latStr, lngStr] = location.split(",").map((val) => val.trim());
        const lat = parseFloat(latStr);
        const lng = parseFloat(lngStr);

        const isInvalid = isNaN(lat) || isNaN(lng) || lat === -1 || lng === -1;

        return !isInvalid;
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
    saveAs(blob, `${activeTab}_data.kml`);
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
              {columnsToShow.map((key) => (
                <TableCell key={key} sx={columnConfig[key]?.style}>
                  {columnConfig[key]?.label || key}
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {paginatedData.map((row, i) => (
              <TableRow key={row.id || i}>
                {columnsToShow.map((key) => (
                  <TableCell key={key} sx={columnConfig[key]?.style}>
                    {columnConfig[key]?.render
                      ? columnConfig[key].render(row)
                      : row[key] ?? "-"}
                  </TableCell>
                ))}
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
