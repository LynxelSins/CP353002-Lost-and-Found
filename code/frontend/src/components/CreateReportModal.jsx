import { useEffect, useState } from "react";
import "./CreateReportModal.css";
import ImageUploader from "./ImageUploader.jsx";
import ActionButton from "./ActionButton.jsx";
import { createReport } from "../api/reportApi.js";
import { getTags } from "../api/tagApi.js";

// แท็กที่มีอยู่: แสดงกี่อันก่อนซ่อนที่เหลือไว้หลังปุ่ม "…"
const MAX_VISIBLE_AVAILABLE_TAGS = 8;

function CreateReportModal({ onClose, onCreated }) {
    const [type, setType] = useState("lost");
    const [title, setTitle] = useState("");
    const [location, setLocation] = useState("");
    const [date, setDate] = useState("");
    const [description, setDescription] = useState("");
    const [imageUrls, setImageUrls] = useState([]);
    const [uploading, setUploading] = useState(false);
    const [imageError, setImageError] = useState("");
    const [tags, setTags] = useState([]);
    const [tagInput, setTagInput] = useState("");
    const [availableTags, setAvailableTags] = useState([]);
    const [showAllAvailable, setShowAllAvailable] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [done, setDone] = useState(false);
    const [ratio, setRatio] = useState(0);
    const [error, setError] = useState("");

    const canCollapseAvailable =
        availableTags.length > MAX_VISIBLE_AVAILABLE_TAGS;
    const visibleAvailableTags =
        showAllAvailable || !canCollapseAvailable
            ? availableTags
            : availableTags.slice(0, MAX_VISIBLE_AVAILABLE_TAGS);
    const hiddenAvailableCount =
        availableTags.length - visibleAvailableTags.length;

    useEffect(() => {
        getTags()
            .then(setAvailableTags)
            .catch(() => setAvailableTags([]));
    }, []);

    // createReport ไม่ได้ส่งความคืบหน้ากลับมา เลยไล่ค่าเข้าใกล้ ~90% ระหว่างรอ
    // แล้วเต็มตอนโพสต์สำเร็จ
    useEffect(() => {
        if (!submitting) return;

        setRatio(0);

        const timer = setInterval(() => {
            setRatio((r) => r + (0.9 - r) * 0.08);
        }, 120);

        return () => clearInterval(timer);
    }, [submitting]);

    function addTag(rawTag) {
        const name = rawTag.trim();
        if (!name) return;
        if (tags.some((tag) => tag.toLowerCase() === name.toLowerCase())) {
            setTagInput("");
            return;
        }
        setTags((prev) => [...prev, name]);
        setTagInput("");
    }

    function removeTag(tagToRemove) {
        setTags((prev) => prev.filter((tag) => tag !== tagToRemove));
    }

    function handleTagKeyDown(event) {
        if (event.key === "Enter" || event.key === ",") {
            event.preventDefault();
            addTag(tagInput);
        }
    }

    async function handleSubmit(event) {
        event.preventDefault();
        if (submitting || done) return;
        setError("");

        // บังคับแนบรูปอย่างน้อย 1 รูป (backend เช็คซ้ำด้วย @NotEmpty เผื่อยิง API ตรง)
        if (uploading) {
            setError("รูปภาพกำลังอัปโหลด กรุณารอสักครู่แล้วกดโพสต์อีกครั้ง");
            return;
        }
        if (imageUrls.length === 0) {
            setImageError("กรุณาแนบรูปภาพอย่างน้อย 1 รูป");
            return;
        }

        setSubmitting(true);

        try {
            await createReport({
                type: type.toUpperCase(),
                title,
                description,
                locationName: location,
                eventTimestamp: `${date}T00:00:00`,
                imageUrls,
                tagNames: tags,
            });

            // โชว์ "โพสต์แล้ว ✓" ที่ปุ่มสั้นๆ ก่อนปิด
            setSubmitting(false);
            setRatio(1);
            setDone(true);
            await new Promise((resolve) => setTimeout(resolve, 1000));

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
                    <button type="button" onClick={onClose} aria-label="ปิด">
                        <svg
                            width="14"
                            height="14"
                            viewBox="0 0 14 14"
                            fill="none"
                            aria-hidden="true"
                        >
                            <path
                                d="M2 2l10 10M12 2L2 12"
                                stroke="currentColor"
                                strokeWidth="2"
                                strokeLinecap="round"
                            />
                        </svg>
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
                        วันที่ของหาย / วันที่พบของ
                        <input
                            type="date"
                            value={date}
                            onChange={(e) => setDate(e.target.value)}
                            required
                        />
                    </label>

                    <label className="tag-field">
                        แท็ก
                        <div className="tag-input-wrap">
                            {tags.map((tag) => (
                                <span className="selected-tag" key={tag}>
                                    #{tag}
                                    <button
                                        type="button"
                                        onClick={() => removeTag(tag)}
                                        aria-label={`ลบแท็ก ${tag}`}
                                    >
                                        ×
                                    </button>
                                </span>
                            ))}
                            <input
                                type="text"
                                value={tagInput}
                                onChange={(e) => setTagInput(e.target.value)}
                                onKeyDown={handleTagKeyDown}
                                onBlur={() => addTag(tagInput)}
                                placeholder={
                                    tags.length
                                        ? "เพิ่มแท็ก..."
                                        : "พิมพ์แท็กแล้วกด Enter (ไม่ต้องใส่ #)"
                                }
                            />
                        </div>
                        {availableTags.length > 0 && (
                            <div className="available-tags">
                                <small>แท็กที่มีอยู่:</small>
                                {visibleAvailableTags.map((tag) => (
                                    <button
                                        type="button"
                                        className="available-tag"
                                        key={tag.id}
                                        onClick={() => addTag(tag.tagName)}
                                    >
                                        #{tag.tagName}
                                    </button>
                                ))}
                                {canCollapseAvailable && (
                                    <button
                                        type="button"
                                        className="available-tag more-tags"
                                        onClick={() =>
                                            setShowAllAvailable((prev) => !prev)
                                        }
                                        title={
                                            showAllAvailable
                                                ? "แสดงน้อยลง"
                                                : "แสดงแท็กทั้งหมด"
                                        }
                                    >
                                        {showAllAvailable
                                            ? "แสดงน้อยลง"
                                            : `… +${hiddenAvailableCount}`}
                                    </button>
                                )}
                            </div>
                        )}
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

                    <div className="form-field">
                        รูปภาพประกอบ * (แนบอย่างน้อย 1 รูป)
                        <ImageUploader
                            max={5}
                            error={imageError}
                            onBusyChange={setUploading}
                            onChange={(urls) => {
                                setImageUrls(urls);
                                if (urls.length > 0) setImageError("");
                            }}
                        />
                    </div>

                    {error && <p className="report-form-error">{error}</p>}

                    <div className="report-modal-actions">
                        <button
                            type="button"
                            className="report-cancel"
                            onClick={onClose}
                        >
                            ยกเลิก
                        </button>
                        <ActionButton
                            type="submit"
                            variant="teal"
                            phase={done ? "done" : submitting ? "busy" : "idle"}
                            ratio={ratio}
                            labels={{
                                idle: "โพสต์รายการ",
                                busy: "กำลังโพสต์",
                                done: "โพสต์แล้ว",
                            }}
                        />
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreateReportModal;
