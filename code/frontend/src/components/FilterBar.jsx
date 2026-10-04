import { useEffect, useState } from "react";
import { getTags } from "../api/tagApi.js";
import "./FilterBar.css";

function FilterBar({ filters, setFilters }) {
    const [tags, setTags] = useState([]);

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
                {tags.map((tag) => (
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
                {tags.length === 0 && (
                    <span className="no-tags">ยังไม่มีแท็ก</span>
                )}
            </div>
        </section>
    );
}

export default FilterBar;
