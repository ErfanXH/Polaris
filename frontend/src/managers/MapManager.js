import { api } from "./ApiManager";

const MapManager = {
  /**
   * Get all measurements for the authenticated user
   * @returns {Promise<Array>} Array of measurement objects
   */
  getMeasurements: async () => {
    try {
      const response = await api.get("/mobile/measurement/");
      return response.data;
    } catch (error) {
      console.log(error);
      debugger;
      console.log(error);
      throw new Error(
        error.response?.data?.message ||
          error.response?.data?.detail ||
          "Failed to fetch measurements"
      );
    }
  },

  /**
   * Get measurements filtered by device ID
   * @param {string} deviceId - Device ID to filter by
   * @returns {Promise<Array>} Array of measurement objects
   */
  getMeasurementsByDevice: async (deviceId) => {
    try {
      const response = await api.get(`/mobile/device/${deviceId}/measurement/`);
      return response.data;
    } catch (error) {
      throw new Error(
        error.response?.data?.message ||
          error.response?.data?.detail ||
          "Failed to fetch device measurements"
      );
    }
  },

  /**
   * Delete a measurement by ID
   * @param {string} measurementId - ID of measurement to delete
   * @returns {Promise<Object>} Deletion result
   */
  deleteMeasurement: async (measurementId) => {
    try {
      const response = await api.delete(
        `/mobile/measurement/${measurementId}/`
      );
      return response.data;
    } catch (error) {
      throw new Error(
        error.response?.data?.message ||
          error.response?.data?.detail ||
          "Failed to delete measurement"
      );
    }
  },
};

export default MapManager;
