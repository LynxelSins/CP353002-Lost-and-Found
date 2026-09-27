import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import Sidebar from "../components/Sidebar.jsx";
import FilterBar from "../components/FilterBar.jsx";
import ItemCard from "../components/ItemCard.jsx";
import Pagination from "../components/Pagination.jsx";
import CreateReportModal from "../components/CreateReportModal.jsx";
import { getMe } from "../api/authApi.js";
import "./ItemListPage.css";
import { getReports, getWatchedReports } from "../api/reportApi.js";
import NotificationBell from "../components/NotificationBell.jsx";

// แปลง ReportSummaryResponse จาก backend ให้ตรงกับ shape ที่ ItemCard ต้องการ
function toViewItem(report) {
    return {
        id: report.id,
        title: report.title,
        location: report.locationName,
        type: report.type ? report.type.toLowerCase() : "lost", // LOST/FOUND -> lost/found
        date: report.createdAt ? report.createdAt.slice(0, 10) : "",
        image: report.thumbnailUrl,
        status: report.status,
    };
}

function ItemListPage() {
    const navigate = useNavigate();
    const [search, setSearch] = useState("");
    const [showReport, setShowReport] = useState(false);
    const [filters, setFilters] = useState({
        type: "ประเภททั้งหมด",
        location: "สถานที่ทั้งหมด",
        date: "วันที่ล่าสุด",
        category: "ทั้งหมด",
    });
    const [page, setPage] = useState(1);
    const [items, setItems] = useState([]);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(true);
    const [userName, setUserName] = useState("ผู้ใช้งาน");

        const loadReports = useCallback(async () => {
        setLoading(true);
        try {
            const typeParam =
                filters.type === "ของหาย"
                    ? "LOST"
                    : filters.type === "ของที่พบ"
                      ? "FOUND"
                      : undefined;

            const [result, watchedResult] = await Promise.all([
                getReports({
                    page: page - 1,
                    size: 12,
                    type: typeParam,
                    keyword: search || undefined,
                }),
                getWatchedReports({ size: 100 }).catch(() => ({ content: [] })),
            ]);

            const watchedIds = new Set((watchedResult.content || []).map((r) => r.id));

            setItems(
                (result.content || []).map((r) => ({
                    ...toViewItem(r),
                    watchedByMe: watchedIds.has(r.id),
                })),
            );
            setTotalPages(result.totalPages || 1);
        } catch (error) {
            console.error("โหลดประกาศไม่สำเร็จ:", error);
        } finally {
            setLoading(false);
        }
    }, [page, filters.type, search]);

    useEffect(() => {
        loadReports();
    }, [loadReports]);

    useEffect(() => {
        getMe()
            .then((user) => setUserName(user.fullName || user.email))
            .catch(() => {});
    }, []);

    return (
        <div className="item-list-page">
            <Sidebar active="home" onReport={() => setShowReport(true)} />

            <main className="item-list-main">
                <header className="top-bar">
                    <div className="search-box">
                        <span>⌕</span>
                        <input
                            type="text"
                            placeholder="ค้นหาสิ่งของ, สถานที่, แท็ก..."
                            value={search}
                            onChange={(e) => {
                                setSearch(e.target.value);
                                setPage(1);
                            }}
                        />
                    </div>

                    <div className="user-area">
                        <NotificationBell />

                        <button
                            className="user-avatar"
                            type="button"
                            onClick={() => navigate("/my-profile")}
                            aria-label="ไปหน้าโปรไฟล์"
                        >
                            👤
                        </button>

                        <button
                            className="user-info"
                            type="button"
                            onClick={() => navigate("/my-profile")}
                        >
                            <span>สวัสดี,</span>
                            <strong>{userName}</strong>
                        </button>
                    </div>
                </header>

                <FilterBar filters={filters} setFilters={setFilters} />

                <section className="items-section">
                    <h2>รายการล่าสุด</h2>

                    {loading ? (
                        <div className="empty-items">
                            <p>กำลังโหลด...</p>
                        </div>
                    ) : items.length > 0 ? (
                        <div className="item-grid">
                            {items.map((item) => (
                                <ItemCard key={item.id} item={item} />
                            ))}
                        </div>
                    ) : (
                        <div className="empty-items">
                            <div className="empty-icon">⌕</div>
                            <h3>ยังไม่มีรายการ</h3>
                            <p>เมื่อมีข้อมูลจากระบบ รายการจะแสดงตรงนี้</p>
                        </div>
                    )}
                </section>

                <Pagination
                    currentPage={page}
                    totalPages={totalPages}
                    onPageChange={setPage}
                />
            </main>

            {showReport && (
                <CreateReportModal
                    onClose={() => setShowReport(false)}
                    onCreated={() => {
                        setShowReport(false);
                        setPage(1);
                        loadReports();
                    }}
                />
            )}
        </div>
    );
}

export default ItemListPage;
