import client from "./client";

export async function getReports(params = {}) {
    const { data } = await client.get("/api/reports", { params });
    return data.data; // Page<ReportSummaryResponse> -> { content, totalPages, ... }
}

export async function getReportById(id) {
    const { data } = await client.get(`/api/reports/${id}`);
    return data.data;
}

export async function createReport(payload) {
    const { data } = await client.post("/api/reports", payload);
    return data.data;
}

export async function watchReport(id) {
    await client.post(`/api/reports/${id}/watch`);
}

export async function unwatchReport(id) {
    await client.delete(`/api/reports/${id}/watch`);
}

export async function closeReport(id) {
    await client.post(`/api/reports/${id}/close`);
}

export async function getMyReports(params = {}) {
    const { data } = await client.get("/api/reports/mine", { params });
    return data.data;
}

export async function getWatchedReports(params = {}) {
    const { data } = await client.get("/api/reports/watched", { params });
    return data.data;
}
