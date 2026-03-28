const fileInput = document.getElementById('file-upload');
const chatOutput = document.getElementById('chat-output');
const userInput = document.getElementById('user-input');
const sendBtn = document.getElementById('send-btn');
const btnIcon = document.getElementById('btn-icon');

let abortController = null;

const ICONS = {
    send: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M7 11L12 6L17 11M12 18V7"/></svg>`,
    stop: `<svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><rect x="6" y="6" width="12" height="12" rx="2"/></svg>`
};

// --- CHAT LOGIC ---

const handleChat = async () => {
    // 1. Check if we should ABORT
    if (sendBtn.classList.contains('stop-mode')) {
        if (abortController) abortController.abort();
        resetUI();
        return;
    }

    const msg = userInput.value.trim();
    if (!msg) return;

    // Remove welcome screen on first message
    const welcome = document.querySelector('.welcome-screen');
    if (welcome) welcome.remove();

    appendMessage('user', msg);
    userInput.value = '';
    updateBtnState();

    // 2. Switch to STOP mode
    sendBtn.classList.add('stop-mode');
    btnIcon.innerHTML = ICONS.stop;

    const loaderId = 'loader-' + Date.now();
    appendMessage('bot', 'Analyzing documents...', loaderId);
    document.getElementById(loaderId).classList.add('thinking');

    abortController = new AbortController();

    try {
        const res = await fetch('http://localhost:8080/api/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ sessionId: "user1", message: msg }),
            signal: abortController.signal
        });

        const data = await res.json();
        document.getElementById(loaderId)?.remove();
        appendMessage('bot', data.response);

    } catch (err) {
        if (err.name === 'AbortError') {
            const loader = document.getElementById(loaderId);
            if (loader) loader.innerText = "Response stopped.";
        } else {
            document.getElementById(loaderId).innerText = "Connection error. Please try again.";
        }
    } finally {
        resetUI();
    }
};

function resetUI() {
    sendBtn.classList.remove('stop-mode');
    btnIcon.innerHTML = ICONS.send;
    updateBtnState();
}

function updateBtnState() {
    const hasText = userInput.value.trim().length > 0;
    sendBtn.disabled = !hasText;
    hasText ? sendBtn.classList.add('active') : sendBtn.classList.remove('active');
}

function appendMessage(role, text, id = null) {
    let container = chatOutput.querySelector('.msg-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'msg-container';
        chatOutput.appendChild(container);
    }

    const div = document.createElement('div');
    div.className = `msg ${role}`;
    if (id) div.id = id;
    div.innerText = text;
    container.appendChild(div);
    chatOutput.scrollTop = chatOutput.scrollHeight;
}

// --- FILE UPLOAD LOGIC ---

fileInput.addEventListener('change', () => {
    const file = fileInput.files[0];
    if (!file) return;

    const card = document.getElementById('upload-status');
    const fill = document.getElementById('bar-fill');
    const percentLabel = document.getElementById('percent');
    const nameLabel = document.getElementById('file-name-uploading');

    nameLabel.innerText = file.name;
    card.style.display = 'block';
    fill.style.width = '0%';

    const formData = new FormData();
    formData.append("file", file);

    const xhr = new XMLHttpRequest();
    xhr.upload.onprogress = (e) => {
        if (e.lengthComputable) {
            const p = Math.round((e.loaded / e.total) * 100);
            fill.style.width = p + "%";
            percentLabel.innerText = p + "%";
        }
    };

    xhr.onload = () => {
        if (xhr.status === 200 || xhr.status === 201) {
            document.getElementById('status-text').innerText = "Sync complete";
            addFileToList(file.name);
            setTimeout(() => { card.style.display = 'none'; }, 2000);
        }
        fileInput.value = '';
    };

    xhr.open("POST", "http://localhost:8080/api/pdf/upload");
    xhr.send(formData);
});

function addFileToList(name) {
    const div = document.createElement('div');
    div.className = 'file-item';
    div.innerHTML = `<span>${name}</span><button onclick="this.parentElement.remove()" style="border:none;background:none;color:#ff453a;cursor:pointer;font-size:16px;">&times;</button>`;
    document.getElementById('file-list').appendChild(div);
}

userInput.addEventListener('input', updateBtnState);
sendBtn.addEventListener('click', handleChat);
userInput.addEventListener('keypress', (e) => { if(e.key === 'Enter') handleChat(); });