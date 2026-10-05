import client from "./client";

export async function getReports(params = {}) {
    const { data } = await client.get("/api/v1/reports", { params });
    return data.data; // Page<ReportSummaryResponse> -> { content, totalPages, ... }
}

export async function getReportById(id) {
    const { data } = await client.get(`/api/v1/reports/${id}`);
    return data.data;
}

export async function createReport(payload) {
    const { data } = await client.post("/api/v1/reports", payload);
    return data.data;
}

export async function watchReport(id) {
    await client.post(`/api/v1/reports/${id}/watch`);
}

export async function unwatchReport(id) {
    await client.delete(`/api/v1/reports/${id}/watch`);
}

export async function closeReport(id) {
    await client.post(`/api/v1/reports/${id}/close`);
}

export async function getMyReports(params = {}) {
    const { data } = await client.get("/api/v1/reports/mine", { params });
    return data.data;
}

export async function getWatchedReports(params = {}) {
    const { data } = await client.get("/api/v1/reports/watched", { params });
    return data.data;
}

export async function adminDeleteReport(id) {
    await client.delete(`/api/v1/admin/reports/${id}`);
}
