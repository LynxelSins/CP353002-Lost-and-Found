import { useState } from "react";
import "./ImageUploader.css";
import { uploadImage } from "../api/uploadApi.js";

/** อัปโหลดรูปได้สูงสุด `max` รูป ส่ง URL ที่อัปโหลดสำเร็จกลับไปให้ parent ผ่าน onChange */
function ImageUploader({ onChange, max = 5 }) {
    const [previews, setPreviews] = useState([]);
    const [error, setError] = useState("");

    async function handleFiles(event) {
        const files = Array.from(event.target.files || []);
        if (files.length === 0) return;

        if (previews.length + files.length > max) {
            setError(`อัปโหลดได้สูงสุด ${max} รูป`);
            return;
        }
        setError("");

        for (const file of files) {
            const localId = crypto.randomUUID();
            setPreviews((prev) => [
                ...prev,
                { id: localId, url: URL.createObjectURL(file), uploading: true },
            ]);

            try {
                const uploadedUrl = await uploadImage(file);
                setPreviews((prev) => {
                    const next = prev.map((p) =>
                        p.id === localId ? { ...p, uploading: false, uploadedUrl } : p,
                    );
                    onChange(next.filter((p) => p.uploadedUrl).map((p) => p.uploadedUrl));
                    return next;
                });
            } catch {
                setError("อัปโหลดรูปไม่สำเร็จ ลองใหม่อีกครั้ง");
                setPreviews((prev) => prev.filter((p) => p.id !== localId));
            }
        }
        event.target.value = "";
    }

    function removeImage(id) {
        setPreviews((prev) => {
            const next = prev.filter((p) => p.id !== id);
            onChange(next.filter((p) => p.uploadedUrl).map((p) => p.uploadedUrl));
            return next;
        });
    }

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
                            multiple
                            onChange={handleFiles}
                            hidden
                        />
                    </label>
                )}
            </div>

            {error && <p className="image-uploader-error">{error}</p>}
        </div>
    );
}

export default ImageUploader;