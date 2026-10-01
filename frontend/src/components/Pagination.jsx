import "./Pagination.css";

function Pagination({ currentPage, totalPages, onPageChange }) {
    if (currentPage >= totalPages) {
        return null;
    }

    return (
        <div className="pagination">
            <button onClick={() => onPageChange(currentPage + 1)}>
                โหลดเพิ่มเติม
            </button>
        </div>
    );
}

export default Pagination;