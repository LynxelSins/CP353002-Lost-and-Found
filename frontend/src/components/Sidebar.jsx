import { useNavigate } from "react-router-dom";
import { House } from "reicon-react";
import { Hearts } from "reicon-react";
import { AddCircle } from 'reicon-react';
import { Search4 } from 'reicon-react';
import "./Sidebar.css";

function Sidebar({ active = "home", onReport }) {
    const navigate = useNavigate();

    return (
        <aside className="sidebar">
            <button
                className="sidebar-brand"
                onClick={() => navigate("/items")}
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
                    onClick={() => navigate("/items")}
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
                    onClick={() => navigate("/my-items-list")}
                >
                    <Hearts size={24} />
                    รายการของฉัน
                </button>
            </nav>

            <button className="sidebar-report" onClick={onReport}>
                <AddCircle size={24} />
                แจ้งของหาย/พบ
            </button>
        </aside>
    );
}

export default Sidebar;
