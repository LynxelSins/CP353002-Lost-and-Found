import client from "./client";

// ยื่นขอรับของ/แจ้งเป็นเจ้าของสิ่งของในรายการนั้น
// backend: POST /api/reports/{reportId}/claims (SubmitClaimRequest: evidenceText, evidenceImageUrl)
export async function createClaim(reportId, payload = {}) {
    const { data } = await client.post(`/api/reports/${reportId}/claims`, payload);
    return data?.data;
}

// ดึงรายการคำขอรับของทั้งหมดของ report หนึ่ง ๆ (เฉพาะเจ้าของโพสต์เท่านั้นที่เรียกได้ -> backend คืน 403 ถ้าไม่ใช่เจ้าของ)
// backend: GET /api/reports/{reportId}/claims
export async function getClaimsForReport(reportId) {
    const { data } = await client.get(`/api/reports/${reportId}/claims`);
    return data?.data || [];
}

// เจ้าของโพสต์อนุมัติคำขอรับของ (ไม่บังคับใส่จุด/เวลานัดรับของ)
// backend: PATCH /api/claims/{claimId}/approve
export async function approveClaim(claimId, payload) {
    const { data } = await client.patch(`/api/claims/${claimId}/approve`, payload || {});
    return data?.data;
}

// เจ้าของโพสต์ปฏิเสธคำขอรับของ
// backend: PATCH /api/claims/{claimId}/reject
export async function rejectClaim(claimId) {
    const { data } = await client.patch(`/api/claims/${claimId}/reject`);
    return data?.data;
}

export async function getMyClaims(params = {}) {
    const { data } = await client.get("/api/claims/mine", { params });
    return data?.data;
}
