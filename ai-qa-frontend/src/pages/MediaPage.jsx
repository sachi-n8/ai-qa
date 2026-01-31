import { useParams, useSearchParams, Navigate } from "react-router-dom";
import MediaPlayer from "../components/MediaPlayer";
import "./MediaPage.css";

export default function MediaPage() {
  const { documentId } = useParams();
  const [params] = useSearchParams();
  const start = params.get("start") || 0;

  if (!documentId) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <div className="media-root">
      <div className="media-container">

        <h2 className="media-title">Media Player</h2>

        {/* PLAYER */}
        <div className="media-aspect">
          <MediaPlayer documentId={documentId} start={start} />
        </div>

      </div>
    </div>
  );
}
