import { useEffect, useState } from "react";
import { getTags } from "../api/tagApi.js";
import "./FilterBar.css";

// แสดงแท็กกี่อันก่อนจะซ่อนที่เหลือไว้หลังปุ่ม "…"
const MAX_VISIBLE_TAGS = 8;

function FilterBar({ filters, setFilters }) {
    const [tags, setTags] = useState([]);
    const [showAllTags, setShowAllTags] = useState(false);

    const canCollapse = tags.length > MAX_VISIBLE_TAGS;

    // ตอนพับ: แสดง MAX_VISIBLE_TAGS อันแรก + แท็กที่ถูกเลือกอยู่ (กันไม่ให้แท็กที่เลือกหายไปจากหน้าจอ)
    const visibleTags =
        showAllTags || !canCollapse
            ? tags
            : tags.filter(
                  (tag, index) =>
                      index < MAX_VISIBLE_TAGS || tag.tagName === filters.tag,
              );
    const hiddenCount = tags.length - visibleTags.length;

    useEffect(() => {
        getTags()
            .then(setTags)
            .catch(() => setTags([]));
    }, []);

    function updateFilter(key, value) {
        setFilters({ ...filters, [key]: value });
    }

    function clearFilters() {
        setFilters({
            type: "ประเภททั้งหมด",
            date: "วันที่ล่าสุด",
            tag: "ทั้งหมด",
        });
    }

    return (
        <section className="filter-bar">
            <div className="filter-row">
                <select
                    value={filters.type}
                    onChange={(e) => updateFilter("type", e.target.value)}
                >
                    <option>ประเภททั้งหมด</option>
                    <option>ของหาย</option>
                    <option>ของที่พบ</option>
                </select>

                <select
                    value={filters.date}
                    onChange={(e) => updateFilter("date", e.target.value)}
                >
                    <option>วันที่ล่าสุด</option>
                    <option>วันที่เก่าสุด</option>
                </select>

                <button
                    type="button"
                    className="clear-filter"
                    onClick={clearFilters}
                >
                    ล้างตัวกรอง
                </button>
            </div>

            <div className="filter-divider" />

            <div className="popular-row">
                <strong>แท็กทั้งหมด:</strong>
                <button
                    type="button"
                    className={
                        filters.tag === "ทั้งหมด"
                            ? "popular-tag active"
                            : "popular-tag"
                    }
                    onClick={() => updateFilter("tag", "ทั้งหมด")}
                >
                    ทั้งหมด
                </button>
                {visibleTags.map((tag) => (
                    <button
                        key={tag.id}
                        type="button"
                        className={
                            filters.tag === tag.tagName
                                ? "popular-tag active"
                                : "popular-tag"
                        }
                        onClick={() => updateFilter("tag", tag.tagName)}
                    >
                        {tag.tagName}
                    </button>
                ))}
                {canCollapse && (showAllTags || hiddenCount > 0) && (
                    <button
                        type="button"
                        className="popular-tag more-tags"
                        onClick={() => setShowAllTags((prev) => !prev)}
                        title={showAllTags ? "แสดงน้อยลง" : "แสดงแท็กทั้งหมด"}
                    >
                        {showAllTags ? "แสดงน้อยลง" : `… +${hiddenCount}`}
                    </button>
                )}
                {tags.length === 0 && (
                    <span className="no-tags">ยังไม่มีแท็ก</span>
                )}
            </div>
        </section>
    );
}

export default FilterBar;
