import client from "./client";

export async function uploadImage(file) {
    const formData = new FormData();
    formData.append("file", file);

    const { data } = await client.post("/api/uploads", formData, {
        headers: { "Content-Type": "multipart/form-data" },
    });

    return `${client.defaults.baseURL}${data.data.url}`;
}