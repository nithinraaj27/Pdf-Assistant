const pdfFile = document.getElementById("pdfFile");
const uploadBtn = document.getElementById("uploadBtn");
const uploadMessage = document.getElementById("uploadMessage");

const chatInput = document.getElementById("chatInput");
const sendBtn = document.getElementById("sendBtn");
const chatWindow = document.getElementById("chatWindow");

// NEW PROGRESS BAR ELEMENTS
const progressContainer = document.getElementById("progressContainer");
const uploadProgressBar = document.getElementById("uploadProgressBar");

// Function to simulate upload progress (as true upload progress requires server support)
function simulateProgress(duration = 2000) {
    let progress = 0;
    const interval = 50;
    const steps = duration / interval;
    const increment = 100 / steps;

    const progressSim = setInterval(() => {
        progress += increment;
        if (progress >= 95) { // Stop just before 100% to wait for actual response
            progress = 95;
            clearInterval(progressSim);
        }
        uploadProgressBar.style.width = progress + "%";
    }, interval);

    return progressSim;
}

// Upload PDF
uploadBtn.addEventListener("click", async () => {
    if (!pdfFile.files[0]) {
        alert("Select a PDF first.");
        return;
    }

    // 1. Setup UI for upload start
    uploadMessage.textContent = "Uploading...";
    progressContainer.style.display = 'block'; // Show progress bar
    uploadProgressBar.style.width = "0%";

    const progressInterval = simulateProgress(3000); // Start progress simulation

    const formData = new FormData();
    formData.append("file", pdfFile.files[0]);

    try {
        const response = await fetch("/upload-pdf", { method: "POST", body: formData });

        // 2. Stop simulation and complete progress bar
        clearInterval(progressInterval);
        uploadProgressBar.style.width = "100%"; // Set to 100% instantly

        const text = await response.text();

        if (response.ok) {
            uploadMessage.textContent = "PDF Uploaded Successfully! Ready to Chat.";
            alert("PDF Uploaded Successfully!");
        } else {
            uploadMessage.textContent = "Upload Failed: " + text;
            alert("Upload Failed: " + text);
        }

    } catch (err) {
        console.error(err);
        // 3. Handle error state
        clearInterval(progressInterval);
        uploadProgressBar.style.width = "0%"; // Reset on error
        uploadMessage.textContent = "Error connecting to server.";
        alert("Error uploading PDF");
    } finally {
        // 4. Hide progress bar after a short delay
        setTimeout(() => {
            progressContainer.style.display = 'none';
        }, 800);
    }
});

// Chat Messaging (remains unchanged)
sendBtn.addEventListener("click", sendMessage);
chatInput.addEventListener("keydown", e => e.key === "Enter" && sendMessage());

function appendMessage(sender, content) {
    const msgDiv = document.createElement("div");
    msgDiv.className = `chat-message ${sender}`;
    msgDiv.innerHTML = content;
    chatWindow.appendChild(msgDiv);
    chatWindow.scrollTop = chatWindow.scrollHeight;
}

async function sendMessage() {
    const text = chatInput.value.trim();
    if (!text) return;

    appendMessage("user", text);
    chatInput.value = "";

    // Show typing animation
    const loader = document.createElement("div");
    loader.className = "chat-message bot";
    loader.innerHTML = '<div class="typing-dots"><span></span><span></span><span></span></div>';
    chatWindow.appendChild(loader);
    chatWindow.scrollTop = chatWindow.scrollHeight;

    try {
        const response = await fetch("/api/chat", {
            method: "POST",
            headers: { "Content-Type": "text/plain" },
            body: text,
        });
        const answer = await response.text();
        loader.remove();
        appendMessage("bot", answer);
    } catch (err) {
        console.error(err);
        loader.remove();
        appendMessage("bot", "Error fetching response");
    }
}