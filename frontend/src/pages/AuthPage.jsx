import { useState, useRef } from "react";
import LoginForm from "../components/LoginForm.jsx";
import RegisterForm from "../components/RegisterForm.jsx";
import "./AuthPage.css";
import { useNavigate } from "react-router-dom";

import { signInWithPopup } from "firebase/auth";
import { auth, googleProvider } from "../firebase";
import { loginWithGoogle } from "../api/authApi.js";
import { Search4 } from 'reicon-react';

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

// ไอคอนของหายจางๆ เป็นพื้นหลัง (วางซ้อน 2 ชั้น: ชั้นจาง + ชั้นที่ถูก "ส่อง" ด้วยเมาส์)
const LOST_ITEMS = [
    { d: ["M11 12 20 3", "M17 6l3 3", "M15 8l2 2"], c: [8, 15, 4], x: 55, y: 60, r: -20 }, // key
    { d: ["M3 10h18"], rect: [3, 6, 18, 13, 2], c: [16, 14.5, 1], x: 310, y: 50, r: 14 }, // wallet
    { d: ["M3 12a9 9 0 0 1 18 0Z", "M12 12v6a2 2 0 0 0 4 0"], x: 335, y: 200, r: 18 }, // umbrella
    { d: ["M11 18h2"], rect: [7, 2, 10, 20, 2], x: 25, y: 190, r: -14 }, // phone
    { d: ["M6 20V9a6 6 0 0 1 12 0v11a1 1 0 0 1-1 1H7a1 1 0 0 1-1-1Z", "M9 14h6", "M10 4h4"], x: 330, y: 340, r: -10 }, // backpack
    { d: ["M10 14h4", "M3 14l1.5-6", "M21 14l-1.5-6"], c: [6.5, 14, 3.5], c2: [17.5, 14, 3.5], x: 30, y: 340, r: 10 }, // glasses
    { d: ["M4 14v-2a8 8 0 0 1 16 0v2"], rect: [3, 14, 4, 6, 1], rect2: [17, 14, 4, 6, 1], x: 70, y: 470, r: -8 }, // headphones
    { d: ["M11 12 20 3", "M17 6l3 3", "M15 8l2 2"], c: [8, 15, 4], x: 300, y: 460, r: 25 }, // key
    { d: ["M3 10h18"], rect: [3, 6, 18, 13, 2], c: [16, 14.5, 1], x: 185, y: 505, r: -6 }, // wallet
    { d: ["M10 14h4", "M3 14l1.5-6", "M21 14l-1.5-6"], c: [6.5, 14, 3.5], c2: [17.5, 14, 3.5], x: 190, y: 28, r: 6 }, // glasses
];

function ItemsBackdrop({ lit = false }) {
    return (
        <svg
            className={`brand-items ${lit ? "lit" : ""}`}
            viewBox="0 0 400 533"
            preserveAspectRatio="xMidYMid slice"
            aria-hidden="true"
        >
            {LOST_ITEMS.map((it, i) => (
                <g
                    key={i}
                    transform={`translate(${it.x} ${it.y}) rotate(${it.r}) scale(2.4) translate(-12 -12)`}
                >
                    {it.d.map((path, j) => (
                        <path key={j} d={path} />
                    ))}
                    {it.rect && (
                        <rect x={it.rect[0]} y={it.rect[1]} width={it.rect[2]} height={it.rect[3]} rx={it.rect[4]} />
                    )}
                    {it.rect2 && (
                        <rect x={it.rect2[0]} y={it.rect2[1]} width={it.rect2[2]} height={it.rect2[3]} rx={it.rect2[4]} />
                    )}
                    {it.c && <circle cx={it.c[0]} cy={it.c[1]} r={it.c[2]} />}
                    {it.c2 && <circle cx={it.c2[0]} cy={it.c2[1]} r={it.c2[2]} />}
                </g>
            ))}
        </svg>
    );
}

function BrandVisual({ isLogin }) {
    return (
        <div className="brand-content">
            <div className="brand-logo">
                <div className="brand-logo-icon">
                    <Search4 size={24} weight="Filled" />
                </div>

                <div>
                    <h1>Lost &amp; Found</h1>
                    <p>แพลตฟอร์มแจ้งของหายและของพบ</p>
                </div>
            </div>

            <div className="brand-heading" key={isLogin ? "login" : "register"}>
                {isLogin ? (
                    <>
                        <h2>ค้นหาของที่หาย</h2>
                        <p>
                            รายงานและค้นหาสิ่งของที่สูญหายในมหาวิทยาลัยของคุณได้ง่ายๆ
                        </p>
                    </>
                ) : (
                    <>
                        <h2>ยินดีต้อนรับสู่ Lost &amp; Found</h2>
                        <p>
                            แพลตฟอร์มช่วยให้การแจ้งของหายและของที่พบให้เป็นเรื่องง่าย
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

            await loginWithGoogle(idToken);

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

    // แสงนุ่มๆ ตามเมาส์บนแผง brand (ไม่ต้อง re-render)
    const handleBrandMove = (e) => {
        const rect = e.currentTarget.getBoundingClientRect();
        e.currentTarget.style.setProperty("--mx", `${e.clientX - rect.left}px`);
        e.currentTarget.style.setProperty("--my", `${e.clientY - rect.top}px`);
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
            <section className="auth-brand" onMouseMove={handleBrandMove}>
                <ItemsBackdrop />
                <ItemsBackdrop lit />
                <BrandVisual isLogin={isLogin} />
            </section>
        </div>
    );
}

export default AuthPage;