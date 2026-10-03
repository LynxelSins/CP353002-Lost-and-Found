import { useEffect, useState, useCallback, useRef } from "react";
import Sidebar from "../components/Sidebar.jsx";
import FilterBar from "../components/FilterBar.jsx";
import ItemCard from "../components/ItemCard.jsx";
import Pagination from "../components/Pagination.jsx";
import CreateReportModal from "../components/CreateReportModal.jsx";
import { getMe } from "../api/authApi.js";
import "./ItemListPage.css";
import { getReports, getWatchedReports } from "../api/reportApi.js";
import UserHeaderBar from "../components/UserHeaderBar.jsx";
import { Search4 } from 'reicon-react';

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
    const [search, setSearch] = useState("");
    const [showReport, setShowReport] = useState(false);
    const [filters, setFilters] = useState({
        type: "ประเภททั้งหมด",
        date: "วันที่ล่าสุด",
        tag: "ทั้งหมด",
    });
    const [page, setPage] = useState(1);
    const [items, setItems] = useState([]);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(true);
    const [userName, setUserName] = useState("ผู้ใช้งาน");
    const [avatarUrl, setAvatarUrl] = useState("");

    // ใช้ตรวจว่า filter/คำค้นหาเปลี่ยนไปจากรอบก่อนหรือไม่ (เพื่อรู้ว่าต้องรีเซ็ตหน้ากลับเป็น 1)
    const filterKey = `${filters.type}|${filters.date}|${filters.tag}|${search}`;
    const prevFilterKeyRef = useRef(filterKey);

    const loadReports = useCallback(
        async (targetPage) => {
            setLoading(true);
            try {
                const typeParam =
                    filters.type === "ของหาย"
                        ? "LOST"
                        : filters.type === "ของที่พบ"
                          ? "FOUND"
                          : undefined;

                const sortParam =
                    filters.date === "วันที่เก่าสุด"
                        ? "createdAt,asc"
                        : "createdAt,desc";

                const [result, watchedResult] = await Promise.all([
                    getReports({
                        page: targetPage - 1,
                        size: 12,
                        type: typeParam,
                        keyword: search || undefined,
                        tag:
                            filters.tag !== "ทั้งหมด" ? filters.tag : undefined,
                        sort: sortParam,
                    }),
                    getWatchedReports({ size: 100 }).catch(() => ({
                        content: [],
                    })),
                ]);

                const watchedIds = new Set(
                    (watchedResult.content || []).map((r) => r.id),
                );
                const mapped = (result.content || []).map((r) => ({
                    ...toViewItem(r),
                    watchedByMe: watchedIds.has(r.id),
                }));

                // หน้า 1 = แทนที่รายการเดิมทั้งหมด, หน้าอื่นๆ (กด "โหลดเพิ่มเติม") = ต่อท้ายรายการเดิม
                setItems((prev) =>
                    targetPage === 1 ? mapped : [...prev, ...mapped],
                );
                setTotalPages(result.totalPages || 1);
            } catch (error) {
                console.error("โหลดประกาศไม่สำเร็จ:", error);
            } finally {
                setLoading(false);
            }
        },
        [filters.type, filters.date, filters.tag, search],
    );

    useEffect(() => {
        // ถ้า filter หรือคำค้นหาเปลี่ยน ให้กลับไปหน้า 1 ก่อนเสมอ
        if (prevFilterKeyRef.current !== filterKey) {
            prevFilterKeyRef.current = filterKey;
            if (page !== 1) {
                setPage(1);
                return; // effect นี้จะรันซ้ำอีกครั้งตอน page เปลี่ยนเป็น 1
            }
        }
        loadReports(page);
    }, [page, filterKey, loadReports]);

    useEffect(() => {
        getMe()
            .then((user) => {
                setUserName(user.fullName || user.email);
                setAvatarUrl(user.avatarUrl || "");
            })
            .catch(() => {});
    }, []);

    const handleCardChanged = () => {
        loadReports(page);
    };

    return (
        <div className="item-list-page">
            <Sidebar active="home" onReport={() => setShowReport(true)} />

            <main className="item-list-main">
                <header className="top-bar">
                    <div className="search-box">
                        <Search4 size={24} weight="Filled" />
                        <input
                            type="text"
                            placeholder="ค้นหาชื่อสิ่งของ, สถานที่..."
                            value={search}
                            onChange={(e) => {
                                setSearch(e.target.value);
                            }}
                        />
                    </div>

                    <UserHeaderBar userName={userName} avatarUrl={avatarUrl} />
                </header>

                <FilterBar filters={filters} setFilters={setFilters} />

                <section className="items-section">
                    <h2>รายการล่าสุด</h2>

                    {loading && items.length === 0 ? (
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
                            <Search4 size={24} weight="Filled" />
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
                        loadReports(1);
                    }}
                />
            )}
        </div>
    );
}

export default ItemListPage;
