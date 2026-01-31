import { useParams, Navigate } from "react-router-dom";
import ChatBox from "../components/ChatBox";
import "./ChatPage.css";

export default function ChatPage() {
  const { documentId } = useParams();

  if (!documentId) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <div className="chat-root">
      <div className="chat-container">

        {/* HEADER */}
        <h1 className="chat-title">
          Chat with Document
        </h1>

        {/* CHAT BOX */}
        <div className="chat-box-wrapper">
          <ChatBox documentId={documentId} />
        </div>

      </div>
    </div>
  );
}
