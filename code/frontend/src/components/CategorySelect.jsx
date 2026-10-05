import { useEffect, useState } from "react";
import { getCategories } from "../api/categoryApi.js";
import "./CategorySelect.css";

function CategorySelect({ value, onChange, onCategoryChange, required = true }) {
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        let alive = true;

        async function loadCategories() {
            setLoading(true);
            setError("");
            try {
                const data = await getCategories();
                if (!alive) return;
                setCategories(Array.isArray(data) ? data : []);
            } catch (err) {
                if (!alive) return;
                setError(
                    err.response?.data?.message ||
                        "โหลดหมวดหมู่สิ่งของไม่สำเร็จ",
                );
            } finally {
                if (alive) setLoading(false);
            }
        }

        loadCategories();
        return () => {
            alive = false;
        };
    }, []);

    return (
        <div className="category-select-wrap">
            <select
                value={value}
                onChange={(event) => {
                    const nextId = event.target.value;
                    const selected = categories.find(
                        (category) =>
                            String(
                                category.categoryId ??
                                    category.category_id ??
                                    category.id,
                            ) === nextId,
                    );
                    onChange(nextId);
                    onCategoryChange?.(selected || null);
                }}
                required={required}
                disabled={loading || !!error}
            >
                <option value="">
                    {loading ? "กำลังโหลดหมวดหมู่..." : "เลือกประเภทสิ่งของ"}
                </option>
                {categories.map((category) => {
                    const id =
                        category.categoryId ??
                        category.category_id ??
                        category.id;
                    const name =
                        category.categoryName ??
                        category.category_name ??
                        category.name;

                    return (
                        <option key={id} value={id}>
                            {name}
                        </option>
                    );
                })}
            </select>
            {error && <small className="category-select-error">{error}</small>}
            {!loading && !error && categories.length === 0 && (
                <small className="category-select-help">
                    ยังไม่มีหมวดหมู่ในตาราง categories
                </small>
            )}
        </div>
    );
}

export default CategorySelect;
