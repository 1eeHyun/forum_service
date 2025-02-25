let stompClient = null;
let messageHistory = []; // Store chat history
let onlineUsers = new Set(); // Store users

// Check logged-in users
let userName = document.getElementById("currentUserName").textContent.trim();
if (!userName) {
  userName = "User_" + Math.floor(Math.random() * 1000);
}

// WebSocket connect setting
function connect() {
  let socket = new SockJS('/ws');
  stompClient = Stomp.over(socket);

  stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);

    // add user
    stompClient.send("/app/addUser", {}, JSON.stringify({ 'sender': userName }));

    // receive live chat messages
    stompClient.subscribe('/topic/public', function (message) {
      const parsedMessage = JSON.parse(message.body);
      showMessage(parsedMessage);
    });

    // receive live user members
    stompClient.subscribe('/topic/onlineUsers', function (userList) {
      updateOnlineUsers(JSON.parse(userList.body));
    });

    stompClient.send("/app/requestUserList", {});
  });
}

// send message
function sendMessage() {
  const messageContent = document.getElementById('messageInput').value;
  stompClient.send("/app/sendMessage", {}, JSON.stringify({ 'sender': userName, 'content': messageContent }));
  document.getElementById('messageInput').value = '';
}

// output and save chat messages
function showMessage(message) {
    messageHistory.push(message);
    const messageDiv = document.getElementById("messages");

    // create message element
    const newMessage = document.createElement("p");
    newMessage.innerHTML = `<strong style="color: black;">${message.sender}</strong>: ${message.content}`;
    messageDiv.appendChild(newMessage);

    // auto scroll -> to the last message
    messageDiv.scrollTop = messageDiv.scrollHeight;
}


// update online user list
function updateOnlineUsers(users) {
  onlineUsers = new Set(users);
  const userList = document.getElementById("userList");
  userList.innerHTML = '';
  users.forEach(user => {
    const li = document.createElement("li");
    li.textContent = user;
    userList.appendChild(li);
  });
}

// load chat history
function loadChatHistory() {
  const savedHistory = JSON.parse(localStorage.getItem('chatHistory')) || [];
  savedHistory.forEach(message => {
    showMessage(message);
  });
}

// handle removing user
window.addEventListener("beforeunload", function () {
  if (stompClient && stompClient.connected) {
    stompClient.send("/app/removeUser", {}, JSON.stringify({ 'sender': userName }));
  }
  localStorage.setItem('chatHistory', JSON.stringify(messageHistory));
});

// load chat and start connecting
window.onload = function () {
  loadChatHistory();
  connect();
};
