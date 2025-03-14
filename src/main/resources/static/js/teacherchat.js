// Global Variables
const messagesDiv = document.getElementById('messages');
const messageInput = document.getElementById('messageInput');
const sendButton = document.getElementById('sendButton');
const usersListDiv = document.getElementById('usersList');
const refreshUsersButton = document.getElementById('refreshUsers');
const recipientInput = document.getElementById('recipientInput');
const stompClient = Stomp.over(new SockJS('/ws'));
const chatArea = document.getElementById('chat-messages');

let username;
let selectedUserId;
let chatHistories = {};

// Functions
function displayUsers(users) {
    console.log("displayUsers is being called ...");
    usersListDiv.innerHTML = '';
    users.forEach(user => {
        const listItem = document.createElement('li');
        listItem.classList.add('user-item');
        const userDiv = document.createElement('div');
        userDiv.textContent = user.username;
        userDiv.style.cursor = 'pointer';
        userDiv.addEventListener('click', (event) => {
            recipientInput.value = user.username;
            const clickedUser = event.currentTarget;
            clickedUser.classList.add('active');
            selectedUserId = user.username;
            fetchAndDisplayUserChat().then();
        });
        const userImage = document.createElement('img');
        userImage.src = '../img/user_icon.png';
        userImage.alt = user.username;

        listItem.appendChild(userDiv);
        listItem.appendChild(userImage);
        usersListDiv.appendChild(listItem);
    });
}

function fetchUsers() {
    console.log("fetchUsers is being called ...");
    fetch('/api/users')
        .then(response => response.json())
        .then(users => {
            displayUsers(users);
        })
        .catch(error => console.error('Error fetching users:', error));
}

function displayMessage(senderId, content) {
    console.log("displayMessage is being called ...");
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');
    if (senderId === username) {
        messageContainer.classList.add('sender');
    } else {
        messageContainer.classList.add('receiver');
    }
    const message = document.createElement('p');
    message.textContent = content;
    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);
}

function updateChatArea() {
    console.log("updateChatArea is being called ...");
    chatArea.innerHTML = '';
    if (selectedUserId && chatHistories[selectedUserId]) {
        chatHistories[selectedUserId].forEach(message => {
            displayMessage(message.senderId, message.content);
        });
        chatArea.scrollTop = chatArea.scrollHeight;
        messagesDiv.scrollTop = messagesDiv.scrollHeight;
    }
}

async function fetchAndDisplayUserChat() {
    console.log("fetchAndDisplayUserChat is being called ...");
    const selectedUser = recipientInput.value;
    const userChatResponse = await fetch(`/messages/${username}/${selectedUser}`);
    const userChat = await userChatResponse.json();
    chatHistories[selectedUser] = userChat;
    selectedUserId = selectedUser;
    updateChatArea();
}

async function onMessageReceived(payload) {
    console.log("onMessageReceived is being called ...");
    console.log('payload:', payload);
    const message = JSON.parse(payload.body);
    console.log('Message:', message);

    const senderId = message.senderId;
    const recipientId = message.recipientId;

console.log('senderId: ', senderId);
console.log('recipientId: ', recipientId);
console.log('selectedUserId: ', selectedUserId);

    let chatToUpdate = null;
    if (senderId === selectedUserId && recipientId === username) {
        chatToUpdate = selectedUserId;
    }

console.log('chatToUpdate: ', chatToUpdate);
    if (chatToUpdate) {
        if (!chatHistories[chatToUpdate]) {
            chatHistories[chatToUpdate] = [];
        }
        console.log('chatHistories[chatToUpdate]: ', chatHistories[chatToUpdate]);

        chatHistories[chatToUpdate].push(message);
        updateChatArea();
    }
    const notificationSound = document.getElementById('notificationSound');
    notificationSound.play();
}

// Event Listeners and Initialization
document.addEventListener('DOMContentLoaded', function () {
    refreshUsersButton.addEventListener('click', fetchUsers);
    fetchUsers();

    fetch('/api/user/username')
        .then(response => response.text())
        .then(user => {
            username = user;
            stompClient.connect({}, function (frame) {
                console.log('Connected: ' + frame);
                stompClient.subscribe('/user/{username}/queue/messages', onMessageReceived);
            });
        })
        .catch(error => console.error('Error fetching username:', error));

    sendButton.addEventListener('click', sendMessage);

    // Add event listeners for logout and announcements buttons
        document.getElementById('logoutButton').addEventListener('click', function () {
            window.location.href = '/';
        });

        document.getElementById('announcementsButton').addEventListener('click', function () {
            window.location.href = '/announcements';
        });
});

function sendMessage(event) {
    console.log("sendMessage is being called ...");
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        const chatMessage = {
            senderId: username,
            recipientId: recipientInput.value,
            content: messageContent,
            timestamp: new Date()
        };
        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        displayMessage(username, messageContent);

        if (!chatHistories[recipientInput.value]) {
            chatHistories[recipientInput.value] = [];
        }
        chatHistories[recipientInput.value].push(chatMessage);
        updateChatArea();

        messageInput.value = '';
    }
    chatArea.scrollTop = chatArea.scrollHeight;
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
    event.preventDefault();

    const notificationSound = document.getElementById('notificationSound');
    notificationSound.play();
}