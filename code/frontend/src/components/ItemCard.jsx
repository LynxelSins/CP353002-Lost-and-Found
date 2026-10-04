import { useState } from "react";
import "./ItemCard.css";
import ClaimModel from "./ClaimModel.jsx";
import StatusBadge from "./StatusBadge.jsx";
import {
    watchReport,
    unwatchReport,
    adminDeleteReport,
} from "../api/reportApi.js";
import { isStaff } from "../api/authApi.js";

function ItemCard({ item, onFavoriteChange }) {
    const [favorite, setFavorite] = useState(!!item.watchedByMe);
    const [busy, setBusy] = useState(false);
    const [showDetail, setShowDetail] = useState(false);
    const [deleted, setDeleted] = useState(false);
    const staff = isStaff();

    async function toggleFavorite() {
        if (busy) return;
        const next = !favorite;
        setBusy(true);
        setFavorite(next);

        try {
            if (next) {
                await watchReport(item.id);
            } else {
                await unwatchReport(item.id);
            }
            onFavoriteChange && onFavoriteChange();
        } catch (err) {
            setFavorite(!next); // rollback ถ้า backend error
            alert(
                err.response?.data?.message || "กดติดตามไม่สำเร็จ กรุณาลองใหม่",
            );
        } finally {
            setBusy(false);
        }
    }

    async function handleAdminDelete() {
        if (busy) return;
        const name = item.title || "ไม่มีชื่อรายการ";
        if (
            !window.confirm(
                `ลบประกาศ "${name}" พร้อมข้อมูลที่เกี่ยวข้องทั้งหมด?`,
            )
        )
            return;

        setBusy(true);
        try {
            await adminDeleteReport(item.id);
            setDeleted(true); // ซ่อนการ์ดทันที ไม่ต้องโหลดรายการใหม่
        } catch (err) {
            alert(
                err.response?.data?.message || "ลบประกาศไม่สำเร็จ กรุณาลองใหม่",
            );
        } finally {
            setBusy(false);
        }
    }

    const isLost = item.type === "lost";

    if (deleted) return null;

    return (
        <article className="report-card">
            <div className="report-card-image">
                {item.image ? (
                    <img src={item.image} alt={item.title || "สิ่งของ"} />
                ) : (
                    <div className="report-card-image-empty">ไม่มีรูปภาพ</div>
                )}
            </div>

            <div className="report-card-content">
                <div className="report-card-badges">
                    <span
                        className={
                            isLost
                                ? "report-status lost"
                                : "report-status found"
                        }
                    >
                        {isLost ? "ของหาย" : "ของที่พบ"}
                    </span>
                    {item.status && <StatusBadge status={item.status} />}
                </div>

                <h3>{item.title || "ไม่มีชื่อรายการ"}</h3>

                <div className="report-card-meta">
                    <span>◉ &nbsp;{item.location || "ไม่ระบุสถานที่"}</span>
                    <span>◷ &nbsp;{item.date || "ไม่ระบุวันที่"}</span>
                </div>

                <div className="report-card-bottom">
                    <button
                        className={
                            favorite
                                ? "bookmark-button active"
                                : "bookmark-button"
                        }
                        type="button"
                        onClick={toggleFavorite}
                        disabled={busy}
                        title="ติดตาม"
                    >
                        {favorite ? "♥" : "♡"}
                    </button>

                    {staff && (
                        <button
                            className="delete-button"
                            type="button"
                            onClick={handleAdminDelete}
                            disabled={busy}
                        >
                            ลบโพสต์
                        </button>
                    )}

                    <button
                        className="claim-button"
                        type="button"
                        onClick={() => setShowDetail(true)}
                    >
                        ดูรายละเอียด / ขอรับของ
                    </button>
                </div>
            </div>

            {showDetail && (
                <ClaimModel
                    reportId={item.id}
                    onClose={() => setShowDetail(false)}
                    onChanged={() => onFavoriteChange && onFavoriteChange()}
                />
            )}
        </article>
    );
}

export default ItemCard;
