import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { logout } from "../api/authApi.js";
import { auth } from "../firebase.js";
import { signOut } from "firebase/auth";
import NotificationBell from "./NotificationBell.jsx";
import { ChevronDown } from "reicon-react";
import "./UserHeaderBar.css";

function UserHeaderBar({ userName = "ผู้ใช้งาน", avatarUrl = "" }) {
    const navigate = useNavigate();
    const [open, setOpen] = useState(false);
    const menuRef = useRef(null);

    useEffect(() => {
        function handleOutsideClick(event) {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setOpen(false);
            }
        }

        function handleEscape(event) {
            if (event.key === "Escape") {
                setOpen(false);
            }
        }

        document.addEventListener("mousedown", handleOutsideClick);
        document.addEventListener("keydown", handleEscape);
        return () => {
            document.removeEventListener("mousedown", handleOutsideClick);
            document.removeEventListener("keydown", handleEscape);
        };
    }, []);

    function goToProfile() {
        setOpen(false);
        navigate("/my-profile");
    }

    async function handleLogout() {
        setOpen(false);
        logout();
        try {
            await signOut(auth);
        } catch {
            // Firebase session may already be unavailable; local JWT is already cleared.
        }
        navigate("/login", { replace: true });
    }

    return (
        <div className="user-header-bar">
            <NotificationBell />

            <div className="user-header-menu" ref={menuRef}>
                <button
                    className="user-header-trigger"
                    type="button"
                    onClick={() => setOpen((current) => !current)}
                    aria-label="เปิดเมนูผู้ใช้"
                    aria-expanded={open}
                >
                    <span className="user-header-avatar">
                        {avatarUrl ? <img src={avatarUrl} alt="" /> : "👤"}
                    </span>
                    <span className="user-header-info">
                        <span>สวัสดี</span>
                        <strong>{userName}</strong>
                    </span>
                    <span
                        className={`user-header-chevron ${open ? "open" : ""}`}
                    >
                        <ChevronDown size={24} weight="Filled" />
                    </span>
                </button>

                <div className={`user-dropdown ${open ? "open" : ""}`}>
                    <button type="button" onClick={goToProfile}>
                        <span>✎</span>
                        แก้ไขโปรไฟล์
                    </button>
                    <button
                        type="button"
                        className="user-dropdown-logout"
                        onClick={handleLogout}
                    >
                        <span>↪</span>
                        ออกจากระบบ
                    </button>
                </div>
            </div>
        </div>
    );
}

export default UserHeaderBar;
