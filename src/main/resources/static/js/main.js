'use strict';

function enterAnnouncement(event) {
    username = document.querySelector('#username').value.trim();

    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

function onConnected() {
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
    stompClient.subscribe(`/topic/public`, onMessageReceived);

    // register the connected user
    stompClient.send("/app/user.addUser",
        {},
        JSON.stringify({nickName: nickname, fullName: fullname, status: 'ONLINE'})
    );
    document.querySelector('#connected-user-fullname').textContent = fullname;
    findAndDisplayConnectedUsers().then();
}

document.addEventListener('DOMContentLoaded', function () {
    const chatButton = document.getElementById('chatButton');
    const logoutButton = document.getElementById('logoutButton');

    if (chatButton) {
        chatButton.addEventListener('click', () => {
            window.location.href = '/chat';
        });
    }

    if (logoutButton) {
        logoutButton.addEventListener('click', () => {
            fetch('/logout', { method: 'POST' })
                .then(() => window.location.href = '/');
        });
    }

});

