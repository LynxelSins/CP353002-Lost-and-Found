import "./FilterBar.css";

function FilterBar({ filters, setFilters }) {
    function updateFilter(key, value) {
        setFilters({ ...filters, [key]: value });
    }

    function clearFilters() {
        setFilters({
            type: "ประเภททั้งหมด",
            location: "สถานที่ทั้งหมด",
            date: "วันที่ล่าสุด",
            category: "ทั้งหมด",
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
                    value={filters.location}
                    onChange={(e) => updateFilter("location", e.target.value)}
                >
                    <option>สถานที่ทั้งหมด</option>
                    <option>อาคารเรียน</option>
                    <option>โรงอาหาร</option>
                    <option>ห้องสมุด</option>
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
                <strong>แท็กยอดนิยม:</strong>
                {[
                    "ทั้งหมด",
                    "โทรศัพท์",
                    "กุญแจ",
                    "บัตรประชาชน",
                    "โน้ตบุ๊ก",
                    "หูฟัง",
                    "นาฬิกา",
                    "กระเป๋า",
                ].map((tag) => (
                    <button
                        key={tag}
                        type="button"
                        className={
                            filters.category === tag
                                ? "popular-tag active"
                                : "popular-tag"
                        }
                        onClick={() => updateFilter("category", tag)}
                    >
                        {tag}
                    </button>
                ))}
            </div>
        </section>
    );
}

export default FilterBar;
