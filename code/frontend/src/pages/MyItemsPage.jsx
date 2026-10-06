import { useEffect, useState } from "react";
import Sidebar from "../components/Sidebar.jsx";
import UserHeaderBar from "../components/UserHeaderBar.jsx";
import StatusBadge from "../components/StatusBadge.jsx";
import ClaimModel from "../components/ClaimModel.jsx";
import CreateReportModal from "../components/CreateReportModal.jsx";
import { getMyReports, getWatchedReports } from "../api/reportApi.js";
import { getMyClaims } from "../api/claimApi.js";
import { getMe } from "../api/authApi.js";
import { UserCircle } from "reicon-react";
import { CommentDots } from "reicon-react";

import "./MyItemsPage.css";

const TABS = [
    { key: "posted", label: "ประกาศที่ฉันโพสต์" },
    { key: "claims", label: "คำร้องของฉัน" },
    { key: "watched", label: "รายการที่ฉันติดตาม" },
];

const CLAIM_STATUS_LABEL = {
    PENDING: "รอตรวจสอบ",
    APPROVED: "อนุมัติแล้ว",
    REJECTED: "ถูกปฏิเสธ",
};

function MyItemsPage() {
    const [activeTab, setActiveTab] = useState("posted");
    const [showReport, setShowReport] = useState(false);
    const [openReportId, setOpenReportId] = useState(null);

    const [userName, setUserName] = useState("ผู้ใช้งาน");
    const [avatarUrl, setAvatarUrl] = useState("");
    const [reports, setReports] = useState([]);
    const [claims, setClaims] = useState([]);
    const [watched, setWatched] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const loadTabData = async () => {
            setLoading(true);
            setError("");
            try {
                if (activeTab === "posted") {
                    const result = await getMyReports({ size: 50 });
                    setReports(result.content || []);
                } else if (activeTab === "claims") {
                    const result = await getMyClaims({ size: 50 });
                    setClaims(result.content || []);
                } else {
                    const result = await getWatchedReports({ size: 50 });
                    setWatched(result.content || []);
                }
            } catch (err) {
                setError(err.response?.data?.message || "โหลดข้อมูลไม่สำเร็จ");
            } finally {
                setLoading(false);
            }
        };

        loadTabData();
    }, [activeTab]);

    useEffect(() => {
        getMe()
            .then((user) => {
                setUserName(user.fullName || user.email);
                setAvatarUrl(user.avatarUrl || "");
            })
            .catch(() => {});
    }, []);

    const refreshCurrentTab = async () => {
        setLoading(true);
        setError("");
        try {
            if (activeTab === "posted") {
                const result = await getMyReports({ size: 50 });
                setReports(result.content || []);
            } else if (activeTab === "claims") {
                const result = await getMyClaims({ size: 50 });
                setClaims(result.content || []);
            } else {
                const result = await getWatchedReports({ size: 50 });
                setWatched(result.content || []);
            }
        } catch (err) {
            setError(err.response?.data?.message || "โหลดข้อมูลไม่สำเร็จ");
        } finally {
            setLoading(false);
        }
    };

    function renderReportsTable(rows, emptyText) {
        if (rows.length === 0) {
            return (
                <div className="my-items-empty">
                    <div>♡</div>
                    <h2>ไม่มีรายการ</h2>
                    <p>{emptyText}</p>
                </div>
            );
        }
        return (
            <table className="my-items-table">
                <thead>
                    <tr>
                        <th>รายการ</th>
                        <th>ประเภท</th>
                        <th>สถานะ</th>
                        <th>ผู้ติดตาม</th>
                        <th>คำร้อง</th>
                    </tr>
                </thead>
                <tbody>
                    {rows.map((r) => (
                        <tr key={r.id} onClick={() => setOpenReportId(r.id)}>
                            <td>
                                <div className="my-items-row-main">
                                    <div className="my-items-thumb">
                                        {r.thumbnailUrl ? (
                                            <img
                                                src={r.thumbnailUrl}
                                                alt={r.title}
                                            />
                                        ) : (
                                            <div className="my-items-thumb-empty" />
                                        )}
                                    </div>
                                    <div>
                                        <strong>{r.title}</strong>
                                        <div className="my-items-row-meta">
                                            {r.locationName} ·{" "}
                                            <span className="my-items-date">
                                                {r.createdAt
                                                    ? r.createdAt
                                                          .slice(0, 16)
                                                          .replace("T", " ")
                                                    : ""}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <span
                                    className={`report-type-badge ${
                                        String(r.type).toLowerCase() === "lost"
                                            ? "lost"
                                            : "found"
                                    }`}
                                >
                                    {String(r.type).toLowerCase() === "lost"
                                        ? "ของหาย"
                                        : "ของพบ"}
                                </span>
                            </td>
                            <td>
                                <StatusBadge status={r.status} />
                            </td>
                            <td>
                                <UserCircle
                                    size={24}
                                    weight="Filled"
                                    color="#1a9e91"
                                />{" "}
                                {r.watcherCount ?? 0}
                            </td>
                            <td>
                                <CommentDots
                                    size={24}
                                    weight="Filled"
                                    color="#1a9e91"
                                />{" "}
                                {r.claimCount ?? 0}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        );
    }

    function renderClaimsTable() {
        if (claims.length === 0) {
            return (
                <div className="my-items-empty">
                    <div>♡</div>
                    <h2>ยังไม่มีคำร้อง</h2>
                    <p>
                        ยื่นขอรับของจากประกาศที่คุณสนใจ แล้วรายการจะมาแสดงตรงนี้
                    </p>
                </div>
            );
        }
        return (
            <table className="my-items-table">
                <thead>
                    <tr>
                        <th>รายการที่ยื่นคำร้อง</th>
                        <th>สถานะประกาศ</th>
                        <th>สถานะคำร้องของฉัน</th>
                        <th>วันที่ยื่น</th>
                    </tr>
                </thead>
                <tbody>
                    {claims.map((c) => (
                        <tr
                            key={c.id}
                            onClick={() => setOpenReportId(c.reportId)}
                        >
                            <td>
                                <div className="my-items-row-main">
                                    <div className="my-items-thumb">
                                        {c.reportThumbnailUrl ? (
                                            <img
                                                src={c.reportThumbnailUrl}
                                                alt={c.reportTitle}
                                            />
                                        ) : (
                                            <div className="my-items-thumb-empty" />
                                        )}
                                    </div>
                                    <div>
                                        <strong>{c.reportTitle}</strong>
                                        <div className="my-items-row-meta">
                                            {c.reportLocationName}
                                        </div>
                                    </div>
                                </div>
                            </td>
                            <td>
                                <StatusBadge status={c.reportStatus} />
                            </td>
                            <td>
                                <span
                                    className={`claim-status ${c.claimStatus.toLowerCase()}`}
                                >
                                    {CLAIM_STATUS_LABEL[c.claimStatus] ||
                                        c.claimStatus}
                                </span>
                            </td>
                            <td>
                                {c.createdAt ? c.createdAt.slice(0, 10) : ""}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        );
    }

    return (
        <div className="my-items-page">
            <Sidebar active="my-items" onReport={() => setShowReport(true)} />

            <main className="my-items-main">
                <header className="my-items-header">
                    <h1>รายการของฉัน (My Items)</h1>
                    <UserHeaderBar userName={userName} avatarUrl={avatarUrl} />
                </header>

                <nav
                    className="my-items-tabs"
                    style={{
                        "--active-tab-index": TABS.findIndex(
                            (tab) => tab.key === activeTab,
                        ),
                    }}
                >
                    <span
                        className="my-items-tab-indicator"
                        aria-hidden="true"
                    />
                    {TABS.map((tab) => (
                        <button
                            key={tab.key}
                            type="button"
                            className={activeTab === tab.key ? "active" : ""}
                            onClick={() => setActiveTab(tab.key)}
                        >
                            {tab.label}
                        </button>
                    ))}
                </nav>

                {loading && <p className="my-items-loading">กำลังโหลด...</p>}
                {error && <p className="my-items-error">{error}</p>}

                {!loading && !error && (
                    <div key={activeTab} className="my-items-content-slide">
                        {activeTab === "posted" &&
                            renderReportsTable(
                                reports,
                                "ยังไม่มีประกาศที่คุณโพสต์ กดปุ่ม 'แจ้งของหาย/พบ' เพื่อเริ่มโพสต์แรก",
                            )}
                        {activeTab === "claims" && renderClaimsTable()}
                        {activeTab === "watched" &&
                            renderReportsTable(
                                watched,
                                "กดหัวใจที่รายการที่สนใจในหน้าแรก แล้วรายการจะมาแสดงตรงนี้",
                            )}
                    </div>
                )}
            </main>

            {showReport && (
                <CreateReportModal
                    onClose={() => setShowReport(false)}
                    onCreated={() => {
                        setShowReport(false);
                        setActiveTab("posted");
                        refreshCurrentTab();
                    }}
                />
            )}

            {openReportId && (
                <ClaimModel
                    reportId={openReportId}
                    onClose={() => setOpenReportId(null)}
                    onChanged={refreshCurrentTab}
                />
            )}
        </div>
    );
}

export default MyItemsPage;
