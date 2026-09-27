import { useNavigate } from 'react-router-dom'
import './Sidebar.css'

function Sidebar({ active = 'home', onReport }) {
    const navigate = useNavigate()

    return (
        <aside className="sidebar">
            <button className="sidebar-brand" onClick={() => navigate('/items')}>
                <span className="sidebar-brand-icon">⌕</span>
                <span>
                    <strong>Lost &amp; Found</strong>
                    <small>แพลตฟอร์มแจ้งของหายและของพบ</small>
                </span>
            </button>

            <nav className="sidebar-nav">
                <button
                    className={active === 'home' ? 'sidebar-link active' : 'sidebar-link'}
                    onClick={() => navigate('/items')}
                >
                    <span>⌂</span>
                    หน้าแรก
                </button>

                <button
                    className={active === 'my-items' ? 'sidebar-link active' : 'sidebar-link'}
                    onClick={() => navigate('/my-items-list')}
                >
                    <span>▣</span>
                    รายการของฉัน
                </button>
            </nav>

            <button className="sidebar-report" onClick={onReport}>
                <span>＋</span>
                แจ้งของหาย/พบ
            </button>
        </aside>
    )
}

export default Sidebar
