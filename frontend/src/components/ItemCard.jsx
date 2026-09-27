import { useState } from "react";
import "./ItemCard.css";
import ClaimModel from "./ClaimModel.jsx";
import StatusBadge from "./StatusBadge.jsx";
import { watchReport, unwatchReport } from "../api/reportApi.js";

function ItemCard({ item, onFavoriteChange }) {
    const [favorite, setFavorite] = useState(!!item.watchedByMe);
    const [busy, setBusy] = useState(false);
    const [showDetail, setShowDetail] = useState(false);

    async function toggleFavorite() {
        if (busy) return;
        const next = !favorite;
        setBusy(true);
        setFavorite(next); // optimistic update ให้ UI ตอบสนองทันที

        try {
            if (next) {
                await watchReport(item.id);
            } else {
                await unwatchReport(item.id);
            }
            onFavoriteChange && onFavoriteChange();
        } catch (err) {
            setFavorite(!next); // rollback ถ้า backend error
            alert(err.response?.data?.message || "กดติดตามไม่สำเร็จ กรุณาลองใหม่");
        } finally {
            setBusy(false);
        }
    }

    const isLost = item.type === "lost";

    return (
        <article className="item-card">
            <div className="item-image">
                {item.image ? (
                    <img src={item.image} alt={item.title || "สิ่งของ"} />
                ) : (
                    <div className="item-image-empty">ไม่มีรูปภาพ</div>
                )}
            </div>

            <div className="item-card-content">
                <div className="item-badges">
                    <span className={isLost ? "item-status lost" : "item-status found"}>
                        {isLost ? "ของหาย" : "ของที่พบ"}
                    </span>
                    {item.status && <StatusBadge status={item.status} />}
                </div>

                <h3>{item.title || "ไม่มีชื่อรายการ"}</h3>

                <div className="item-meta">
                    <span>◉ &nbsp;{item.location || "ไม่ระบุสถานที่"}</span>
                    <span>◷ &nbsp;{item.date || "ไม่ระบุวันที่"}</span>
                </div>

                <div className="item-card-bottom">
                    <button
                        className={favorite ? "bookmark-button active" : "bookmark-button"}
                        type="button"
                        onClick={toggleFavorite}
                        disabled={busy}
                        title="ติดตาม"
                    >
                        {favorite ? "♥" : "♡"}
                    </button>

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