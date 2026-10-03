import client from "./client";

export async function getTags() {
    const { data } = await client.get("/api/v1/tags");
    return data.data || [];
}
