import "./StatusBadge.css";

// ต้องตรงกับ enum ReportStatus ฝั่ง backend (OPEN, MATCH_PENDING, CLAIMED, CLOSED)
const STATUS_INFO = {
    OPEN: { label: "เปิดรับแจ้ง", className: "open" },
    MATCH_PENDING: { label: "รอตรวจสอบผู้เคลม", className: "pending" },
    CLAIMED: { label: "มีผู้รับของแล้ว", className: "claimed" },
    CLOSED: { label: "ปิดเคสแล้ว", className: "closed" },
};

function StatusBadge({ status }) {
    if (!status) return null;
    const info = STATUS_INFO[status] || { label: status, className: "default" };

    return <span className={`status-badge ${info.className}`}>{info.label}</span>;
}

export default StatusBadge;
