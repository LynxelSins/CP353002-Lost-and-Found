import client from "./client";

export async function login(email, password) {
    const { data } = await client.post("/api/auth/login", { email, password });
    localStorage.setItem("token", data.data.accessToken); // <-- ชื่อ field จริงคือ accessToken ไม่ใช่ token
    return data.data.user;
}

export async function register(fullName, email, password) {
    const { data } = await client.post("/api/auth/register", {
        fullName,
        email,
        password,
    });
    localStorage.setItem("token", data.data.accessToken);
    return data.data.user;
}

export async function loginWithGoogle(idToken) {
    const { data } = await client.post("/api/auth/google", { idToken });
    localStorage.setItem("token", data.data.accessToken);
    return data.data.user;
}

export async function getMe() {
    const { data } = await client.get("/api/users/me");
    return data.data;
}

// แก้ไขโปรไฟล์ (ชื่อ/เบอร์โทร/รูปโปรไฟล์) — ส่งเฉพาะ field ที่ต้องการแก้ก็ได้ (partial update)
export async function updateProfile(payload) {
    const { data } = await client.patch("/api/users/me", payload);
    return data.data;
}

export async function changePassword(currentPassword, newPassword) {
    const { data } = await client.patch("/api/users/me/password", {
        currentPassword,
        newPassword,
    });
    return data;
}

export function logout() {
    localStorage.removeItem("token");
}
