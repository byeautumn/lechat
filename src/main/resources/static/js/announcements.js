document.addEventListener('DOMContentLoaded', function () {
    fetch('/api/user/role')
        .then(response => response.text())
        .then(role => {
            document.getElementById('userRole').value = role;
            console.log("User Role From API:", role);
            runAnnouncementScript();
        })
        .catch(error => console.error('Error fetching user role:', error));
});

function runAnnouncementScript() {
    const announcementList = document.getElementById('announcementList');
    const announcementForm = document.getElementById('announcementForm');
    const announcementText = document.getElementById('announcementText');
    const postAnnouncementButton = document.getElementById('postAnnouncement');
    const userRole = document.getElementById('userRole').value;
    const notificationSound = new Audio('/sound/notification.mp3');
    const chatButtonsDiv = document.getElementById('chatButtons');

    if (!announcementList || !userRole || !announcementForm) {
        console.error("Required elements not found.");
        return;
    }

    if (userRole === 'TEACHER') {
        announcementForm.style.display = '';
        if (!announcementText || !postAnnouncementButton) {
            console.error("Announcement form elements not found (teacher).");
            return;
        }
        const chatButton = document.createElement('button');
        chatButton.textContent = "Teacher Chat";
        chatButton.addEventListener('click', () => {
            window.location.href = '/teacherchat';
        });
        chatButtonsDiv.appendChild(chatButton);
    } else if (userRole === 'STUDENT') {
        announcementForm.style.display = 'none';
        const chatButton = document.createElement('button');
        chatButton.textContent = "Chat with Teacher";
        chatButton.addEventListener('click', () => {
            window.location.href = '/chat';
        });
        chatButtonsDiv.appendChild(chatButton);
    }

    const stompClient = Stomp.over(new SockJS('/ws'));

    const connectCallback = function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/announcements', function (announcements) {
            displayAnnouncements(JSON.parse(announcements.body));
            notificationSound.play();
        });
        fetchAnnouncements();
    };

    const errorCallback = function (error) {
        console.error('STOMP error', error);
        setTimeout(connect, 5000); // Reconnect after 5 seconds
    };

    const connect = function () {
        stompClient.connect({}, connectCallback, errorCallback);
    };

    connect();

    function displayAnnouncements(announcements) {
        announcementList.innerHTML = '';
        announcements.forEach(announcement => {
            const div = document.createElement('div');
            div.classList.add('announcement');

            const textDiv = document.createElement('div');
            textDiv.classList.add('announcement-text');
            textDiv.textContent = announcement.text;

            const metaDiv = document.createElement('div');
            metaDiv.classList.add('announcement-meta');
            const timestamp = new Date(announcement.timestamp);
            const formattedTimestamp = timestamp.toLocaleString();
            metaDiv.innerHTML = `Posted by ${announcement.username}<br>${formattedTimestamp}`;

            div.appendChild(textDiv);
            div.appendChild(metaDiv);

            announcementList.appendChild(div);

            if (userRole === 'TEACHER') {
                const deleteButton = document.createElement('button');
                deleteButton.textContent = "Delete";
                deleteButton.addEventListener('click', () => {
                    if (confirm("Are you sure you want to delete this announcement?")) {
                        deleteAnnouncement(announcement.id);
                    }
                });
                div.appendChild(deleteButton);
            }
        });
    }

    function fetchAnnouncements() {
        fetch('/api/announcements')
            .then(response => response.json())
            .then(announcements => {
                displayAnnouncements(announcements);
            })
            .catch(error => console.error('Error fetching announcements:', error));
    }

    function postAnnouncement() {
        const text = announcementText.value.trim();
        if (text) {
            fetch('/api/user/role')
                .then(response => response.text())
                .then(username => {
                    fetch('/api/announcements', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ text: text, username: username })
                    })
                        .then(() => {
                            announcementText.value = '';
                        })
                        .catch(error => console.error('Error posting announcement:', error));
                });
        }
    }

    function deleteAnnouncement(id) {
        fetch(`/api/announcements/${id}`, { method: 'DELETE' })
            .catch(error => console.error('Error deleting announcement:', error));
    }

    if (userRole === 'TEACHER') {
        postAnnouncementButton.addEventListener('click', postAnnouncement);
    }
}