import "./ReportTypeBadge.css";

function ReportTypeBadge({ type }) {
    const normalizedType = String(type || "").toUpperCase();
    const isLost = normalizedType === "LOST" || normalizedType === "LOST_ITEM";

    return (
        <span className={`report-type-badge ${isLost ? "lost" : "found"}`}>
            {isLost ? "ของหาย" : "ของที่พบ"}
        </span>
    );
}

export default ReportTypeBadge;
