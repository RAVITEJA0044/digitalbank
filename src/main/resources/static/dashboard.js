let accountId = null;
let currentOperation = null;

const token = localStorage.getItem("token");


// Check login
if (!token) {

    window.location.href = "index.html";

}


// Load account information
async function loadAccount() {
    try {

        const response = await fetch("/api/accounts", {

            headers: {
                "Authorization": "Bearer " + token
            }

        });

        if (!response.ok) {

            throw new Error("Unable to load account");

        }

        const accounts = await response.json();

        if (accounts.length === 0) {

            throw new Error("No bank account found");

        }

        const account = accounts[0];

        accountId = account.accountId;

        document.getElementById("accountNumber").textContent =
            account.accountNumber;

        document.getElementById("accountType").textContent =
            account.accountType;

        document.getElementById("balance").textContent =
            "₹ " + account.balance;

        document.getElementById("accountStatus").textContent =
            account.status;

        document.getElementById("usernameDisplay").textContent =
            "Welcome";

    } catch (error) {

        console.error(error);

        document.getElementById("balance").textContent =
            "Unable to load";

    }

}


// Show deposit form
function showDeposit() {

    currentOperation = "deposit";

    document.getElementById("transactionTitle").textContent =
        "Deposit Money";

    document.getElementById("transferAccount").style.display =
        "none";

    document.getElementById("transactionSection").style.display =
        "block";

}


// Show withdrawal form
function showWithdraw() {

    currentOperation = "withdraw";

    document.getElementById("transactionTitle").textContent =
        "Withdraw Money";

    document.getElementById("transferAccount").style.display =
        "none";

    document.getElementById("transactionSection").style.display =
        "block";

}


// Show transfer form
function showTransfer() {

    currentOperation = "transfer";

    document.getElementById("transactionTitle").textContent =
        "Transfer Money";

    document.getElementById("transferAccount").style.display =
        "block";

    document.getElementById("transactionSection").style.display =
        "block";

}


// Execute transaction
async function executeTransaction() {

    const amount =
        document.getElementById("transactionAmount").value;

    const description =
        document.getElementById("description").value;

    const message =
        document.getElementById("transactionMessage");

    if (!amount || Number(amount) <= 0) {

        message.textContent =
            "Please enter a valid amount.";

        return;

    }


    try {

        let url = "";

        if (currentOperation === "deposit") {

            url =
                `/api/transactions/deposit/${accountId}` +
                `?amount=${amount}`;

        }

        else if (currentOperation === "withdraw") {

            url =
                `/api/transactions/withdraw/${accountId}` +
                `?amount=${amount}`;

        }

        else if (currentOperation === "transfer") {

            const destinationAccount =
                document.getElementById("transferAccount").value;

            if (!destinationAccount) {

                message.textContent =
                    "Please enter destination account number.";

                return;

            }

            url =
                `/api/transactions/transfer` +
                `?fromAccountId=${accountId}` +
                `&toAccountNumber=${destinationAccount}` +
                `&amount=${amount}`;

        }


        if (description) {

            url +=
                `&description=${encodeURIComponent(description)}`;

        }


        const response = await fetch(url, {

            method: "POST",

            headers: {

                "Authorization": "Bearer " + token

            }

        });


        const data = await response.json();


        if (response.ok) {

            message.textContent =
                "Transaction successful!";

            document.getElementById("transactionAmount").value =
                "";

            document.getElementById("description").value =
                "";

            document.getElementById("transferAccount").value =
                "";

            await loadAccount();

            await loadTransactions();

        }

        else {

            message.textContent =
                data.message || "Transaction failed.";

        }

    }

    catch (error) {

        console.error(error);

        message.textContent =
            "Unable to connect to the server.";

    }

}


// Load transaction history
async function loadTransactions() {

    if (!accountId) {

        await loadAccount();

    }


    try {

        const response = await fetch(
            `/api/transactions/account/${accountId}`,
            {

                headers: {
                    "Authorization": "Bearer " + token
                }

            }
        );


        if (!response.ok) {

            throw new Error(
                "Unable to load transactions"
            );

        }


        const transactions =
            await response.json();


        const table =
            document.getElementById("transactionTable");

        table.innerHTML = "";


        transactions.forEach(transaction => {

            const row =
                document.createElement("tr");


            row.innerHTML = `

                <td>${transaction.transactionId}</td>

                <td>${transaction.transactionType}</td>

                <td>₹ ${transaction.amount}</td>

                <td>₹ ${transaction.balanceAfterTransaction}</td>

                <td>${transaction.transactionDate}</td>

                <td>${transaction.description || ""}</td>

            `;


            table.appendChild(row);

        });

    }

    catch (error) {

        console.error(error);

    }

}


// Logout
document
    .getElementById("logoutButton")
    .addEventListener("click", function() {

        localStorage.removeItem("token");

        window.location.href = "index.html";

    });


// Load dashboard when page opens
loadAccount();

loadTransactions();
