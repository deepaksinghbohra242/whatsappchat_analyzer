let selectedFile = null;

// File input handling
document.getElementById("fileInput").addEventListener("change", function (e) {
  const file = e.target.files[0];
  if (file && file.name.endsWith(".txt")) {
    selectedFile = file;
    showFileInfo(file);
  } else {
    showError("Please select a valid .txt file");
  }
});

// Drag and drop functionality
const uploadSection = document.getElementById("uploadSection");

uploadSection.addEventListener("dragover", function (e) {
  e.preventDefault();
  uploadSection.classList.add("dragover");
});

uploadSection.addEventListener("dragleave", function (e) {
  e.preventDefault();
  uploadSection.classList.remove("dragover");
});

uploadSection.addEventListener("drop", function (e) {
  e.preventDefault();
  uploadSection.classList.remove("dragover");

  const files = e.dataTransfer.files;
  if (files.length > 0) {
    const file = files[0];
    if (file.name.endsWith(".txt")) {
      selectedFile = file;
      document.getElementById("fileInput").files = files;
      showFileInfo(file);
    } else {
      showError("Please select a valid .txt file");
    }
  }
});

function showFileInfo(file) {
  document.getElementById("fileName").textContent = file.name;
  document.getElementById("fileSize").textContent = formatFileSize(file.size);
  document.getElementById("fileInfo").style.display = "block";
  hideError();
}

function formatFileSize(bytes) {
  if (bytes === 0) return "0 Bytes";
  const k = 1024;
  const sizes = ["Bytes", "KB", "MB", "GB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
}

function showError(message) {
  const errorDiv = document.getElementById("error");
  errorDiv.textContent = message;
  errorDiv.style.display = "block";
}

function hideError() {
  document.getElementById("error").style.display = "none";
}

function showLoading() {
  document.getElementById("loading").style.display = "block";
  document.getElementById("results").style.display = "none";
}

function hideLoading() {
  document.getElementById("loading").style.display = "none";
}

async function analyzeChat() {
  if (!selectedFile) {
    showError("Please select a file first");
    return;
  }

  showLoading();
  hideError();

  try {
    const formData = new FormData();
    formData.append("chatFile", selectedFile);

    const response = await fetch("http://localhost:8080/api/analyze", {
      method: "POST",
      body: formData,
    });

    if (!response.ok) {
      throw new Error(`Server error: ${response.status}`);
    }

    const data = await response.json();
    displayResults(data);
  } catch (error) {
    console.error("Error:", error);
    showError("Error analyzing chat: " + error.message);
  } finally {
    hideLoading();
  }
}

function displayResults(data) {
  // Update basic stats
  document.getElementById("totalMessages").textContent =
    data.totalMessages || 0;
  document.getElementById("mostActiveUser").textContent =
    data.mostActiveUser || "-";
  document.getElementById("mostActiveCount").textContent =
    data.mostActiveUserCount || 0;
  document.getElementById("totalWords").textContent = data.totalWords || 0;
  document.getElementById("mediaMessages").textContent =
    data.mediaMessages || 0;

  // Update user list
  const userList = document.getElementById("userList");
  userList.innerHTML = "";
  if (data.userMessageCounts) {
    Object.entries(data.userMessageCounts)
      .sort((a, b) => b[1] - a[1])
      .forEach(([user, count]) => {
        const li = document.createElement("li");
        li.className = "user-item";
        li.innerHTML = `
                            <span class="user-name">${user}</span>
                            <span class="user-count">${count}</span>
                        `;
        userList.appendChild(li);
      });
  }

  // Update emoji grid
  const emojiGrid = document.getElementById("emojiGrid");
  emojiGrid.innerHTML = "";
  if (data.topEmojis) {
    data.topEmojis.slice(0, 12).forEach(([emoji, count]) => {
      const div = document.createElement("div");
      div.className = "emoji-item";
      div.innerHTML = `
                        <div class="emoji">${emoji}</div>
                        <div class="emoji-count">${count}</div>
                    `;
      emojiGrid.appendChild(div);
    });
  }

  // Update timeline chart
  const timelineChart = document.getElementById("timelineChart");
  timelineChart.innerHTML = "";
  if (data.timeline) {
    const maxCount = Math.max(...Object.values(data.timeline));
    Object.entries(data.timeline).forEach(([date, count]) => {
      const bar = document.createElement("div");
      bar.className = "timeline-bar";
      const height = (count / maxCount) * 100;
      bar.style.height = `${height}%`;
      bar.title = `${date}: ${count} messages`;
      timelineChart.appendChild(bar);
    });
  }

  // Update word cloud
  const wordCloud = document.getElementById("wordCloud");
  wordCloud.innerHTML = "";
  if (data.topWords) {
    data.topWords.slice(0, 20).forEach(([word, count]) => {
      const span = document.createElement("span");
      span.className = "word-item";
      span.textContent = `${word} (${count})`;
      span.style.fontSize = `${Math.min(
        1.5,
        0.8 + (count / data.topWords[0][1]) * 0.7
      )}rem`;
      wordCloud.appendChild(span);
    });
  }

  document.getElementById("results").style.display = "block";
}
