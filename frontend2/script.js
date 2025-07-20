const API_BASE = "http://localhost:8080/api";

function uploadFile() {
  const fileInput = document.getElementById("fileInput");
  const file = fileInput.files[0];

  if (!file) {
    showResult("Please select a file first", "error");
    return;
  }

  const formData = new FormData();
  formData.append("chatFile", file);

  fetch(`${API_BASE}/analyze`, {
    method: "POST",
    body: formData,
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.error) {
        showResult(`Error: ${data.error}`, "error");
      } else {
        showResult(JSON.stringify(data, null, 2), "success");
      }
    })
    .catch((error) => {
      showResult(`Network error: ${error.message}`, "error");
    });
}

function analyzeText() {
  const textInput = document.getElementById("textInput");
  const content = textInput.value.trim();

  if (!content) {
    showResult("Please enter some chat content first", "error");
    return;
  }

  fetch(`${API_BASE}/analyze/text`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ content: content }),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.error) {
        showResult(`Error: ${data.error}`, "error");
      } else {
        showResult(JSON.stringify(data, null, 2), "success");
      }
    })
    .catch((error) => {
      showResult(`Network error: ${error.message}`, "error");
    });
}

function showResult(message, type) {
  const resultDiv = document.getElementById("result");
  resultDiv.textContent = message;
  resultDiv.className = `result ${type}`;
  resultDiv.style.display = "block";
}

// Test connection on page load
fetch(`${API_BASE}/health`)
  .then((response) => response.json())
  .then((data) => {
    console.log("API Health Check:", data);
  })
  .catch((error) => {
    console.error("API connection failed:", error);
  });
