import { useEffect, useState } from "react";
import api from "../api/api";

export default function DocumentList() {
  const [docs, setDocs] = useState([]);

  useEffect(() => {
    api.get("/documents").then(res => setDocs(res.data));
  }, []);

  return (
    <ul>
      {docs.map(doc => (
        <li key={doc.id}>
          <b>{doc.fileName}</b><br/>
          <small>{doc.content}</small>
        </li>
      ))}
    </ul>
  );
}
