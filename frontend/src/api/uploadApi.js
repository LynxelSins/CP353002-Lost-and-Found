import client from "./client";

export async function uploadImage(file) {
    const formData = new FormData();
    formData.append("file", file);

    const { data } = await client.post("/api/uploads", formData, {
        headers: { "Content-Type": "multipart/form-data" },
    });

    return data.data.url; // backend คืน URL เต็มของรูปที่เก็บใน Neon (/api/files/{id}) ไม่ต้องต่อ baseURL เอง
}