import { useState, useRef } from "react";
import LoginForm from "../components/LoginForm.jsx";
import RegisterForm from "../components/RegisterForm.jsx";
import "./AuthPage.css";
import { useNavigate } from "react-router-dom";

import { signInWithPopup } from "firebase/auth";
import { auth, googleProvider } from "../firebase";
import { loginWithGoogle } from "../api/authApi.js";

function SearchIcon({ size = 28 }) {
    return (
        <svg
            width={size}
            height={size}
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

function UserIcon() {
    return (
        <svg
            width="52"
            height="52"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.5"
            strokeLinecap="round"
            strokeLinejoin="round"
        >
            <circle cx="12" cy="8" r="3.5" />
            <path d="M5.5 20c0-3.6 2.9-6 6.5-6s6.5 2.4 6.5 6" />
        </svg>
    );
}

function LocationIcon() {
    return (
        <svg
            width="17"
            height="17"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
        >
            <path d="M20 10c0 5-8 11-8 11S4 15 4 10a8 8 0 1 1 16 0Z" />
            <circle cx="12" cy="10" r="2.5" />
        </svg>
    );
}

function BrandVisual({ isLogin }) {
    return (
        <div className="brand-content">
            <div className="brand-logo">
                <div className="brand-logo-icon">
                    <SearchIcon size={29} />
                </div>

                <div>
                    <h1>Lost &amp; Found</h1>
                    <p>แพลตฟอร์มแจ้งของหายและของพบ</p>
                </div>
            </div>

            <div className="brand-heading">
                {isLogin ? (
                    <>
                        <h2>ค้นหาของที่หาย</h2>
                        <p>
                            รายงานและค้นหาสิ่งของที่สูญหายในชุมชนของคุณได้ง่ายๆ
                        </p>
                    </>
                ) : (
                    <>
                        <h2>ยินดีต้อนรับสู่ Lost &amp; Found</h2>
                        <p>
                            แพลตฟอร์มช่วยให้การแจ้งของหายและของพบเป็นเรื่องง่าย
                            รวดเร็ว
                            <br />
                            และปลอดภัย
                        </p>
                    </>
                )}
            </div>

            <div className="lost-found-illustration">
                <div className="location-card">
                    <LocationIcon />

                    <div>
                        <strong>กระเป๋าสะพายสีดำ</strong>
                        <span>ลานจอดรถ อาคาร A</span>
                    </div>
                </div>

                <div className="item-card">
                    <SearchIcon size={16} />

                    <div>
                        <strong>iPhone 13 สีดำ</strong>
                        <span>โรงอาหาร ชั้น 2</span>
                    </div>
                </div>
            </div>

            {!isLogin && (
                <div className="brand-tags">
                    <span>● &nbsp;แจ้งของหาย</span>
                    <span>● &nbsp;ค้นหาของที่พบ</span>
                    <span>● &nbsp;ติดต่อเจ้าของ</span>
                </div>
            )}
        </div>
    );
}

function AuthPage() {
    const [isLogin, setIsLogin] = useState(true);
    const navigate = useNavigate();

    const googleLoginRunning = useRef(false);

    // Google Login
    const handleGoogleLogin = async () => {
        if (googleLoginRunning.current) return;
        googleLoginRunning.current = true;

        try {
            const result = await signInWithPopup(auth, googleProvider);
            const idToken = await result.user.getIdToken();

            await loginWithGoogle(idToken); // เก็บ token ให้อัตโนมัติใน authApi แล้ว

            navigate("/items");
        } catch (error) {
            console.error("Google Login Error:", error);
            const message =
                error.response?.data?.message ||
                error.message ||
                "Google Login ไม่สำเร็จ";
            alert("Google Login ไม่สำเร็จ\n\n" + message);
        } finally {
            googleLoginRunning.current = false;
        }
    };

    return (
        <div
            className={`auth-page ${isLogin ? "login-mode" : "register-mode"}`}
        >
            {/* Login */}
            <section className="auth-form-side login-side">
                <LoginForm
                    onRegister={() => setIsLogin(false)}
                    onGoogleLogin={handleGoogleLogin}
                />
            </section>

            {/* Register */}
            <section className="auth-form-side register-side">
                <RegisterForm
                    onLogin={() => setIsLogin(true)}
                    onGoogleLogin={handleGoogleLogin}
                />
            </section>

            {/* Brand */}
            <section className="auth-brand">
                <BrandVisual isLogin={isLogin} />
            </section>
        </div>
    );
}

export default AuthPage;
