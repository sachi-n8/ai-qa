import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import { UploadCloud } from "lucide-react";
import "./UploadPage.css";

export default function UploadPage() {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleUpload = async () => {
    if (!file) return;

    const formData = new FormData();
    formData.append("file", file);

    try {
      setLoading(true);
      setMessage("");

      await api.post("/upload", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      setMessage("✅ Upload successful. Redirecting...");
      setTimeout(() => navigate("/dashboard"), 1200);
    } catch (err) {
      console.error(err);
      setMessage("❌ Upload failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="upload-root">
      <div className="upload-card upload-glow">

        {/* ICON */}
        <div className="upload-icon">
          <UploadCloud size={34} />
        </div>

        {/* TITLE */}
        <h2 className="upload-title">Upload Document</h2>
        <p className="upload-subtitle">
          Upload a file to transcribe, chat, or summarize
        </p>

        {/* FILE INPUT */}
        <label className="file-box">
          <input
            type="file"
            hidden
            onChange={(e) => setFile(e.target.files[0])}
          />
          {file ? (
            <span className="file-name">{file.name}</span>
          ) : (
            <span className="file-placeholder">
              Click to choose a file
            </span>
          )}
        </label>

        {/* UPLOAD BUTTON */}
        <button
          onClick={handleUpload}
          disabled={!file || loading}
          className="upload-btn"
        >
          {loading ? "Uploading..." : "Upload"}
        </button>

        {/* MESSAGE */}
        {message && (
          <p className="upload-message">{message}</p>
        )}

        {/* BACK */}
        <button
          onClick={() => navigate("/dashboard")}
          className="back-link"
        >
          ← Back to Dashboard
        </button>
      </div>
    </div>
  );
}
