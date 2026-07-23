# Gym Management System

A Java-powered backend REST service and desktop application for managing gym memberships, subscribers, pricing plans, staff accounts, and billing transactions.

---

## Team Members

- Ly Serakyuth
- Bauch Sacara
- Iem Sievinh
- Teng Mouykim

---

## Table of Contents

- [Team Members](#team-members)
- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [System Diagrams](#system-diagrams)
  - [Tier Architecture Diagram](#tier-architecture-diagram)
  - [Class Diagram](#class-diagram)
  - [Stepwise Workflow Diagram](#stepwise-workflow-diagram)
  - [Database ER Diagram](#database-er-diagram)
- [Getting Started](#getting-started)
  - [1. Database Configuration](#1-database-configuration)
  - [2. Build & Run Application](#2-build--run-application)
- [Troubleshooting & Common Issues](#troubleshooting--common-issues)
- [Project Architecture](#project-architecture)

---

## Overview

This project provides the Java backend REST API server (built with Javalin) and desktop GUI (built with Java Swing) for the Gym Management System. It handles database persistence, business validation, member registrations, and payment transaction processing.

---

## Prerequisites

- **JDK 17+**: Java Development Kit to compile and run the backend application
- **Apache Maven**: Dependency management and build tool (`mvn`)
- **Javalin**: Lightweight Java REST API framework
- **MySQL Server**: Relational database engine (`gym_db` on default port `3306`)

---

## System Diagrams

### Tier Architecture Diagram
![Tier Architecture Diagram](assets/tierDiagram.png)

### Class Diagram
![Class Diagram](assets/classdiagram.png)

### Stepwise Workflow Diagram
![Stepwise Workflow Diagram](assets/stepwise.png)

### Database ER Diagram
```mermaid
erDiagram
    member {
        INT id PK "AUTO_INCREMENT"
        VARCHAR fullName "NOT NULL"
        VARCHAR gender "CHECK(MALE, FEMALE, OTHER)"
        VARCHAR phoneNumber "DEFAULT 'N/A'"
        DATE dob
        VARCHAR status "DEFAULT 'INACTIVE'"
    }

    membershipPlan {
        INT id PK "AUTO_INCREMENT"
        VARCHAR planName "NOT NULL"
        INT duration "CHECK(>=1)"
        DECIMAL planPrice "CHECK(>=0)"
    }

    memberships {
        INT id PK "AUTO_INCREMENT"
        INT planID FK
        INT memberID FK
        TIMESTAMP startDate "DEFAULT CURRENT_TIMESTAMP"
        TIMESTAMP endDate
        VARCHAR status "DEFAULT 'PENDING'"
    }

    payment {
        INT id PK "AUTO_INCREMENT"
        INT membershipID FK
        DECIMAL baseAmount "CHECK(>=0)"
        DECIMAL finalAmount "CHECK(>=0)"
        DECIMAL discount "DEFAULT 0.00"
        VARCHAR method "DEFAULT 'BYCASH'"
        VARCHAR status "DEFAULT 'PENDING'"
        TIMESTAMP createAt "DEFAULT CURRENT_TIMESTAMP"
        TIMESTAMP paymentDate
    }

    staff {
        INT id PK "AUTO_INCREMENT"
        VARCHAR name "NOT NULL"
        VARCHAR gender "CHECK(MALE, FEMALE, OTHER)"
        DATE dob
        DECIMAL salary "CHECK(>=0)"
        VARCHAR phoneNumber "DEFAULT 'N/A'"
        VARCHAR password "NOT NULL"
        VARCHAR role "CHECK(ADMIN, CASHIER)"
        VARCHAR shift "CHECK(MORNING, AFTERNOON, NIGHT, FULLTIME)"
        DATE hireDate
    }

    memberships ||--|| member : "belongs to (memberID)"
    memberships ||--|| membershipPlan : "subscribes to (planID)"
    payment ||--|| memberships : "pays for (membershipID)"
```

---

## Getting Started

### 1. Database Configuration

1. **Create the database in MySQL**:
   ```sql
   CREATE DATABASE gym_db;
   ```

2. **Locate and update the properties file**:
   Open `src/main/resources/db.properties` in your project folder and update it with your local MySQL credentials:
   ```properties
   db.url = jdbc:mysql://localhost:3306/gym_db
   db.user = YOUR_MYSQL_USERNAME
   db.password = YOUR_MYSQL_PASSWORD
   ```
   - **File Location**: `src/main/resources/db.properties`
   - **db.url**: MySQL connection URL (default port is `3306` and database name is `gym_db`).
   - **db.user**: Your MySQL database username (e.g. `root`).
   - **db.password**: Your MySQL account password.

   *(Note: Table schema and default records are automatically generated on application startup by `DatabaseInitializer`.)*

---

### 2. Build & Run Application

#### Option A: Running with Maven (Native)

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Brotheryuth/Gym-Management-System.git
   cd Gym-Management-System
   ```

2. **Clean compile and download dependencies**:
   ```bash
   mvn clean compile
   ```

3. **Start Application**:
   ```bash
   mvn exec:java
   ```

#### Option B: Running with Docker

1. **Build the Docker Image**:
   ```bash
   docker build -t gym-management .
   ```

2. **Run the Container**:
   ```bash
   docker run -p 7070:7070 gym-management
   ```

---

## Troubleshooting & Common Issues

### 1. Compilation or Runtime Errors
If you run into build issues, stale class files, or runtime errors after pulling or modifying code, clear the target build folder and recompile:

```bash
mvn clean compile
```

Once the clean build succeeds, start the server again:

```bash
mvn exec:java
```

### 2. Database Connection Failure
If the application fails to connect to MySQL on startup:
- Verify that your MySQL server service is actively running on port `3306`.
- Double-check that `gym_db` has been created (`CREATE DATABASE gym_db;`).
- Ensure the username and password in `src/main/resources/db.properties` match your MySQL setup.

---

## Project Architecture

- **Model Layer (`com.gym.model`)**: Domain data structures (`Member`, `Membership`, `Payment`, `Staff`, etc.).
- **Repository Layer (`com.gym.repository`)**: Direct SQL queries and JDBC database access.
- **Service Layer (`com.gym.service`)**: Business logic, date validation, and payment processing rules.
- **Controller Layer (`com.gym.controller`)**: Javalin REST API routes for external web clients.
- **GUI Layer (`com.gym.gui`)**: Swing desktop views and cashier terminal dialogs.
