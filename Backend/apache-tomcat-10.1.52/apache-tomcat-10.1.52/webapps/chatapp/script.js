// ----------------------------------------------script for login and registration page-----------------------------------------------

//number validation for login form
function validatePhone() {
  const phoneInput = document.getElementById("phone");
  const phoneError =
    document.getElementById("phoneError") ||
    document.getElementById("regphoneError");

  if (!phoneInput || !phoneError) return true;

  const phone = phoneInput.value.trim();

  phoneError.innerText = "";

  if (phone.length !== 10 || isNaN(phone)) {
    phoneError.innerText = "Phone number must be 10 digits";
    return false;
  }

  return true;
}

// read URL parameters
const params = new URLSearchParams(window.location.search);

const error = params.get("error");
const regerror = params.get("regerror");

// LOGIN PAGE ERRORS
const userError = document.getElementById("userError");
const phoneError = document.getElementById("phoneError");
const passError = document.getElementById("passError");

if (userError) userError.innerText = "";
if (phoneError) phoneError.innerText = "";
if (passError) passError.innerText = "";

if (error === "user" && userError) {
  userError.innerText = "User not found";
}

if (error === "phone" && phoneError) {
  phoneError.innerText = "Phone number does not match";
}

if (error === "password" && passError) {
  passError.innerText = "Wrong password";
}

// REGISTRATION PAGE ERROR
const regPhoneError = document.getElementById("regphoneError");

if (regerror === "phone" && regPhoneError) {
  regPhoneError.innerText = "Phone number is already registered";
}

// password confirmation validation for registration form

function checkPassword() {
  let pass = document.getElementById("password").value;
  let confirm = document.getElementById("confirmPassword").value;

  // clear old messages
  document.getElementById("confirmPassError").innerText = "";

  if (pass !== confirm) {
    document.getElementById("confirmPassError").innerText =
      "Passwords do not match";
    return false;
  }

  return true;
}

// ----------------------------------------------script for avatar selection page-----------------------------------------------

document.addEventListener("DOMContentLoaded", function () {
  const avatars = document.querySelectorAll(".avatar");

  avatars.forEach(function (avatar) {
    avatar.addEventListener("click", function () {
      const avatarId = this.id;
      const avatarFile = avatarId + ".png";

      fetch("setAvatar", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: "avatar=" + avatarFile,
      })
        .then((response) => {
          if (response.redirected) {
            window.location.href = response.url;
          }
        })
        .catch((err) => console.log(err));
    });
  });
});

//----------------------------------------------script for chat page-----------------------------------------------

let socket;
let username;
let selectedUser = "";

document.addEventListener("DOMContentLoaded", initChat);

async function initChat() {
  // get logged in user
  // const res = await fetch("getUser");
  // username = await res.text();

  const res = await fetch("getUser");
  const data = await res.text();

  const parts = data.split("|");

  username = parts[0]; // only username
  const avatar = parts[1];

  if (username === "not_logged_in") {
    const page = window.location.pathname;

    // only protect chat page
    if (page.includes("chat.html")) {
      window.location.href = "index.html";
    }

    return;
  }

  fetch("getUser")
    .then((res) => res.text())
    .then((data) => {
      if (data === "not_logged_in") {
        window.location.href = "index.html";
        return;
      }

      const parts = data.split("|");

      const username = parts[0];
      const avatar = parts[1];

      const nameBox = document.getElementById("usernameDisplay");
      const avatarBox = document.getElementById("userAvatar");

      if (nameBox) {
        nameBox.textContent = username;
      }

      if (avatarBox) {
        avatarBox.src = "Assets/" + avatar;
      }
    });

  connectWebSocket();
  loadUsers();

  // Load the chat user list (replace with backend data when available)
  loadUsers();

  // send button
  const sendBtn = document.getElementById("sendBtn");

  if (sendBtn) {
    sendBtn.addEventListener("click", sendMessage);
  }

  // press ENTER to send
  const input = document.getElementById("messageInput");

  if (input) {
    input.addEventListener("keypress", function (e) {
      if (e.key === "Enter") {
        sendMessage();
      }
    });
  }
}

function connectWebSocket() {
  socket = new WebSocket("ws://localhost:8080/chatapp/chat/" + username);
  socket.onopen = function () {
    console.log("Connected to chat server");
  };

  socket.onmessage = function (event) {
    const data = event.data;

    const parts = data.split(":");

    const sender = parts[0];
    const message = parts.slice(1).join(":");
    const messagesBox = document.getElementById("messages");
    const msg = document.createElement("div");

    // decide message side
    if (sender === username) {
      msg.classList.add("msg", "me"); // right side
    } else {
      msg.classList.add("msg", "other"); // left side
    }

    msg.innerText = message;

    messagesBox.appendChild(msg);

    messagesBox.scrollTop = messagesBox.scrollHeight;
  };
}

function sendMessage() {
  const input = document.getElementById("messageInput");
  const message = input.value.trim();

  if (message === "") return;

  if (selectedUser === "") {
    alert("Select a user first");
    return;
  }

  const fullMessage = username + "|" + selectedUser + "|" + message;

  console.log("Sending:", fullMessage);
  if (socket && socket.readyState === WebSocket.OPEN) {
    socket.send(fullMessage);
  } else {
    console.log("WebSocket not connected");
  }

  input.value = "";
}

function selectUser(user) {
  selectedUser = user;

  console.log("Chatting with:", selectedUser);

  // clear old chat messages
  const messagesBox = document.getElementById("messages");

  if (messagesBox) {
    messagesBox.innerHTML = "";
  }
}

function loadUsers() {
  fetch("getUsers")
    .then((res) => res.text())
    .then((data) => {
      const users = data.trim().split("\n");

      const userList = document.getElementById("userList");

      userList.innerHTML = ""; // clear old

      // users.forEach(user => {

      //   // skip yourself
      //   if (user === username) return;

      //   const div = document.createElement("div");

      //   div.classList.add("user");
      //   div.textContent = user;

      //   div.onclick = function () {
      //     selectUser(user);
      //   };

      //   userList.appendChild(div);
      // });
      users.forEach((user) => {
        const parts = user.split("|");
        const name = parts[0];
        const image = parts[1];

        if (name === username) return;

        const div = document.createElement("div");
        div.classList.add("user");

        // ❌ IMPORTANT: remove this line if exists
        // div.textContent = user;

        const img = document.createElement("img");
        // img.src = image;
        img.src = "/chatapp/Assets/" + image;
        img.alt = "avatar";

        const span = document.createElement("span");
        // span.textContent = name;

        span.textContent = " " + name; // 👈 add space before name

        div.appendChild(img);
        div.appendChild(span);

        div.onclick = () => selectUser(name);

        userList.appendChild(div);
      });
    })
    .catch((err) => console.log(err));
}
