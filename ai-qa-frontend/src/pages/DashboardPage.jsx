import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import api from "../api/api";
import { Upload, MessageSquare, FileText, Clock } from "lucide-react";
import "./DashboardPage.css";

export default function DashboardPage() {
  const [docs, setDocs] = useState([]);
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();
  const { logout } = useAuth();

  useEffect(() => {
    api
      .get("/document")
      .then((res) => {
        const list = [];
        if (res.data.latestDocument) list.push(res.data.latestDocument);
        if (res.data.otherDocuments?.length) {
          list.push(...res.data.otherDocuments);
        }
        setDocs(list);
      })
      .catch(() => setDocs([]))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="dashboard-loading">
        Loading dashboard...
      </div>
    );
  }

  const hasDocs = docs.length > 0;
  const latestDocId = hasDocs ? docs[0].id || docs[0]._id : null;

  return (
    <div className="dashboard-root">
      <div className="dashboard-container">

        {/* HEADER */}
        <div className="dashboard-header">
          <h1 className="dashboard-title">
            Welcome, <span>Sachin</span> 👋
          </h1>

          <button
            className="logout-btn"
            onClick={() => logout()}
          >
            Logout
          </button>
        </div>

        {/* ACTION CARDS */}
        <div className="dashboard-actions">
          <ActionCard
            icon={<Upload size={28} />}
            label="Upload Files"
            onClick={() => navigate("/upload")}
          />

          <ActionCard
            icon={<MessageSquare size={28} />}
            label="Chat with Docs"
            disabled={!hasDocs}
            onClick={() => navigate(`/chat/${latestDocId}`)}
          />

          <ActionCard
            icon={<FileText size={28} />}
            label="Summarize"
            disabled={!hasDocs}
            onClick={() => navigate(`/summary/${latestDocId}`)}
          />

          <ActionCard
            icon={<Clock size={28} />}
            label="Media Timecodes"
            disabled={!hasDocs}
            onClick={() => navigate(`/media/${latestDocId}`)}
          />
        </div>

        {/* DOCUMENT TABLE */}
        <div className="dashboard-table-card">
          <h2 className="table-title">Your Documents</h2>

          {!hasDocs ? (
            <p className="empty-text">
              No documents uploaded yet 📂
            </p>
          ) : (
            <table className="doc-table">
              <thead>
                <tr>
                  <th>File</th>
                  <th>Category</th>
                  <th>Status</th>
                  <th className="center">Actions</th>
                </tr>
              </thead>

              <tbody>
                {docs.map((doc) => {
                  const id = doc.id || doc._id;
                  const status = (doc.status || "UPLOADED").toUpperCase();

                  return (
                    <tr key={id}>
                      <td>{doc.fileName}</td>
                      <td>{doc.category}</td>
                      <td>
                        <span className={`status-badge ${status.toLowerCase()}`}>
                          {status}
                        </span>
                      </td>
                      <td className="center">
                        {status === "TRANSCRIBED" && (
                          <button
                            className="open-btn"
                            onClick={() => navigate(`/chat/${id}`)}
                          >
                            Open
                          </button>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          )}
        </div>

      </div>
    </div>
  );
}

/* ---------- ACTION CARD ---------- */
function ActionCard({ icon, label, onClick, disabled }) {
  return (
    <button
      onClick={onClick}
      disabled={disabled}
      className={`action-card ${disabled ? "disabled" : ""}`}
    >
      <div className="action-icon">{icon}</div>
      <span>{label}</span>
    </button>
  );
}