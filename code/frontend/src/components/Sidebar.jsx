import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { House } from "reicon-react";
import { Hearts } from "reicon-react";
import { AddCircle } from 'reicon-react';
import { Search4 } from 'reicon-react';
import "./Sidebar.css";

function Sidebar({ active = "home", onReport }) {
    const navigate = useNavigate();
    const [open, setOpen] = useState(false);

    function go(path) {
        setOpen(false);
        navigate(path);
    }

    function handleReport() {
        setOpen(false);
        if (onReport) onReport();
    }

    return (
        <>
        <button
            type="button"
            className="sidebar-toggle"
            aria-label="เปิดเมนู"
            aria-expanded={open}
            onClick={() => setOpen((v) => !v)}
        >
            <span></span>
            <span></span>
            <span></span>
        </button>
        <div
            className={open ? "sidebar-backdrop show" : "sidebar-backdrop"}
            onClick={() => setOpen(false)}
        />
        <aside className={open ? "sidebar open" : "sidebar"}>
            <button
                className="sidebar-brand"
                onClick={() => go("/items")}
            >
                <span className="sidebar-brand-icon"><Search4 size={24} weight="Filled" /></span>
                <span>
                    <strong>Lost &amp; Found</strong>
                    <small>แพลตฟอร์มแจ้งของหายและของพบ</small>
                </span>
            </button>

            <nav className="sidebar-nav">
                <button
                    className={
                        active === "home"
                            ? "sidebar-link active"
                            : "sidebar-link"
                    }
                    onClick={() => go("/items")}
                >
                    <House size={24} />
                    หน้าแรก
                </button>

                <button
                    className={
                        active === "my-items"
                            ? "sidebar-link active"
                            : "sidebar-link"
                    }
                    onClick={() => go("/my-items-list")}
                >
                    <Hearts size={24} />
                    รายการของฉัน
                </button>
            </nav>

            <button className="sidebar-report" onClick={handleReport}>
                <AddCircle size={24} />
                แจ้งของหาย/พบ
            </button>
        </aside>
        </>
    );
}

export default Sidebar;
