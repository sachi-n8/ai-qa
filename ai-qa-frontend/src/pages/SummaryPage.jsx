import { useParams, Navigate } from "react-router-dom";
import { useEffect, useState } from "react";
import api from "../api/api";
import "./SummaryPage.css";

export default function SummaryPage() {
  const { documentId } = useParams();
  const [summary, setSummary] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!documentId) return;

    api
      .get(`/summary/${documentId}`)
      .then((res) => setSummary(res.data.summary))
      .catch(() => setSummary("Summary not available"))
      .finally(() => setLoading(false));
  }, [documentId]);

  if (!documentId) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <div className="summary-root">
      <div className="summary-container">

        {/* HEADER */}
        <h1 className="summary-title">
          Document Summary
        </h1>

        {/* CONTENT */}
        {loading ? (
          <p className="summary-loading">
            Loading summary...
          </p>
        ) : (
          <div className="summary-card">
            {summary || "No summary generated yet"}
          </div>
        )}

      </div>
    </div>
  );
}
