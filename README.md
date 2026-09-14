# DigitalBank

A full-stack digital banking application built with **Java, Spring Boot, MySQL, JWT Authentication, HTML, CSS, and JavaScript**.

DigitalBank provides a secure web-based platform where customers can register, log in, manage bank accounts, and perform common banking operations such as deposits, withdrawals, transfers, and transaction-history management.

---

## 📌 Project Overview

DigitalBank is a Spring Boot based banking application developed to demonstrate real-world backend and frontend development concepts.

The application follows a layered architecture:

```text
Frontend
   ↓
REST Controller
   ↓
Service Layer
   ↓
Repository Layer
   ↓
MySQL Database
```

The application uses **Spring Data JPA and Hibernate** for database interaction and **JWT (JSON Web Token)** for authentication and authorization.

---

## 🚀 Features

### Authentication

* User registration
* User login
* JWT-based authentication
* Protected REST APIs
* Token validation
* Role information in JWT
* Secure authentication flow

### Customer Management

* Create customer
* View customer information
* Update customer information
* Delete customer
* Customer status management
* Input validation

### Bank Account Management

* Create bank account
* View account information
* Update account information
* Account status management
* Customer-account ownership validation
* Balance management

### Banking Transactions

* Deposit money
* Withdraw money
* Transfer money between accounts
* View transaction history
* Check account balance
* Verify transaction balance
* Filter transactions by type
* Filter transactions by date range
* Pagination for transaction records

### Frontend

* Login page
* Registration functionality
* Banking dashboard
* Deposit interface
* Withdrawal interface
* Money transfer interface
* Transaction history
* Logout functionality
* Validation and error handling
* Responsive user interface

---

# 🛠️ Technologies Used

## Backend

| Technology      | Purpose                         |
| --------------- | ------------------------------- |
| Java 21         | Programming language            |
| Spring Boot     | Backend framework               |
| Spring Web      | REST API development            |
| Spring Data JPA | Database access                 |
| Hibernate       | ORM framework                   |
| Spring Security | Application security            |
| JWT             | Authentication                  |
| Maven           | Build and dependency management |

## Database

| Technology | Purpose             |
| ---------- | ------------------- |
| MySQL 8    | Relational database |

## Frontend

| Technology | Purpose                      |
| ---------- | ---------------------------- |
| HTML5      | Web page structure           |
| CSS3       | Styling and responsive UI    |
| JavaScript | Frontend functionality       |
| Fetch API  | Communication with REST APIs |

## Development Tools

* Git
* GitHub
* Linux / WSL
* Eclipse / VS Code
* MySQL
* Maven

---

# 🏗️ Application Architecture

```text
                     ┌──────────────────────┐
                     │      Web Browser     │
                     │                      │
                     │ HTML / CSS / JS      │
                     └──────────┬───────────┘
                                │
                                │ HTTP / REST
                                ▼
                     ┌──────────────────────┐
                     │   Spring Boot App    │
                     │                      │
                     │   Controller Layer   │
                     └──────────┬───────────┘
                                │
                                ▼
                     ┌──────────────────────┐
                     │    Service Layer     │
                     │                      │
                     │ Business Logic       │
                     └──────────┬───────────┘
                                │
                                ▼
                     ┌──────────────────────┐
                     │  Repository Layer    │
                     │                      │
                     │ Spring Data JPA      │
                     └──────────┬───────────┘
                                │
                                │ Hibernate / JPA
                                ▼
                     ┌──────────────────────┐
                     │       MySQL          │
                     │      digitalbank     │
                     └──────────────────────┘
```

---

# 🔐 Authentication Flow

DigitalBank uses JWT-based authentication.

```text
User
 │
 │ Login
 ▼
Login API
 │
 │ Username + Password
 ▼
Authentication
 │
 │ Valid credentials
 ▼
JWT Token Generated
 │
 ▼
Token returned to client
 │
 ▼
Client stores token
 │
 ▼
Token sent with protected requests
 │
 ▼
JWT Authentication Filter
 │
 ▼
Token validation
 │
 ▼
Request authenticated
 │
 ▼
Protected API
```

The JWT contains information such as the authenticated username and role.

Sensitive configuration such as the database password and JWT secret is supplied using environment variables instead of being committed directly to the source code.

---

# 💰 Banking Operations

## Deposit

Customers can deposit money into an active bank account.

```text
Deposit Request
      ↓
Validate Account
      ↓
Check Account Status
      ↓
Add Amount
      ↓
Update Balance
      ↓
Create Transaction Record
```

## Withdrawal

Customers can withdraw money from their account when sufficient balance is available.

```text
Withdrawal Request
      ↓
Validate Account
      ↓
Check Account Status
      ↓
Check Available Balance
      ↓
Deduct Amount
      ↓
Create Transaction Record
```

## Transfer

Customers can transfer money between accounts.

```text
Transfer Request
      ↓
Validate Sender
      ↓
Validate Receiver
      ↓
Check Sender Balance
      ↓
Debit Sender
      ↓
Credit Receiver
      ↓
Create Transaction Records
```

---

# 📊 Transaction Management

The application supports transaction-history operations including:

* View transaction history
* Transaction type filtering
* Date-range filtering
* Pagination
* Balance verification
* Transaction summaries

Example transaction types:

```text
DEPOSIT
WITHDRAWAL
TRANSFER
```

---

# 🔌 REST API

The backend exposes REST APIs for communication between the frontend and backend.

## Authentication APIs

