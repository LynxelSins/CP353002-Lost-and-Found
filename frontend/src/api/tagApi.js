import client from "./client";

export async function getTags() {
    const { data } = await client.get("/api/tags");
    return data.data || [];
}
