import { useEffect, useRef } from "react";

export default function MediaPlayer({ documentId, start }) {
  const ref = useRef();

  useEffect(() => {
    if (ref.current) {
      ref.current.currentTime = Number(start);
    }
  }, [start]);

  return (
    <video
      ref={ref}
      controls
      width="600"
      src={`http://localhost:8080/api/v1/media/${documentId}`}
    />
  );
}
