import client from "./client";

export async function getNotifications(params = {}) {
    const { data } = await client.get("/api/notifications", { params });
    return data?.data;
}

export async function getUnreadCount() {
    const { data } = await client.get("/api/notifications/unread-count");
    return data?.data?.count ?? 0;
}

export async function markNotificationRead(id) {
    await client.patch(`/api/notifications/${id}/read`);
}

export async function markAllNotificationsRead() {
    await client.patch("/api/notifications/read-all");
}