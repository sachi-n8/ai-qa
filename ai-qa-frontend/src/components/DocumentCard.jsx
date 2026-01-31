import api from "../api/api";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function DocumentCard({ doc, highlight = false }) {
  const navigate = useNavigate();

  // normalize id
  const documentId = doc.id || doc._id;

  const [status, setStatus] = useState(doc.status);
  const [loading, setLoading] = useState(false);

  const transcribe = async () => {
    if (loading) return; // 🚫 prevent double click

    try {
      setLoading(true);
      setStatus("TRANSCRIBING");

      await api.post(`/transcribe/${documentId}`);

      // optional small delay for better UX
      setTimeout(() => {
        setStatus("TRANSCRIBED");
        setLoading(false);
      }, 800);
    } catch (err) {
      console.error("Transcription failed", err);
      setStatus("FAILED");
      setLoading(false);
    }
  };

  return (
    <div
      className={`p-4 rounded-xl border transition ${
        highlight ? "border-indigo-500 bg-indigo-50" : "border-gray-300"
      }`}
    >
      <h4 className="font-semibold truncate">{doc.fileName}</h4>

      <p>
        Category: <b>{doc.category}</b>
      </p>

      <p>
        Status:{" "}
        <b
          className={
            status === "FAILED"
              ? "text-red-600"
              : status === "TRANSCRIBING"
              ? "text-indigo-600"
              : "text-green-600"
          }
        >
          {status}
        </b>
      </p>

      <div className="mt-4 flex gap-2 flex-wrap">
        {/* TRANSCRIBE */}
        {status === "UPLOADED" && (
          <button
            onClick={transcribe}
            disabled={loading}
            className="px-3 py-1 rounded bg-indigo-600 text-white disabled:opacity-60"
          >
            {loading ? "🎙️ Transcribing..." : "🎙️ Transcribe"}
          </button>
        )}

        {/* ACTIONS AFTER TRANSCRIBE */}
        {status === "TRANSCRIBED" && (
          <>
            <button
              onClick={() => navigate(`/chat/${documentId}`)}
              className="px-3 py-1 rounded bg-green-600 text-white"
            >
              💬 Chat
            </button>

            {doc.category === "MEDIA" && (
              <button
                onClick={() => navigate(`/media/${documentId}`)}
                className="px-3 py-1 rounded bg-purple-600 text-white"
              >
                🎧 Media
              </button>
            )}

            <button
              onClick={() => navigate(`/summary/${documentId}`)}
              className="px-3 py-1 rounded bg-yellow-500 text-white"
            >
              🧠 Summary
            </button>
          </>
        )}

        {/* FAILED STATE */}
        {status === "FAILED" && (
          <button
            onClick={transcribe}
            className="px-3 py-1 rounded bg-red-600 text-white"
          >
            🔁 Retry
          </button>
        )}
      </div>
    </div>
  );
}