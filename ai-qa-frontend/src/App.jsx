import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage";
import ChatPage from "./pages/ChatPage";
import UploadPage from "./pages/UploadPage";
import MediaPage from "./pages/MediaPage";
import SummaryPage from "./pages/SummaryPage";
import ProtectedRoute from "./auth/ProtectedRoute";
import { useAuth } from "./auth/AuthContext";

export default function App() {
  const { isAuthenticated } = useAuth();

  return (
    <Routes>
      {/* Public */}
      <Route
        path="/login"
        element={isAuthenticated ? <Navigate to="/dashboard" /> : <LoginPage />}
      />

      {/* Protected */}
      <Route element={<ProtectedRoute />}>
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/upload" element={<UploadPage />} />
        <Route path="/chat/:documentId" element={<ChatPage />} />
        <Route path="/media/:documentId" element={<MediaPage />} />
        <Route path="/summary/:documentId" element={<SummaryPage />} />
      </Route>

      {/* Default */}
      <Route
        path="/"
        element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"} />}
      />

      <Route path="*" element={<h2>Page Not Found</h2>} />
    </Routes>
  );
}
