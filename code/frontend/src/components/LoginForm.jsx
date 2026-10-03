import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./LoginForm.css";
import { login } from "../api/authApi.js";

function SearchIcon() {
    return (
        <svg
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
        >
            <circle cx="11" cy="11" r="7" />
            <path d="m20 20-4-4" />
        </svg>
    );
}

function LockIcon() {
    return (
        <svg
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
        >
            <rect x="5" y="10" width="14" height="10" rx="2" />
            <path d="M8 10V7a4 4 0 0 1 8 0v3" />
        </svg>
    );
}

function EyeIcon() {
    return (
        <svg
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
        >
            <path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6S2 12 2 12Z" />
            <circle cx="12" cy="12" r="2.5" />
        </svg>
    );
}

function EyeOffIcon() {
    return (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" strokeWidth="2"
            strokeLinecap="round" strokeLinejoin="round">
            <path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6S2 12 2 12Z" />
            <circle cx="12" cy="12" r="2.5" />
            <path d="M4 4l16 16" />
        </svg>
    );
}

function GlobeIcon() {
    return (
        <svg
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
        >
            <circle cx="12" cy="12" r="9" />
            <path d="M3 12h18M12 3c2.5 2.5 3.5 5.5 3.5 9s-1 6.5-3.5 9c-2.5-2.5-3.5-5.5-3.5-9S9.5 5.5 12 3Z" />
        </svg>
    );
}

function ShareIcon() {
    return (
        <svg
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
        >
            <circle cx="18" cy="5" r="2.5" />
            <circle cx="6" cy="12" r="2.5" />
            <circle cx="18" cy="19" r="2.5" />
            <path d="m8.2 10.8 7.6-4.5M8.2 13.2l7.6 4.5" />
        </svg>
    );
}

function LoginForm({ onRegister, onGoogleLogin }) {
    const navigate = useNavigate();

    const [submitting, setSubmitting] = useState(false);

    const [showPassword, setShowPassword] = useState(false);

    async function handleLogin(event) {
        event.preventDefault();

        const formData = new FormData(event.currentTarget);
        const email = formData.get("email");
        const password = formData.get("password");

        setSubmitting(true);
        try {
            await login(email, password);
            navigate("/items");
        } catch (error) {
            const message =
                error.response?.data?.message || "อีเมลหรือรหัสผ่านไม่ถูกต้อง";
            alert(message);
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <div className="login-form">
            <div className="login-header">
                <h2>ยินดีต้อนรับ!</h2>
                <p>เข้าสู่ระบบเพื่อใช้งาน Lost &amp; Found</p>
            </div>

            <form onSubmit={handleLogin}>
                <div className="form-field">
                    <label htmlFor="login-email">อีเมลหรือชื่อผู้ใช้</label>

                    <div className="input-wrapper">
                        <SearchIcon />

                        <input
                            id="login-email"
                            name="email"
                            type="email"
                            placeholder="กรอกอีเมล"
                            required
                        />
                    </div>
                </div>

                <div className="form-field">
                    <div className="password-label">
                        <label htmlFor="login-password">รหัสผ่าน</label>

                        <button type="button" className="forgot-password">
                            ลืมรหัสผ่าน?
                        </button>
                    </div>

                    <div className="input-wrapper">
                        <LockIcon />

                        <input
                            id="login-password"
                            name="password"
                            type={showPassword ? "text" : "password"}
                            placeholder="กรอกรหัสผ่าน"
                            required
                        />

                        <button
                            type="button"
                            className="password-toggle"
                            aria-label={showPassword ? "ซ่อนรหัสผ่าน" : "แสดงรหัสผ่าน"}
                            onClick={() => setShowPassword((prev) => !prev)}
                        >
                            {showPassword ? <EyeOffIcon /> : <EyeIcon />}
                        </button>
                    </div>
                </div>

                <label className="remember-me">
                    <input type="checkbox" />
                    <span>จดจำฉันไว้</span>
                </label>

                <button
                    className="main-submit-button"
                    type="submit"
                    disabled={submitting}
                >
                    {submitting ? "กำลังเข้าสู่ระบบ..." : "เข้าสู่ระบบ"}
                </button>
            </form>

            <div className="or-divider">
                <span></span>
                <p>หรือ</p>
                <span></span>
            </div>

            <div className="social-buttons">
                <button
                    type="button"
                    className="google-btn"
                    onClick={onGoogleLogin}
                >
                    <img src="/google.svg" />
                    Continue with Google
                </button>
            </div>

            <div className="switch-auth">
                <span>ยังไม่มีบัญชี?</span>

                <button type="button" onClick={onRegister}>
                    สมัครสมาชิกเลย
                </button>
            </div>
        </div>
        //
    );
}

export default LoginForm;
