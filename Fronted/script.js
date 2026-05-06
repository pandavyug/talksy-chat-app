const params = new URLSearchParams(window.location.search);
const error = params.get("error");

if(error === "invalid"){
    document.getElementById("passError").innerText = "Wrong username or password";
}
