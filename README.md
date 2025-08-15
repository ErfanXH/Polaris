# Polaris

[🇮🇷 فارسی](README.fa.md)

**Polaris** is a comprehensive network measurement and monitoring platform with both **Android** and **Web** versions.  
It enables end users to measure network parameters, run functional tests, and view results in an intuitive interface, while providing administrators with powerful management and analytics tools.

---

## 📌 Features

### **Android Application**
- **Clean Architecture (MVVM)** for modular, maintainable code.
- **User Account Management**
  - Sign Up, Login, Email Verification.
  - Password Reset with multi-step authentication.
- **Network Measurements**
  - Download & Upload Throughput.
  - SMS Delivery & Latency.
  - Signal Quality, Cell Information, GPS Location.
- **Background Measurements** with precise location tracking.
- **Permission Management** for precise measurement.
- **Customizable Settings**
  - SIM selection.
  - Background sync intervals (15 mins to 24 hours).
  - Test configurations (Web Response, DNS, Ping).

### **Web Platform**
- **Modern, Responsive UI** for desktop and mobile browsers.
- **User Dashboard**
  - Real-time network monitoring.
  - Map-based data visualization.
- **Data Filtering & Export**
  - Filter by date range, test type, or network configuration.
  - Export data to CSV or KML.
- **Administrator Tools**
  - User management (ban/unban, search by phone or email).
  - Access to all test results and configurations.

---

## 🚀 Getting Started

### **Android App**
1. **Download & Install**
   - Visit: [http://45.149.77.43:5174/](http://45.149.77.43:5174/)
   - Install the APK on your (android 9.0+) device.
2. **Grant Permissions**
   - Location (Precise & Background).
   - SMS (Send, Read).
   - Calls, Notifications and Internet access.
   - Alarms and Reminders
   - Ignore Battery Optimisation
3. **Create an Account**
   - Sign up with your phone number and email.
   - Verify your email via 5-digit code.
4. **Start Measuring**
   - Select tests, run measurements, and view results.

### **Web Platform**
1. Open [http://45.149.77.43:5174/](http://45.149.77.43:5174/) in your browser.
2. Create an account or log in.
3. Access dashboards, view data on maps, and export results.
4. Admins can manage users and monitor network performance.

---

## 📂 Project Structure
- **`/android`** – Android client application.
- **`/frontend`** – Web frontend.
- **`/backend`** – Web backend.
- **`/manual`** – User manual.

---

## 🔒 Permissions Required (Android)
- Precise & background location.
- SMS read/send.
- Call management.
- Internet access.
- Notifications.
- Battery optimization bypass (for uninterrupted background tasks).
- Alarm and Reminder

---

## 📊 Example Test Types
- **Throughput Tests** – Upload/Download speed.
- **Functional Tests** – SMS Delivery, Ping, DNS Lookup.
- **Signal Quality** – RSRP, RSRQ, SINR values.
- **Cell Information** – Cell ID, Cell Technology, MCC, MNC, Frequency and Frequency Band.

---

## 🤝 Contributing
We welcome contributions!  
Fork the repo, make your changes, and open a pull request.

---

## 📜 License
This project is licensed under the MIT License.

---

## 📧 Contact
- Project Team:  
  - Mehran Razaghi (Frontend and Android Developer)
  - Erfan Hemati (Android and Frontend Developer)
  - Hamed Sadat (Backend and Frontend Developer)
