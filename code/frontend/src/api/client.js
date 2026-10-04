import axios from "axios";

// เปลี่ยนตรงนี้ที่เดียวตอน deploy จริง (หรือตั้งค่า .env: VITE_API_BASE_URL)
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const client = axios.create({
    baseURL: API_BASE_URL,
    headers: { "Content-Type": "application/json" },
});

// แนบ JWT ไปกับทุก request อัตโนมัติ
client.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// token หมดอายุ/ไม่ถูกต้อง (401) -> เด้งกลับหน้า login อัตโนมัติ
client.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem("token");
            if (window.location.pathname !== "/login") {
                window.location.href = "/login";
            }
        }
        return Promise.reject(error);
    },
);


export default client;