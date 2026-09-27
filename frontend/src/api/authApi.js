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

export function logout() {
    localStorage.removeItem("token");
}