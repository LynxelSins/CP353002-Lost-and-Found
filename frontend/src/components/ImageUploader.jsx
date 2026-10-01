import { useEffect, useRef, useState } from "react";
import "./ImageUploader.css";
import { uploadImage } from "../api/uploadApi.js";

/**
 * props
 *   onChange      (urls: string[]) => void   รายการ URL ที่อัปโหลดสำเร็จแล้ว (เรียกทุกครั้งที่รายการเปลี่ยน)
 *   onBusyChange  (busy: boolean) => void    true ระหว่างที่ยังมีรูปกำลังอัปโหลด — parent ใช้กันไม่ให้กดส่งฟอร์มก่อนรูปขึ้นครบ
 *   error         string                     ข้อความ error จาก parent เช่น "ต้องแนบรูปอย่างน้อย 1 รูป"
 *   max           จำนวนรูปสูงสุด (default 5)
 */
function ImageUploader({ onChange, onBusyChange, error: externalError, max = 5 }) {
    const [previews, setPreviews] = useState([]);
    const [error, setError] = useState("");

    // เก็บ callback ล่าสุดไว้ใน ref เพื่อไม่ให้ effect ด้านล่างวนซ้ำเวลา parent ส่ง arrow function ตัวใหม่มาทุก render
    const onChangeRef = useRef(onChange);
    const onBusyChangeRef = useRef(onBusyChange);
    useEffect(() => {
        onChangeRef.current = onChange;
        onBusyChangeRef.current = onBusyChange;
    });

    // แจ้ง parent หลัง state เปลี่ยนเสร็จ
    // (ของเดิมเรียก onChange ข้างใน setPreviews(updater) ซึ่งเป็น side effect ใน render phase
    //  -> React เตือน "Cannot update a component while rendering a different component" และ StrictMode เรียกซ้ำสองรอบ)
    useEffect(() => {
        onChangeRef.current?.(
            previews.filter((p) => p.uploadedUrl).map((p) => p.uploadedUrl),
        );
        onBusyChangeRef.current?.(previews.some((p) => p.uploading));
    }, [previews]);

    async function handleFiles(event) {
        const input = event.target;
        const files = Array.from(input.files || []);
        input.value = ""; // รีเซ็ตทันที เลือกไฟล์เดิมซ้ำได้ และไม่ค้างค่าเมื่อ return ก่อนเวลา
        if (files.length === 0) return;

        if (previews.length + files.length > max) {
            setError(`อัปโหลดได้สูงสุด ${max} รูป`);
            return;
        }
        setError("");

        for (const file of files) {
            const localId = crypto.randomUUID();
            const localUrl = URL.createObjectURL(file);
            setPreviews((prev) => [
                ...prev,
                { id: localId, url: localUrl, uploading: true },
            ]);

            try {
                const uploadedUrl = await uploadImage(file);
                setPreviews((prev) =>
                    prev.map((p) =>
                        p.id === localId
                            ? { ...p, uploading: false, uploadedUrl }
                            : p,
                    ),
                );
            } catch (err) {
                // backend ส่งข้อความไทยมาให้ เช่น ไฟล์เกิน 5MB / ชนิดไฟล์ไม่รองรับ
                setError(
                    err.response?.data?.message ||
                        "อัปโหลดรูปไม่สำเร็จ ลองใหม่อีกครั้ง",
                );
                URL.revokeObjectURL(localUrl);
                setPreviews((prev) => prev.filter((p) => p.id !== localId));
            }
        }
    }

    function removeImage(id) {
        const target = previews.find((p) => p.id === id);
        if (target) URL.revokeObjectURL(target.url);
        setPreviews((prev) => prev.filter((p) => p.id !== id));
    }

    const shownError = error || externalError;

    return (
        <div className="image-uploader">
            <div className="image-uploader-grid">
                {previews.map((p) => (
                    <div key={p.id} className="image-uploader-thumb">
                        <img src={p.url} alt="ตัวอย่างรูป" />
                        {p.uploading && (
                            <span className="image-uploader-loading">กำลังอัปโหลด...</span>
                        )}
                        <button type="button" onClick={() => removeImage(p.id)}>
                            ×
                        </button>
                    </div>
                ))}

                {previews.length < max && (
                    <label className="image-uploader-add">
                        +
                        <input
                            type="file"
                            accept="image/png, image/jpeg, image/webp, image/gif"
                            multiple={max > 1}
                            onChange={handleFiles}
                            hidden
                        />
                    </label>
                )}
            </div>

            {shownError && <p className="image-uploader-error">{shownError}</p>}
        </div>
    );
}

export default ImageUploader;