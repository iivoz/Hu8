const socket = io();

let currentGroup = null;

function googleLogin() {
    alert('تسجيل دخول باستخدام جوجل');
    showChatPage();
}

function manualLogin() {
    alert('تسجيل دخول يدوي');
    showChatPage();
}

function showChatPage() {
    document.getElementById('login-container').style.display = 'none';
    document.getElementById('chat-container').style.display = 'block';
    document.getElementById('groups-container').style.display = 'block';
    document.getElementById('messages').innerHTML = '';
}

function sendMessage() {
    const message = document.getElementById('messageInput').value;
    if (message.trim() !== '') {
        socket.emit('sendMessage', message);
        document.getElementById('messageInput').value = '';
    }
}

function sendImage() {
    const imageInput = document.getElementById('imageInput');
    const file = imageInput.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            socket.emit('sendImage', e.target.result);
        };
        reader.readAsDataURL(file);
    }
}

function logout() {
    document.getElementById('login-container').style.display = 'block';
    document.getElementById('chat-container').style.display = 'none';
    document.getElementById('groups-container').style.display = 'none';
    socket.emit('logout');
}

function createGroup() {
    const groupName = prompt('أدخل اسم المجموعة');
    if (groupName) {
        socket.emit('createGroup', groupName);
    }
}

function showGroupChat(groupName) {
    document.getElementById('groups-container').style.display = 'none';
    document.getElementById('group-chat-container').style.display = 'block';
    document.getElementById('groupName').textContent = groupName;
    currentGroup = groupName;
}

function exitGroup() {
    document.getElementById('groups-container').style.display = 'block';
    document.getElementById('group-chat-container').style.display = 'none';
    currentGroup = null;
}

function sendGroupMessage() {
    const message = document.getElementById('groupMessageInput').value;
    if (message.trim() !== '' && currentGroup) {
        socket.emit('sendGroupMessage', { group: currentGroup, message });
        document.getElementById('groupMessageInput').value = '';
    }
}

function sendGroupImage() {
    const imageInput = document.getElementById('groupImageInput');
    const file = imageInput.files[0];
    if (file && currentGroup) {
        const reader = new FileReader();
        reader.onload = function (e) {
            socket.emit('sendGroupImage', { group: currentGroup, image: e.target.result });
        };
        reader.readAsDataURL(file);
    }
}

socket.on('receiveMessage', (message) => {
    const messageElement = document.createElement('div');
    messageElement.textContent = message;
    document.getElementById('messages').appendChild(messageElement);
});

socket.on('receiveImage', (image) => {
    const imageElement = document.createElement('img');
    imageElement.src = image;
    document.getElementById('messages').appendChild(imageElement);
});

socket.on('groupList', (groups) => {
    const groupListContainer = document.getElementById('groupList');
    groupListContainer.innerHTML = '';
    groups.forEach(group => {
        const groupButton = document.createElement('button');
        groupButton.textContent = group;
        groupButton.onclick = () => showGroupChat(group);
        groupListContainer.appendChild(groupButton);
    });
});

socket.on('receiveGroupMessage', (data) => {
    const groupChatBody = document.getElementById('group-chat-body');
    const messageElement = document.createElement('div');
    messageElement.textContent = `${data.user}: ${data.message}`;
    groupChatBody.appendChild(messageElement);
    groupChatBody.scrollTop = groupChatBody.scrollHeight;
});

socket.on('receiveGroupImage', (data) => {
    const groupChatBody = document.getElementById('group-chat-body');
    const imageElement = document.createElement('img');
    imageElement.src = data.image;
    groupChatBody.appendChild(imageElement);
    groupChatBody.scrollTop = groupChatBody.scrollHeight;
});
