import { useState } from "react";
import api from "../api/api";
import { useNavigate } from "react-router-dom";

export default function TimestampList({ documentId }) {
  const [topic, setTopic] = useState("");
  const [timestamps, setTimestamps] = useState(null);
  const navigate = useNavigate();

  const extract = async () => {
    const res = await api.post("/timestamp", {
      documentId,
      question: topic
    });
    setTimestamps(res.data);
  };

  return (
    <div>
      <h3>Find timestamp in media</h3>
      <input
        placeholder="Topic (e.g. firewall rules)"
        value={topic}
        onChange={e => setTopic(e.target.value)}
      />
      <button onClick={extract}>Find</button>

      {timestamps && (
        <div>
          <p>
            Start: {timestamps.start.toFixed(2)} sec | End:{" "}
            {timestamps.end.toFixed(2)} sec
          </p>
          <button
            onClick={() =>
              navigate(`/media/${documentId}?start=${timestamps.start}`)
            }
          >
            Play Segment
          </button>
        </div>
      )}
    </div>
  );
}
