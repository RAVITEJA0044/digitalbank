const loginForm = document.getElementById("loginForm");
const message = document.getElementById("message");

loginForm.addEventListener("submit", async function(event) {

    event.preventDefault();

    const username =
        document.getElementById("username").value;

    const password =
        document.getElementById("password").value;

    message.textContent = "Logging in...";

    try {

        const response = await fetch(
            `/api/auth/login?username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`,
            {
                method: "POST"
            }
        );

        const data = await response.json();

        if (response.ok) {

            localStorage.setItem("token", data.token);

            message.textContent = "Login successful!";

            window.location.href = "dashboard.html";

        } else {

            message.textContent =
                data.message || "Invalid username or password";
        }

    } catch (error) {

        console.error(error);

        message.textContent =
            "Unable to connect to the server.";
    }

});