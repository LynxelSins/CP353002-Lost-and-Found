import { useState } from "react";
import "./RegisterForm.css";
import { useNavigate } from "react-router-dom";
import { register } from "../api/authApi.js";

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

function RegisterForm({ onLogin, onGoogleLogin }) {
    const navigate = useNavigate();
    const [submitting, setSubmitting] = useState(false);

    async function handleRegister(event) {
        event.preventDefault();

        const firstName = document.getElementById("first-name").value.trim();
        const lastName = document.getElementById("last-name").value.trim();
        const email = document.getElementById("register-email").value.trim();
        const password = document.getElementById("register-password").value;
        const confirmPassword =
            document.getElementById("confirm-password").value;

        if (password.length < 8) {
            alert("รหัสผ่านต้องมีอย่างน้อย 8 ตัวอักษร");
            return;
        }
        if (password !== confirmPassword) {
            alert("รหัสผ่านและยืนยันรหัสผ่านไม่ตรงกัน");
            return;
        }

        setSubmitting(true);
        try {
            await register(`${firstName} ${lastName}`.trim(), email, password);
            navigate("/items");
        } catch (error) {
            const message =
                error.response?.data?.message ||
                "สมัครสมาชิกไม่สำเร็จ กรุณาลองใหม่";
            alert(message);
        } finally {
            setSubmitting(false);
        }
    }
    return (
        <div className="register-form">
            <div className="register-header">
                <h2>สร้างบัญชีใหม่</h2>
                <p>กรอกข้อมูลเพื่อเริ่มใช้งาน Lost &amp; Found</p>
            </div>

            <form onSubmit={handleRegister}>
                <div className="name-row">
                    <div className="register-field">
                        <label htmlFor="first-name">ชื่อ</label>

                        <input
                            id="first-name"
                            type="text"
                            placeholder="ชื่อจริง"
                            required
                        />
                    </div>

                    <div className="register-field">
                        <label htmlFor="last-name">นามสกุล</label>

                        <input
                            id="last-name"
                            type="text"
                            placeholder="นามสกุล"
                            required
                        />
                    </div>
                </div>

                <div className="register-field">
                    <label htmlFor="register-email">อีเมล</label>

                    <div className="register-input-with-icon">
                        <SearchIcon />

                        <input
                            id="register-email"
                            type="email"
                            placeholder="example@email.com"
                            required
                        />
                    </div>
                </div>

                <div className="register-field">
                    <label htmlFor="phone">เบอร์โทรศัพท์</label>

                    <input
                        id="phone"
                        type="tel"
                        placeholder="+66 8X-XXX-XXXX"
                        required
                    />
                </div>

                <div className="register-field">
                    <label htmlFor="register-password">รหัสผ่าน</label>

                    <input
                        id="register-password"
                        type="password"
                        placeholder="••••••••••••"
                        required
                    />

                    <small>รหัสผ่านต้องมีอย่างน้อย 8 ตัวอักษร</small>
                </div>

                <div className="register-field">
                    <label htmlFor="confirm-password">ยืนยันรหัสผ่าน</label>

                    <input
                        id="confirm-password"
                        type="password"
                        placeholder="••••••••••••"
                        required
                    />
                </div>

                <button className="register-submit" type="submit">
                    สมัครสมาชิก
                </button>
            </form>

            <div className="register-divider">
                <span></span>
                <p>หรือ</p>
                <span></span>
            </div>

            <div className="register-social">
                <button
                    type="button"
                    className="google-btn"
                    onClick={onGoogleLogin}
                >
                    <img src="/google.svg" />
                    Sign up with Google
                </button>

                <button type="button">
                    <span className="facebook-f">f</span>
                    Facebook
                </button>
            </div>

            <div className="register-switch">
                <span>มีบัญชีอยู่แล้ว?</span>

                <button type="button" onClick={onLogin}>
                    เข้าสู่ระบบ
                </button>
            </div>
        </div>
    );
}

export default RegisterForm;
