import { useState } from "react";
import api from "../api/api";
import { UploadCloud, File, Loader2 } from "lucide-react";
import { useNavigate } from "react-router-dom";

export default function UploadPage() {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const handleFileChange = (e) => {
    if (e.target.files?.[0]) {
      setFile(e.target.files[0]);
      setMessage("");
    }
  };

  const handleUpload = async () => {
    if (!file) return;

    setLoading(true);
    setMessage("");

    const formData = new FormData();
    formData.append("file", file); // MUST be "file"

    try {
      await api.post("/upload", formData); // ❌ NO headers
      setMessage("File uploaded successfully!");
      setFile(null);

      // optional: go back to dashboard
      setTimeout(() => navigate("/dashboard"), 1000);
    } catch (err) {
      console.error("Upload failed:", err);
      setMessage("Upload failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen p-6 flex items-center justify-center">
      <div className="w-full max-w-lg">
        <div className="bg-white p-10 rounded-2xl shadow-xl">
          <h2 className="text-3xl font-bold mb-8 text-center">
            Upload Your File
          </h2>

          <div className="space-y-6">
            <label className="flex flex-col items-center justify-center w-full h-64 border-2 border-dashed rounded-xl cursor-pointer">
              <div className="flex flex-col items-center justify-center">
                {file ? (
                  <>
                    <File className="w-12 h-12 mb-4" />
                    <p className="font-medium">{file.name}</p>
                    <p className="text-sm text-gray-500">
                      {(file.size / 1024 / 1024).toFixed(2)} MB
                    </p>
                  </>
                ) : (
                  <>
                    <UploadCloud className="w-12 h-12 mb-4" />
                    <p className="font-medium">
                      Click to upload or drag & drop
                    </p>
                    <p className="text-sm text-gray-500">
                      PDF, MP3, WAV, MP4, MOV
                    </p>
                  </>
                )}
              </div>

              <input
                type="file"
                className="hidden"
                onChange={handleFileChange}
                accept=".pdf,.mp3,.wav,.m4a,.mp4,.mov"
              />
            </label>

            {message && (
              <p
                className={`text-center font-medium ${
                  message.includes("success")
                    ? "text-green-600"
                    : "text-red-600"
                }`}
              >
                {message}
              </p>
            )}

            <button
              onClick={handleUpload}
              disabled={!file || loading}
              className="w-full py-3 bg-black text-white rounded-lg flex items-center justify-center gap-2 disabled:opacity-50"
            >
              {loading && <Loader2 className="h-5 w-5 animate-spin" />}
              {loading ? "Uploading..." : "Upload File"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
