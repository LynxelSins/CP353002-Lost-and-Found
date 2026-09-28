import { useState } from "react";
import "./CreateReportModal.css";
import ImageUploader from "./ImageUploader.jsx";
import { createReport } from "../api/reportApi.js";

function CreateReportModal({ onClose, onCreated }) {
    const [type, setType] = useState("lost");
    const [title, setTitle] = useState("");
    const [location, setLocation] = useState("");
    const [date, setDate] = useState("");
    const [description, setDescription] = useState("");
    const [imageUrls, setImageUrls] = useState([]);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        setSubmitting(true);

        try {
            await createReport({
                type: type.toUpperCase(), // lost/found -> LOST/FOUND
                title,
                description,
                locationName: location,
                eventTimestamp: `${date}T00:00:00`,
                imageUrls,
            });
            onCreated ? onCreated() : onClose();
        } catch (err) {
            setError(
                err.response?.data?.message ||
                    "โพสต์ประกาศไม่สำเร็จ กรุณาลองใหม่",
            );
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <div className="report-modal-backdrop" onMouseDown={onClose}>
            <div
                className="report-modal"
                onMouseDown={(e) => e.stopPropagation()}
            >
                <div className="report-modal-header">
                    <div>
                        <h2>แจ้งของหาย / พบ</h2>
                        <p>กรอกข้อมูลสิ่งของที่ต้องการแจ้ง</p>
                    </div>
                    <button type="button" onClick={onClose}>
                        ×
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="report-form">
                    <label>
                        ประเภท
                        <select
                            value={type}
                            onChange={(e) => setType(e.target.value)}
                        >
                            <option value="lost">ของหาย</option>
                            <option value="found">ของที่พบ</option>
                        </select>
                    </label>

                    <label>
                        ชื่อสิ่งของ
                        <input
                            type="text"
                            placeholder="เช่น กระเป๋าสตางค์, โทรศัพท์"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            required
                        />
                    </label>

                    <label>
                        สถานที่
                        <input
                            type="text"
                            placeholder="ระบุสถานที่ที่หายหรือพบ"
                            value={location}
                            onChange={(e) => setLocation(e.target.value)}
                            required
                        />
                    </label>

                    <label>
                        วันที่
                        <input
                            type="date"
                            value={date}
                            onChange={(e) => setDate(e.target.value)}
                            required
                        />
                    </label>

                    <label>
                        รายละเอียดเพิ่มเติม
                        <textarea
                            placeholder="รายละเอียดของสิ่งของ เช่น สี ยี่ห้อ หรือลักษณะเด่น"
                            rows="4"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                        />
                    </label>

                    <label>
                        รูปภาพประกอบ (ไม่บังคับ)
                        <ImageUploader onChange={setImageUrls} />
                    </label>

                    {error && <p className="report-form-error">{error}</p>}

                    <div className="report-modal-actions">
                        <button
                            type="button"
                            className="report-cancel"
                            onClick={onClose}
                        >
                            ยกเลิก
                        </button>
                        <button
                            type="submit"
                            className="report-submit"
                            disabled={submitting}
                        >
                            {submitting ? "กำลังโพสต์..." : "โพสต์รายการ"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreateReportModal;
