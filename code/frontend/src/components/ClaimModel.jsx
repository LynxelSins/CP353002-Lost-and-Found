import { useCallback, useEffect, useState } from "react";
import "./ClaimModel.css";
import ImageUploader from "./ImageUploader.jsx";
import StatusBadge from "./StatusBadge.jsx";
import { closeReport, getReportById } from "../api/reportApi.js";
import { getMe } from "../api/authApi.js";
import {
    approveClaim,
    createClaim,
    getClaimsForReport,
    rejectClaim,
} from "../api/claimApi.js";
import { useFormValidation } from "../hooks/useFormValidation.js";

const CLAIM_STATUS_LABEL = {
    PENDING: "รอตรวจสอบ",
    APPROVED: "อนุมัติแล้ว",
    REJECTED: "ถูกปฏิเสธ",
};

function ClaimModel({ reportId, onClose, onChanged }) {
    const [loading, setLoading] = useState(true);
    const [loadError, setLoadError] = useState("");
    const [report, setReport] = useState(null);
    const [me, setMe] = useState(null);
    const [claims, setClaims] = useState([]);

    const [evidenceText, setEvidenceText] = useState("");
    const [evidenceImageUrl, setEvidenceImageUrl] = useState("");
    const [uploading, setUploading] = useState(false);
    const [uploaderKey, setUploaderKey] = useState(0); // เปลี่ยนค่า = ล้างรูป preview ใน ImageUploader
    const [submitting, setSubmitting] = useState(false);
    const [actionError, setActionError] = useState("");
    const [actionBusyId, setActionBusyId] = useState(null);
    const [closing, setClosing] = useState(false);

    // ต้องตรงกับ backend: LostReportClaimEligibilityStrategy บังคับ >= 10 ตัวอักษร (ประกาศ LOST)
    const minEvidenceLength = report?.type === "LOST" ? 10 : 5;

    const { errors, validate, clearError } = useFormValidation({
        evidenceText: (value) =>
            !value || value.trim().length < minEvidenceLength
                ? `กรุณากรอกหลักฐานยืนยันอย่างน้อย ${minEvidenceLength} ตัวอักษร`
                : null,
        // บังคับแนบรูปหลักฐาน (backend เช็คซ้ำด้วย @NotBlank)
        evidenceImageUrl: (value) =>
            !value ? "กรุณาแนบรูปหลักฐานอย่างน้อย 1 รูป" : null,
    });

    const isOwner = !!(me && report && report.ownerId === me.id);
    const canSubmitClaim =
        !!report &&
        !isOwner &&
        (report.status === "OPEN" || report.status === "MATCH_PENDING");

    const refreshReport = useCallback(async () => {
        const data = await getReportById(reportId);
        setReport(data);
        return data;
    }, [reportId]);

    const refreshClaims = useCallback(async () => {
        const list = await getClaimsForReport(reportId);
        setClaims(list);
    }, [reportId]);

    useEffect(() => {
        let alive = true;

        async function load() {
            setLoading(true);
            setLoadError("");
            try {
                const [reportData, meData] = await Promise.all([
                    getReportById(reportId),
                    getMe(),
                ]);
                if (!alive) return;
                setReport(reportData);
                setMe(meData);

                if (reportData.ownerId === meData.id) {
                    const claimList = await getClaimsForReport(reportId);
                    if (alive) setClaims(claimList);
                }
            } catch (err) {
                if (alive) {
                    setLoadError(
                        err.response?.data?.message ||
                            "โหลดรายละเอียดประกาศไม่สำเร็จ",
                    );
                }
            } finally {
                if (alive) setLoading(false);
            }
        }

        load();
        return () => {
            alive = false;
        };
    }, [reportId]);

    async function handleSubmitClaim(event) {
        event.preventDefault();
        setActionError("");
        if (uploading) {
            setActionError("รูปหลักฐานกำลังอัปโหลด กรุณารอสักครู่แล้วลองอีกครั้ง");
            return;
        }
        if (!validate({ evidenceText, evidenceImageUrl })) return;

        setSubmitting(true);
        try {
            await createClaim(reportId, {
                evidenceText,
                evidenceImageUrl,
            });
            setEvidenceText("");
            setEvidenceImageUrl("");
            setUploaderKey((k) => k + 1); // ล้าง preview รูปเก่า ไม่ให้ค้างทั้งที่ state ว่างแล้ว
            await refreshReport();
            onChanged && onChanged();
        } catch (err) {
            setActionError(
                err.response?.data?.message ||
                    "ส่งคำขอรับของไม่สำเร็จ กรุณาลองใหม่",
            );
        } finally {
            setSubmitting(false);
        }
    }

    async function handleApprove(claimId) {
        if (
            !window.confirm(
                "ยืนยันอนุมัติคำขอนี้? คำขออื่นที่ยังรอตรวจสอบอยู่จะถูกปฏิเสธให้อัตโนมัติ",
            )
        ) {
            return;
        }
        setActionBusyId(claimId);
        try {
            await approveClaim(claimId);
            await Promise.all([refreshClaims(), refreshReport()]);
            onChanged && onChanged();
        } catch (err) {
            alert(err.response?.data?.message || "ดำเนินการไม่สำเร็จ");
        } finally {
            setActionBusyId(null);
        }
    }

    async function handleReject(claimId) {
        if (!window.confirm("ยืนยันปฏิเสธคำขอนี้?")) return;
        setActionBusyId(claimId);
        try {
            await rejectClaim(claimId);
            await Promise.all([refreshClaims(), refreshReport()]);
            onChanged && onChanged();
        } catch (err) {
            alert(err.response?.data?.message || "ดำเนินการไม่สำเร็จ");
        } finally {
            setActionBusyId(null);
        }
    }

    async function handleCloseReport() {
        if (
            !window.confirm(
                "ยืนยันปิดเคส? ใช้เมื่อส่งมอบของเรียบร้อยแล้ว และปิดแล้วไม่สามารถย้อนกลับได้",
            )
        ) {
            return;
        }
        setClosing(true);
        try {
            await closeReport(reportId);
            await refreshReport();
            onChanged && onChanged();
        } catch (err) {
            alert(err.response?.data?.message || "ปิดเคสไม่สำเร็จ");
        } finally {
            setClosing(false);
        }
    }

    return (
        <div className="claim-modal-backdrop" onMouseDown={onClose}>
            <div
                className="claim-modal"
                onMouseDown={(e) => e.stopPropagation()}
            >
                <div className="claim-modal-header">
                    <h2>{loading ? "กำลังโหลด..." : report?.title}</h2>
                    <button type="button" onClick={onClose} aria-label="ปิด">
                        ×
                    </button>
                </div>

                {loading && (
                    <p className="claim-modal-loading">
                        กำลังโหลดรายละเอียด...
                    </p>
                )}
                {loadError && (
                    <p className="claim-modal-error">{loadError}</p>
                )}

                {!loading && report && (
                    <div className="claim-modal-body">
                        <StatusBadge status={report.status} />

                        {report.images?.length > 0 && (
                            <div className="claim-modal-images">
                                {report.images.map((img) => (
                                    <img
                                        key={img.id}
                                        src={img.imageUrl}
                                        alt={report.title}
                                    />
                                ))}
                            </div>
                        )}

                        <dl className="claim-modal-meta">
                            <div>
                                <dt>ประเภท</dt>
                                <dd>
                                    {report.type === "LOST"
                                        ? "ของหาย"
                                        : "ของที่พบ"}
                                </dd>
                            </div>
                            <div>
                                <dt>สถานที่</dt>
                                <dd>{report.locationName}</dd>
                            </div>
                            <div>
                                <dt>วันที่เกิดเหตุ</dt>
                                <dd>
                                    {report.eventTimestamp?.slice(0, 10) ||
                                        "ไม่ระบุ"}
                                </dd>
                            </div>
                            <div>
                                <dt>ผู้แจ้ง</dt>
                                <dd>{report.ownerName}</dd>
                            </div>
                            {report.tags?.length > 0 && (
                                <div>
                                    <dt>แท็ก</dt>
                                    <dd>{report.tags.join(", ")}</dd>
                                </div>
                            )}
                        </dl>

                        {report.description && (
                            <p className="claim-modal-description">
                                {report.description}
                            </p>
                        )}

                        {isOwner ? (
                            <div className="claim-modal-claims">
                                <h3>คำขอรับของ ({claims.length})</h3>

                                {claims.length === 0 && (
                                    <p className="claim-modal-note">
                                        ยังไม่มีผู้ยื่นคำขอรับของ
                                    </p>
                                )}

                                {claims.map((c) => (
                                    <div key={c.id} className="claim-item">
                                        <div className="claim-item-head">
                                            {c.claimantAvatarUrl ? (
                                                <img
                                                    className="claim-avatar"
                                                    src={c.claimantAvatarUrl}
                                                    alt=""
                                                />
                                            ) : (
                                                <div className="claim-avatar claim-avatar-empty">
                                                    {(c.claimantName || "?")[0].toUpperCase()}
                                                </div>
                                            )}
                                            <strong>{c.claimantName}</strong>
                                            <span
                                                className={`claim-status ${c.claimStatus.toLowerCase()}`}
                                            >
                                                {CLAIM_STATUS_LABEL[
                                                    c.claimStatus
                                                ] || c.claimStatus}
                                            </span>
                                        </div>

                                        <p>{c.evidenceText}</p>

                                        {c.evidenceImageUrl && (
                                            <img
                                                className="claim-item-evidence"
                                                src={c.evidenceImageUrl}
                                                alt="หลักฐานยืนยัน"
                                            />
                                        )}

                                        {c.claimStatus === "PENDING" && (
                                            <div className="claim-item-actions">
                                                <button
                                                    type="button"
                                                    className="claim-approve"
                                                    disabled={
                                                        actionBusyId === c.id
                                                    }
                                                    onClick={() =>
                                                        handleApprove(c.id)
                                                    }
                                                >
                                                    อนุมัติ
                                                </button>
                                                <button
                                                    type="button"
                                                    className="claim-reject"
                                                    disabled={
                                                        actionBusyId === c.id
                                                    }
                                                    onClick={() =>
                                                        handleReject(c.id)
                                                    }
                                                >
                                                    ปฏิเสธ
                                                </button>
                                            </div>
                                        )}
                                    </div>
                                ))}

                                {report.status === "CLAIMED" && (
                                    <div className="claim-close-section">
                                        <p className="claim-modal-note">
                                            ส่งมอบของให้ผู้ที่ได้รับอนุมัติแล้ว
                                            กดปิดเคสเพื่อจบขั้นตอน
                                        </p>
                                        <button
                                            type="button"
                                            className="claim-close"
                                            disabled={closing}
                                            onClick={handleCloseReport}
                                        >
                                            {closing ? "กำลังปิดเคส..." : "ปิดเคส"}
                                        </button>
                                    </div>
                                )}
                            </div>
                        ) : canSubmitClaim ? (
                            <form
                                className="claim-submit-form"
                                onSubmit={handleSubmitClaim}
                            >
                                <h3>ยื่นขอรับของนี้</h3>

                                <label>
                                    หลักฐานยืนยันความเป็นเจ้าของ
                                    <textarea
                                        rows="3"
                                        value={evidenceText}
                                        onChange={(e) => {
                                            setEvidenceText(e.target.value);
                                            clearError("evidenceText");
                                        }}
                                        placeholder="เช่น ลักษณะเฉพาะ, ของที่อยู่ข้างใน, จุดสังเกตอื่น ๆ"
                                    />
                                    {errors.evidenceText && (
                                        <small className="claim-field-error">
                                            {errors.evidenceText}
                                        </small>
                                    )}
                                </label>

                                <div className="form-field">
                                    รูปหลักฐาน * (แนบ 1 รูป)
                                    <ImageUploader
                                        key={uploaderKey}
                                        max={1}
                                        error={errors.evidenceImageUrl}
                                        onBusyChange={setUploading}
                                        onChange={(urls) => {
                                            setEvidenceImageUrl(urls[0] || "");
                                            if (urls[0]) clearError("evidenceImageUrl");
                                        }}
                                    />
                                </div>

                                {actionError && (
                                    <p className="claim-modal-error">
                                        {actionError}
                                    </p>
                                )}

                                <button type="submit" disabled={submitting || uploading}>
                                    {submitting
                                        ? "กำลังส่ง..."
                                        : "ส่งคำขอรับของ"}
                                </button>
                            </form>
                        ) : (
                            <p className="claim-modal-note">
                                {report.status === "CLAIMED" &&
                                    "ประกาศนี้มีผู้ได้รับการอนุมัติให้รับของไปแล้ว"}
                                {report.status === "CLOSED" &&
                                    "ประกาศนี้ปิดเคสแล้ว ไม่สามารถยื่นคำขอเพิ่มได้"}
                            </p>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}

export default ClaimModel;