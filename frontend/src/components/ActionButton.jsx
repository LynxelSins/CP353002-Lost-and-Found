import "./ActionButton.css";

function ArrowIcon({ size = 20 }) {
    return (
        <svg
            width={size}
            height={size}
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2.5"
            strokeLinecap="round"
            strokeLinejoin="round"
        >
            <path d="M12 19V5" />
            <path d="m6 11 6-6 6 6" />
        </svg>
    );
}

function CheckIcon({ size = 20 }) {
    return (
        <svg
            width={size}
            height={size}
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2.8"
            strokeLinecap="round"
            strokeLinejoin="round"
        >
            <path d="m5 12 4 4L19 6" pathLength="1" />
        </svg>
    );
}

/**
 * ปุ่มแบบแบ่งครึ่ง: [ ข้อความ | ไอคอน ]
 *
 * phase    "idle" | "busy" | "done"
 * ratio    0-1 ความคืบหน้า (ใช้ตอน busy) -> น้ำค่อยๆ เติมขึ้นในช่องไอคอน
 * labels   { idle, busy, done }
 * variant  "teal" | "dark"
 * block    true = เต็มความกว้าง
 */
function ActionButton({
    phase = "idle",
    ratio = 0,
    labels,
    variant = "teal",
    block = false,
    type = "button",
    onClick,
    disabled = false,
}) {
    const label = labels[phase];

    return (
        <button
            type={type}
            className={`action-btn action-btn--${variant} is-${phase} ${
                block ? "action-btn--block" : ""
            }`}
            onClick={onClick}
            disabled={disabled || phase !== "idle"}
        >
            <span className="action-btn-label" key={label}>
                {label}
            </span>

            <span className="action-btn-side" aria-hidden="true">
                <span className="ab-glyph ab-arrow">
                    <ArrowIcon />
                </span>

                <span
                    className="ab-fill"
                    style={{ height: `${Math.min(1, Math.max(0, ratio)) * 100}%` }}
                />

                <span className="ab-glyph ab-check">
                    <CheckIcon />
                </span>
            </span>
        </button>
    );
}

export default ActionButton;
