import { useEffect, useRef, useState } from "react";
import "./NotificationBell.css";
import {
    getNotifications,
    getUnreadCount,
    markAllNotificationsRead,
    markNotificationRead,
} from "../api/notificationApi.js";

function NotificationBell() {
    const [open, setOpen] = useState(false);
    const [items, setItems] = useState([]);
    const [unread, setUnread] = useState(0);
    const boxRef = useRef(null);

    async function refreshUnread() {
        try {
            setUnread(await getUnreadCount());
        } catch {
            // ปล่อยผ่านถ้า poll พลาดบางรอบ ไม่ต้องรบกวนผู้ใช้
        }
    }

    async function loadList() {
        try {
            const result = await getNotifications({ size: 10 });
            setItems(result.content || []);
        } catch {
            setItems([]);
        }
    }

    useEffect(() => {
        refreshUnread();
        const timer = setInterval(refreshUnread, 30000); // poll ทุก 30 วิ
        return () => clearInterval(timer);
    }, []);

    useEffect(() => {
        function handleClickOutside(e) {
            if (boxRef.current && !boxRef.current.contains(e.target)) {
                setOpen(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    async function toggleOpen() {
        const next = !open;
        setOpen(next);
        if (next) await loadList();
    }

    async function handleItemClick(n) {
        if (n.read) return;
        try {
            await markNotificationRead(n.id);
            setItems((prev) => prev.map((it) => (it.id === n.id ? { ...it, read: true } : it)));
            refreshUnread();
        } catch {
            // ignore
        }
    }

    async function handleMarkAll() {
        try {
            await markAllNotificationsRead();
            setItems((prev) => prev.map((it) => ({ ...it, read: true })));
            setUnread(0);
        } catch {
            // ignore
        }
    }

    return (
        <div className="notification-bell" ref={boxRef}>
            <button className="notification-button" type="button" onClick={toggleOpen} aria-label="การแจ้งเตือน">
                ♧
                {unread > 0 && <span className="notification-badge">{unread > 9 ? "9+" : unread}</span>}
            </button>

            {open && (
                <div className="notification-dropdown">
                    <div className="notification-dropdown-header">
                        <strong>การแจ้งเตือน</strong>
                        {unread > 0 && (
                            <button type="button" onClick={handleMarkAll}>
                                อ่านทั้งหมด
                            </button>
                        )}
                    </div>

                    {items.length === 0 ? (
                        <p className="notification-empty">ยังไม่มีการแจ้งเตือน</p>
                    ) : (
                        <ul className="notification-list">
                            {items.map((n) => (
                                <li
                                    key={n.id}
                                    className={n.read ? "read" : "unread"}
                                    onClick={() => handleItemClick(n)}
                                >
                                    <p>{n.message}</p>
                                    <span>{n.createdAt ? n.createdAt.slice(0, 16).replace("T", " ") : ""}</span>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            )}
        </div>
    );
}

export default NotificationBell;