import { useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Paper,
  Box,
  Switch,
  TablePagination,
  CircularProgress,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import { useUserList } from "../../hooks/useUserList";

export default function UserList() {
  const { users = [], isLoading, error, banUser, allowUser } = useUserList();
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const [rowsPerPage, setRowsPerPage] = useState(isMobile ? 5 : 10);

  const filteredUsers = users.filter(
    (user) =>
      user.username?.toLowerCase().includes(search.toLowerCase()) ||
      user.email?.toLowerCase().includes(search.toLowerCase()) ||
      user.phone_number?.includes(search)
  );

  const handleChangePage = (_, newPage) => setPage(newPage);
  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  if (isLoading) {
    return (
      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          minHeight: "200px", // You can adjust height
        }}
      >
        <CircularProgress />
      </Box>
    );
  }
  if (error) return <div>Error loading users</div>;

  return (
    <Paper sx={{ padding: 2 }}>
      <TextField
        label="Search"
        variant="outlined"
        fullWidth
        margin="normal"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />
      <TableContainer>
        <Table size={isMobile ? "small" : "medium"} stickyHeader>
          <TableHead>
            <TableRow>
              <TableCell>Email</TableCell>
              <TableCell>Phone Number</TableCell>
              <TableCell>Verified</TableCell>
              <TableCell>Banned</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredUsers
              .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
              .map((user) => (
                <TableRow key={user.id}>
                  <TableCell>{user.email}</TableCell>
                  <TableCell>{user.phone_number}</TableCell>
                  <TableCell>{user.is_verified ? "Yes" : "No"}</TableCell>
                  <TableCell>
                    <Switch
                      checked={user.is_banned}
                      onChange={() =>
                        user.is_banned
                          ? allowUser(user.email || user.phone_number)
                          : banUser(user.email || user.phone_number)
                      }
                      color="primary"
                    />
                  </TableCell>
                </TableRow>
              ))}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        rowsPerPageOptions={[5, 10, 20]}
        component="div"
        count={filteredUsers.length}
        rowsPerPage={rowsPerPage}
        page={page}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage}
      />
    </Paper>
  );
}
