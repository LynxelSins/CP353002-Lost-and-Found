import { useState } from 'react'
import Sidebar from '../components/Sidebar.jsx'
import CreateReportModal from '../components/CreateReportModal.jsx'
import './ProfilePage.css'

function ProfilePage() {
    const [showReport, setShowReport] = useState(false)
    const [account, setAccount] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
    })
    const [passwords, setPasswords] = useState({
        current: '',
        next: '',
        confirm: '',
    })

    function updateAccount(key, value) {
        setAccount({ ...account, [key]: value })
    }

    function updatePassword(key, value) {
        setPasswords({ ...passwords, [key]: value })
    }

    function handleSaveAccount(event) {
        event.preventDefault()
    }

    function handleChangePassword(event) {
        event.preventDefault()
    }

    return (
        <div className="profile-page">
            <Sidebar active="profile" onReport={() => setShowReport(true)} />

            <main className="profile-main">
                <h1>โปรไฟล์ของฉัน</h1>

                <form className="profile-card" onSubmit={handleSaveAccount}>
                    <div className="profile-card-header">
                        <div>
                            <h2>ข้อมูลบัญชี</h2>
                            <p>ข้อมูลจะเชื่อมกับ Backend ภายหลัง</p>
                        </div>
                        <button type="submit">บันทึก</button>
                    </div>

                    <div className="profile-fields">
                        <label>
                            ชื่อ
                            <input
                                value={account.firstName}
                                onChange={(e) => updateAccount('firstName', e.target.value)}
                                placeholder="ชื่อ"
                            />
                        </label>

                        <label>
                            นามสกุล
                            <input
                                value={account.lastName}
                                onChange={(e) => updateAccount('lastName', e.target.value)}
                                placeholder="นามสกุล"
                            />
                        </label>

                        <label>
                            อีเมล
                            <input
                                type="email"
                                value={account.email}
                                onChange={(e) => updateAccount('email', e.target.value)}
                                placeholder="example@email.com"
                            />
                        </label>

                        <label>
                            เบอร์โทรศัพท์
                            <input
                                value={account.phone}
                                onChange={(e) => updateAccount('phone', e.target.value)}
                                placeholder="เบอร์โทรศัพท์"
                            />
                        </label>
                    </div>
                </form>

                <form className="profile-card" onSubmit={handleChangePassword}>
                    <div className="profile-card-header">
                        <div>
                            <h2>เปลี่ยนรหัสผ่าน</h2>
                            <p>กรอกข้อมูลเมื่อพร้อมเชื่อมกับระบบจริง</p>
                        </div>
                    </div>

                    <div className="profile-fields password-fields">
                        <label>
                            รหัสผ่านปัจจุบัน
                            <input
                                type="password"
                                value={passwords.current}
                                onChange={(e) => updatePassword('current', e.target.value)}
                            />
                        </label>

                        <label>
                            รหัสผ่านใหม่
                            <input
                                type="password"
                                value={passwords.next}
                                onChange={(e) => updatePassword('next', e.target.value)}
                            />
                        </label>

                        <label>
                            ยืนยันรหัสผ่านใหม่
                            <input
                                type="password"
                                value={passwords.confirm}
                                onChange={(e) => updatePassword('confirm', e.target.value)}
                            />
                        </label>
                    </div>

                    <div className="profile-card-footer">
                        <button type="submit">เปลี่ยนรหัสผ่าน</button>
                    </div>
                </form>
            </main>

            {showReport && (
                <CreateReportModal onClose={() => setShowReport(false)} />
            )}
        </div>
    )
}

export default ProfilePage
