import client from "./client";

// ดึงรายการหมวดหมู่ทั้งหมด (เช่น โทรศัพท์, กุญแจ, บัตรประชาชน...) -> GET /api/categories
export async function getCategories() {
    const { data } = await client.get("/api/categories");
    return data?.data || [];
}

// ดึงหมวดหมู่ตาม id -> GET /api/categories/:id
export async function getCategoryById(id) {
    const { data } = await client.get(`/api/categories/${id}`);
    return data?.data;
}
