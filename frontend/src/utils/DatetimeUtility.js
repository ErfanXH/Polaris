export const formatDateTime = (timestamp) => {
  if (!timestamp) return "-";
  const date = new Date(timestamp);

  const pad = (n) => n.toString().padStart(2, "0");

  const year = date.getUTCFullYear();
  const month = pad(date.getUTCMonth() + 1);
  const day = pad(date.getUTCDate());
  const hours = pad(date.getUTCHours());
  const minutes = pad(date.getUTCMinutes());
  const seconds = pad(date.getUTCSeconds());

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

export const LocalizeDateTime = (timestamp) => {
  const localDate = new Date(timestamp);
  localDate.setMinutes(localDate.getMinutes() - 210);
  return localDate;
};
