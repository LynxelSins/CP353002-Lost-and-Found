import { useEffect, useRef, useState } from 'react'
import Sidebar from '../components/Sidebar.jsx'
import CreateReportModal from '../components/CreateReportModal.jsx'
import { getMe, updateProfile } from '../api/authApi.js'
import { uploadImage } from '../api/uploadApi.js'
import './ProfilePage.css'

// full_name ฝั่ง backend เก็บเป็นค่าเดียว -> ฟอร์มนี้แยกเป็นชื่อ/นามสกุลเพื่อ UX ที่คุ้นเคย
// จึงต้องแตก/รวมคำสองแบบนี้เวลาโหลด/บันทึกข้อมูล
function splitFullName(fullName) {
    if (!fullName) return { firstName: '', lastName: '' }
    const [firstName, ...rest] = fullName.trim().split(/\s+/)
    return { firstName: firstName || '', lastName: rest.join(' ') }
}

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

    const [avatarUrl, setAvatarUrl] = useState('')
    const [avatarUploading, setAvatarUploading] = useState(false)
    const [avatarError, setAvatarError] = useState('')
    const fileInputRef = useRef(null)

    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [saveMessage, setSaveMessage] = useState('')

    useEffect(() => {
        getMe()
            .then((user) => {
                const { firstName, lastName } = splitFullName(user.fullName)
                setAccount({
                    firstName,
                    lastName,
                    email: user.email || '',
                    phone: user.phoneNumber || '',
                })
                setAvatarUrl(user.avatarUrl || '')
            })
            .catch(() => {})
            .finally(() => setLoading(false))
    }, [])

    function updateAccount(key, value) {
        setAccount({ ...account, [key]: value })
    }

    function updatePassword(key, value) {
        setPasswords({ ...passwords, [key]: value })
    }

    // เลือกรูปแล้วอัปโหลด + บันทึกเป็นรูปโปรไฟล์ทันที ไม่ต้องรอกดปุ่ม "SAVE"
    async function handleAvatarChange(event) {
        const file = event.target.files?.[0]
        if (!file) return

        setAvatarError('')
        const localPreview = URL.createObjectURL(file)
        setAvatarUrl(localPreview)
        setAvatarUploading(true)

        try {
            const uploadedUrl = await uploadImage(file)
            await updateProfile({ avatarUrl: uploadedUrl })
            setAvatarUrl(uploadedUrl)
            setSaveMessage('อัปเดตรูปโปรไฟล์เรียบร้อย')
        } catch {
            setAvatarError('อัปโหลดรูปโปรไฟล์ไม่สำเร็จ ลองใหม่อีกครั้ง')
            setAvatarUrl((prev) => (prev === localPreview ? '' : prev))
        } finally {
            setAvatarUploading(false)
            event.target.value = ''
        }
    }

    async function handleSaveAccount(event) {
        event.preventDefault()
        setSaving(true)
        setSaveMessage('')
        try {
            await updateProfile({
                fullName: `${account.firstName} ${account.lastName}`.trim(),
                phoneNumber: account.phone,
            })
            setSaveMessage('บันทึกข้อมูลบัญชีเรียบร้อย')
        } catch {
            setSaveMessage('บันทึกข้อมูลไม่สำเร็จ ลองใหม่อีกครั้ง')
        } finally {
            setSaving(false)
        }
    }

    function handleChangePassword(event) {
        event.preventDefault()
    }

    const initials =
        (account.firstName[0] || account.email[0] || '?').toUpperCase()

    return (
        <div className="profile-page">
            <Sidebar active="profile" onReport={() => setShowReport(true)} />

            <main className="profile-main">
                <form className="profile-card" onSubmit={handleSaveAccount}>
                    <div className="profile-card-header">
                        <h2>Account Information</h2>
                        <button type="submit" className="profile-btn-primary" disabled={saving || loading}>
                            {saving ? 'SAVING...' : 'SAVE'}
                        </button>
                    </div>

                    <div className="profile-account-body">
                        <div className="profile-avatar-col">
                            <div className="profile-avatar">
                                {avatarUrl ? (
                                    <img src={avatarUrl} alt="รูปโปรไฟล์" />
                                ) : (
                                    <span>{initials}</span>
                                )}
                                {avatarUploading && (
                                    <div className="profile-avatar-overlay">กำลังอัปโหลด...</div>
                                )}
                            </div>

                            <button
                                type="button"
                                className="profile-avatar-btn"
                                onClick={() => fileInputRef.current?.click()}
                                disabled={avatarUploading}
                            >
                                Upload New Picture
                            </button>
                            {avatarError && <p className="profile-avatar-error">{avatarError}</p>}

                            <input
                                ref={fileInputRef}
                                type="file"
                                accept="image/png, image/jpeg, image/webp, image/gif"
                                onChange={handleAvatarChange}
                                hidden
                            />
                        </div>

                        <div className="profile-fields-list">
                            <label>
                                <span className="profile-field-label">First Name</span>
                                <input
                                    value={account.firstName}
                                    onChange={(e) => updateAccount('firstName', e.target.value)}
                                    placeholder="First Name"
                                />
                            </label>

                            <label>
                                <span className="profile-field-label">Last Name</span>
                                <input
                                    value={account.lastName}
                                    onChange={(e) => updateAccount('lastName', e.target.value)}
                                    placeholder="Name"
                                />
                            </label>

                            <label>
                                <span className="profile-field-label">Email Address</span>
                                <input type="email" value={account.email} placeholder="example@email.com" readOnly />
                            </label>

                            <label>
                                <span className="profile-field-label">Phone Number</span>
                                <input
                                    value={account.phone}
                                    onChange={(e) => updateAccount('phone', e.target.value)}
                                    placeholder="+123 456-7889"
                                />
                            </label>
                        </div>
                    </div>

                    {saveMessage && <p className="profile-save-message">{saveMessage}</p>}
                    {loading && <p className="profile-save-message profile-loading-message">กำลังโหลดข้อมูล...</p>}
                </form>

                <form className="profile-card" onSubmit={handleChangePassword}>
                    <div className="profile-card-header">
                        <h2>Change Password</h2>
                    </div>

                    <div className="profile-fields-list profile-fields-list--password">
                        <label>
                            <span className="profile-field-label">Current Password</span>
                            <input
                                type="password"
                                value={passwords.current}
                                onChange={(e) => updatePassword('current', e.target.value)}
                            />
                        </label>

                        <label>
                            <span className="profile-field-label">New Password</span>
                            <input
                                type="password"
                                value={passwords.next}
                                onChange={(e) => updatePassword('next', e.target.value)}
                            />
                        </label>

                        <label>
                            <span className="profile-field-label">Confirm New Password</span>
                            <input
                                type="password"
                                value={passwords.confirm}
                                onChange={(e) => updatePassword('confirm', e.target.value)}
                            />
                        </label>
                    </div>

                    <div className="profile-card-footer">
                        <button type="submit" className="profile-btn-primary">CHANGE</button>
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
