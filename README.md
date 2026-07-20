# ⚙️ TelcoCare Backend

A Spring Boot-based RESTful backend for the **TelcoCare Ticket Management System**, developed for managing customer support operations in telecommunication companies.

The backend handles authentication, customer management, ticket lifecycle management, business rules, reporting, and data persistence while serving the React frontend.

---

# 🌐 Related Repositories

This project consists of two separate repositories.

- 🎨 **Frontend:** https://github.com/fall09/telcocare-frontend
- ⚙️ **Backend:** https://github.com/fall09/ticket-management-system

---

# ✨ Features

## 🔐 Authentication

- JWT Authentication
- Secure Login
- BCrypt Password Encryption
- Protected Endpoints
- Spring Security Integration

---

## 👥 Customer Management

- Create Customer
- Update Customer
- Delete Customer
- Customer Search
- Pagination
- Province & District Filtering
- Customer Status Management
- Customer Status History

---

## 🎫 Ticket Management

- Create Ticket
- Update Ticket
- Delete Ticket
- Ticket Assignment
- Ticket Pool
- My Tickets
- Ticket Status Updates
- Ticket Priority Updates
- Ticket Description Updates
- Ticket Filtering & Searching
- Automatic Ticket Number Generation
- SLA Due Date Calculation

---

## 📋 Ticket Activity Logging

Every important ticket action is automatically recorded.

Supported activities include:

- Ticket Created
- Ticket Assigned
- Status Changed
- Priority Changed
- Description Updated

This provides a complete audit trail throughout the ticket lifecycle.

---

## 🗂 Category Management

- Categories
- Subcategories
- Default Priority Configuration
- Location Requirement Rules
- Potential Customer Restrictions

Business rules are automatically enforced during ticket creation.

---

## 📊 Reports

The backend generates all statistical data displayed by the frontend.

Available reports include:

- Total Tickets
- Open Tickets
- Active Tickets
- Critical Tickets
- Resolution Rate
- Category Distribution
- Province Distribution
- Priority Distribution
- Customer Status Distribution
- Daily Ticket Trends

---

## 👨‍💼 Employee Features

- Employee Authentication
- Profile Information
- Assigned Tickets
- Ticket Ownership

---

# 🎫 Ticket Workflow

```text
OPEN
   ↓
IN_PROGRESS
   ↓
ON_HOLD
   ↓
RESOLVED
   ↓
CLOSED
```

Every transition is automatically recorded in the Ticket Activity Log.

---

# ⚙️ Business Rules

The backend is responsible for enforcing all business rules.

Examples include:

- Every subcategory has a default priority.
- Some ticket types require province and district information.
- Potential customers can only create informational tickets.
- Existing customers have access to all ticket categories.
- Ticket numbers are generated automatically.
- Ticket activities are logged automatically.
- Customer status changes create history records.
- SLA due dates are calculated automatically.
- Category settings dynamically determine ticket creation behavior.

---

# 🏗 Architecture

The project follows a layered architecture.

```text
Controller
     │
     ▼
Service
     │
     ▼
Repository
     │
     ▼
PostgreSQL Database
```

Project layers include:

- Controllers
- Services
- Repositories
- Entities
- DTOs
- Security
- Configuration
- Exception Handling
- Seeders
- Utilities

---

# 🛠 Tech Stack

- Java 17
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- Lombok
- REST API

---

# 📁 Project Structure

```text
src
└── main
    ├── java
    │
    ├── config
    ├── controller
    ├── dto
    ├── entity
    ├── exception
    ├── mapper
    ├── repository
    ├── security
    ├── service
    ├── seeder
    └── util
    │
    └── resources
        ├── application.properties
        └── csv/
```

---

# 🚀 Getting Started

## Prerequisites

Before running the project, install:

- Java 17
- Maven
- PostgreSQL
- Git

---

## Clone Repository

```bash
git clone https://github.com/fall09/ticket-management-system.git

cd ticket-management-system
```

---

## Configure PostgreSQL

Create a PostgreSQL database.

Example:

```text
telcocare
```

Update **application.properties**:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/telcocare
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
```

---

## Install Dependencies

```bash
mvn clean install
```

---

## Run the Application

Using Maven:

```bash
mvn spring-boot:run
```

Or simply run the main Spring Boot application class from IntelliJ IDEA.

The backend will start on:

```text
http://localhost:8080
```

---

# 🔗 REST API

Base URL

```text
http://localhost:8080/api
```

### Authentication

```http
POST /auth/login
```

### Customers

```http
GET    /customers
POST   /customers
PUT    /customers/{id}
DELETE /customers/{id}
```

### Tickets

```http
GET    /tickets
POST   /tickets
PUT    /tickets/{id}
```

### Categories

```http
GET    /categories
POST   /categories
```

### Reports

```http
GET /reports/summary
GET /reports/category-distribution
GET /reports/province-distribution
GET /reports/priority-distribution
GET /reports/customer-status
GET /reports/daily-trend
```

---

# 🔐 Security

Authentication is implemented using JWT.

After a successful login:

- Passwords are encrypted using BCrypt.
- A JWT token is generated.
- Protected endpoints require:

```text
Authorization: Bearer <token>
```

Unauthorized requests are rejected automatically by Spring Security.

---

# 🗄 Database

Main entities include:

- Employee
- Customer
- CustomerStatusHistory
- Ticket
- TicketActivityLog
- Category
- Subcategory

Relationships are managed using Spring Data JPA and Hibernate.

---

# 💡 Future Improvements

- Swagger / OpenAPI Documentation
- Docker Support
- Email Notifications
- File Attachments
- Automated SLA Monitoring
- Unit & Integration Testing
- Role-Based Authorization
- Audit Dashboard

---

# 📄 License

This project was developed for educational and portfolio purposes.
