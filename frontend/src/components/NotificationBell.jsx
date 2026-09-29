import { useEffect, useRef, useState } from "react";
import { Bell } from "reicon-react";
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
            // ถ้าโหลดจำนวนแจ้งเตือนไม่ได้ ไม่รบกวนผู้ใช้
        }
    }

    async function loadList() {
        try {
            const result = await getNotifications({ size: 10 });
            setItems(result?.content || []);
        } catch {
            setItems([]);
        }
    }

    // เช็กจำนวนแจ้งเตือนทุก 10 วินาที
    useEffect(() => {
        refreshUnread();

        const timer = setInterval(refreshUnread, 10000);

        return () => clearInterval(timer);
    }, []);

    // เปิด dropdown แล้วค่อยโหลดรายการ
    useEffect(() => {
        if (open) {
            loadList();
        }
    }, [open]);

    // คลิกข้างนอกเพื่อปิด
    useEffect(() => {
        function handleClickOutside(e) {
            if (boxRef.current && !boxRef.current.contains(e.target)) {
                setOpen(false);
            }
        }

        document.addEventListener("mousedown", handleClickOutside);

        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    async function handleItemClick(notification) {
        if (notification.read) return;

        try {
            await markNotificationRead(notification.id);

            setItems((prev) =>
                prev.map((item) =>
                    item.id === notification.id
                        ? { ...item, read: true }
                        : item,
                ),
            );

            refreshUnread();
        } catch {
            // ignore
        }
    }

    async function handleMarkAll() {
        try {
            await markAllNotificationsRead();

            setItems((prev) =>
                prev.map((item) => ({
                    ...item,
                    read: true,
                })),
            );

            setUnread(0);
        } catch {
            // ignore
        }
    }

    function toggleOpen() {
        setOpen((prev) => !prev);
    }

    return (
        <div className="notification-bell" ref={boxRef}>
            <button
                className="notification-button"
                type="button"
                onClick={toggleOpen}
                aria-label="การแจ้งเตือน"
            >
                <Bell size={24} weight="Filled" />

                {unread > 0 && (
                    <span className="notification-badge">
                        {unread > 9 ? "9+" : unread}
                    </span>
                )}
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
                        <p className="notification-empty">
                            ยังไม่มีการแจ้งเตือน
                        </p>
                    ) : (
                        <ul className="notification-list">
                            {items.map((notification) => (
                                <li
                                    key={notification.id}
                                    className={
                                        notification.read ? "read" : "unread"
                                    }
                                    onClick={() =>
                                        handleItemClick(notification)
                                    }
                                >
                                    <p>{notification.message}</p>

                                    <span>
                                        {notification.createdAt
                                            ? notification.createdAt
                                                  .slice(0, 16)
                                                  .replace("T", " ")
                                            : ""}
                                    </span>
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
