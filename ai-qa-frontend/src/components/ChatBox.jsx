import { useState } from "react";
import api from "../api/api";
import "./ChatBox.css";

export default function ChatBox({ documentId }) {
  const [question, setQuestion] = useState("");
  const [answer, setAnswer] = useState("");
  const [loading, setLoading] = useState(false);

  const ask = async () => {
    if (!question || !documentId) return;

    setLoading(true);
    setAnswer("");

    try {
      const res = await api.post("/chat", {
        documentId,
        question,
      });

      setAnswer(res.data.answer);
    } catch (err) {
      setAnswer(
        "Document is still being processed. Please wait."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="chatbox-root">

      {/* INPUT ROW */}
      <div className="chatbox-input-row">
        <input
          className="chatbox-input"
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          placeholder="Ask a question about the document..."
        />

        <button
          className="chatbox-btn"
          onClick={ask}
          disabled={loading}
        >
          {loading ? "Asking..." : "Ask"}
        </button>
      </div>

      {/* ANSWER */}
      {answer && (
        <div className="chatbox-answer">
          {answer}
        </div>
      )}
    </div>
  );
}
