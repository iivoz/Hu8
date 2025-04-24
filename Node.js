const express = require('express');
const http = require('http');
const socketIo = require('socket.io');

const app = express();
const server = http.createServer(app);
const io = socketIo(server);

let onlineUsers = [];
let groups = {};

app.use(express.static('public'));

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/public/index.html');
});

io.on('connection', (socket) => {
    console.log('مستخدم متصل');
    onlineUsers.push(socket.id);
    io.emit('onlineUsers', onlineUsers);

    socket.on('sendMessage', (message) => {
        io.emit('receiveMessage', message);
    });

    socket.on('sendImage', (image) => {
        io.emit('receiveImage', image);
    });

    socket.on('createGroup', (groupName) => {
        if (!groups[groupName]) {
            groups[groupName] = [];
        }
        io.emit('groupList', Object.keys(groups));
    });

    socket.on('sendGroupMessage', (data) => {
        const group = groups[data.group];
        if (group) {
            group.forEach(socketId => {
                io.to(socketId).emit('receiveGroupMessage', { user: socket.id, message: data.message });
            });
        }
    });

    socket.on('sendGroupImage', (data) => {
        const group = groups[data.group];
        if (group) {
            group.forEach(socketId => {
                io.to(socketId).emit('receiveGroupImage', { image: data.image });
            });
        }
    });

    socket.on('logout', () => {
        onlineUsers = onlineUsers.filter(user => user !== socket.id);
        io.emit('onlineUsers', onlineUsers);
    });

    socket.on('disconnect', () => {
        console.log('مستخدم غير متصل');
        onlineUsers = onlineUsers.filter(user => user !== socket.id);
        io.emit('onlineUsers', onlineUsers);
    });
});

server.listen(3000, () => {
    console.log('الخادم يعمل على المنفذ 3000');
});
