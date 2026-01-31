import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api/v1",
});

api.interceptors.request.use((config) => {
  // Skip login
  if (!config.url.includes("/login")) {
    const token = localStorage.getItem("token");

    // ✅ ONLY attach header if token exists
    if (token && token !== "undefined" && token !== "null") {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

export default api;
