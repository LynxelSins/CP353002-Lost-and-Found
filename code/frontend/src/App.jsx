import { Routes, Route, Navigate } from "react-router-dom";

import AuthPage from "./pages/AuthPage.jsx";
import ItemListPage from "./pages/ItemListPage.jsx";
import MyItemsPage from "./pages/MyItemsPage.jsx";
import ProfilePage from "./pages/ProfilePage.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";

function App() {
    return (
        <Routes>
            <Route path="/" element={<AuthPage />} />
            <Route path="/login" element={<AuthPage />} />
            <Route
                path="/items"
                element={
                    <ProtectedRoute>
                        <ItemListPage />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/my-items-list"
                element={
                    <ProtectedRoute>
                        <MyItemsPage />
                    </ProtectedRoute>
                }
            />
            <Route
                path="/my-profile"
                element={
                    <ProtectedRoute>
                        <ProfilePage />
                    </ProtectedRoute>
                }
            />
            <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>//
    );
}

export default App;