```text
POST /api/auth/register
POST /api/auth/login
```

## Customer APIs

```text
/api/customers
```

## Bank Account APIs

```text
/api/accounts
```

## Transaction APIs

```text
/api/transactions
```

Transaction APIs provide functionality for:

* Deposit
* Withdrawal
* Transfer
* Transaction history
* Balance checking
* Filtering
* Date-range queries
* Pagination

> API paths may contain additional path variables and query parameters depending on the operation.

---

# 📁 Project Structure

```text
digitalbank/
│
├── .mvn/
│   └── wrapper/
│
├── src/
│   │
│   ├── main/
│   │   │
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── digitalbank/
│   │   │           │
│   │   │           ├── controller/
│   │   │           │
│   │   │           ├── service/
│   │   │           │
│   │   │           ├── repository/
│   │   │           │
│   │   │           ├── entity/
│   │   │           │
│   │   │           ├── security/
│   │   │           │
│   │   │           └── DigitalbankApplication.java
│   │   │
│   │   └── resources/
│   │       │
│   │       ├── application.properties
│   │       │
│   │       └── static/
│   │           ├── index.html
│   │           ├── style.css
│   │           ├── script.js
│   │           ├── dashboard.html
│   │           └── dashboard.js
│   │
│   └── test/
│
├── .gitignore
├── .gitattributes
├── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

---

# 🗄️ Database

The application uses **MySQL** as its relational database.

Database:

```text
digitalbank
```

The application uses **Spring Data JPA and Hibernate** to communicate with the database.

The database stores information related to:

* Customers
* Users
* Bank accounts
* Transactions

Hibernate manages entity-to-table mapping and database operations.

---

# ⚙️ Configuration

The application requires a MySQL database and environment variables for sensitive configuration.

Example environment variables:

```bash
export DB_PASSWORD='your_database_password'
export JWT_SECRET='your_jwt_secret'
```

The actual values should **never be committed to GitHub**.

The application reads these values from environment variables.

Example:

```properties
spring.datasource.password=${DB_PASSWORD}

jwt.secret=${JWT_SECRET}
```

---

# 🖥️ Running the Application Locally

## Prerequisites

Install the following:

* Java 21
* Maven 3.9+
* MySQL 8
* Git

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

Verify Git:

```bash
git --version
```

---

## 1. Clone the Repository

```bash
git clone https://github.com/RAVITEJA0044/digitalbank.git
```

Navigate into the project:

```bash
cd digitalbank
```

---

## 2. Create the Database

Open MySQL and create the database:

```sql
CREATE DATABASE digitalbank;
```

Configure the required MySQL user and permissions.

---

## 3. Configure Environment Variables

Linux / WSL:

```bash
export DB_PASSWORD='your_database_password'
export JWT_SECRET='your_jwt_secret'
```

Use your own local values.

Do not commit these values to GitHub.

---

## 4. Start the Application

Using Maven:

```bash
mvn spring-boot:run
```

Or using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

---

## 5. Open the Application

After the application starts, open:

```text
http://localhost:8080
```

The frontend communicates with the Spring Boot REST APIs.

---

# 🧪 Testing

Run the automated tests using:

```bash
./mvnw test
```

Or:

```bash
mvn test
```

The project uses Spring Boot testing support for application testing.

---

# 📦 Build the Application

To create a production-style JAR file:

```bash
./mvnw clean package
```

The generated JAR will be created inside:

```text
target/
```

You can run the JAR using:

```bash
java -jar target/digitalbank-0.0.1-SNAPSHOT.jar
```

---

# 🔒 Security Practices

The project implements several security practices:

* JWT-based authentication
* Protected REST endpoints
* Environment-based secret configuration
* Account ownership validation
* Input validation
* Authentication filters
* Password authentication
* `.gitignore` protection for environment files
* No database password stored directly in source code

Sensitive credentials should always be managed outside the source code.

---

# 🔄 Git and GitHub Workflow

The project is maintained using Git and GitHub.

Typical development workflow:

```text
Make Code Changes
       ↓
git status
       ↓
git add .
       ↓
git commit
       ↓
git push
       ↓
GitHub
```

Repository:

https://github.com/RAVITEJA0044/digitalbank

---

# 📸 Screenshots

Screenshots can be added here to demonstrate the application interface.

Recommended screenshots:

1. Login page
2. Registration page
3. Dashboard
4. Deposit operation
5. Withdrawal operation
6. Transfer operation
7. Transaction history

Example:

```markdown
![DigitalBank Dashboard](screenshots/dashboard.png)
```

---

# 🔮 Future Enhancements

Possible future improvements include:

* Docker containerization
* Docker Compose
* Kubernetes deployment
* AWS deployment
* OpenShift deployment
* GitHub Actions CI/CD
* Swagger / OpenAPI documentation
* Admin dashboard
* Improved role-based authorization
* Email notifications
* Account statements
* PDF transaction statements
* Production database configuration
* Monitoring and logging
* Cloud deployment

---

# 🎯 Learning Objectives

This project demonstrates practical experience with:

* Core Java
* Spring Boot
* REST APIs
* Spring Data JPA
* Hibernate
* MySQL
* Spring Security
* JWT authentication
* HTML
* CSS
* JavaScript
* Fetch API
* Git
* GitHub
* Maven
* Linux / WSL

---

# 👨‍💻 Author

**Raviteja**

GitHub:

https://github.com/RAVITEJA0044

---

# 📄 License

This project is created for learning, development, and portfolio purposes.

