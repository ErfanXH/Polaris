import {
  Box,
  Container,
  Typography,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from "@mui/material";
import { ExpandMore } from "@mui/icons-material";
import FaqList from "./FaqsList";

const Faq = ({ theme, isDark }) => {
  return (
    <Box id="faq" sx={{ py: 8 }}>
      <Container maxWidth="md">
        <Typography
          variant="h3"
          component="h2"
          sx={{
            textAlign: "center",
            fontWeight: 700,
            mb: 6,
            color: theme.palette.text.primary,
          }}
        >
          Frequently Asked Questions
        </Typography>

        <Box
          sx={{
            bgcolor: theme.palette.custom.cardBg,
            borderRadius: 2,
            overflow: "hidden",
            border: `1px solid ${theme.palette.divider}`,
          }}
        >
          {FaqList.map((faq, index) => (
            <Accordion
              key={index}
              elevation={0}
              sx={{
                bgcolor: "transparent",
                "&:before": {
                  display: "none",
                },
                "&:not(:last-child)": {
                  borderBottom: `1px solid ${theme.palette.divider}`,
                },
              }}
            >
              <AccordionSummary
                expandIcon={<ExpandMore />}
                sx={{
                  "&:hover": {
                    bgcolor: isDark
                      ? "rgba(255,255,255,0.05)"
                      : "rgba(0,0,0,0.05)",
                  },
                }}
              >
                <Typography variant="h6" sx={{ fontWeight: 500 }}>
                  {faq.question}
                </Typography>
              </AccordionSummary>
              <AccordionDetails>
                <Typography>{faq.answer}</Typography>
              </AccordionDetails>
            </Accordion>
          ))}
        </Box>
      </Container>
    </Box>
  );
};

export default Faq;
