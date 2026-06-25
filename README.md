# Before starting, you should have the following requirements on your machine:
1. **JDK 17 or Higher**
2. **Apache Maven configured in your Environment Path**
3. **MySQL Server**

## Get Started
Follow these steps to run the project on your local machine:

### 1. Clone Repository
* Clone:
```bash 
git clone https://github.com/Brotheryuth/Gym-Management-System.git
```
* Navigate to cloned repo:
```bash 
cd Gym-Management-System
```

### 2. Configure Database
#### 1. Open MySQL client and create database:
```sql
CREATE DATABASE gym_db;
```
#### 2. Navigate to `db.properties` and update it:
```properties
db.url = jdbc:mysql://localhost:3306/gym_db
db.user = YOUR_MYSQL_USERNAME
db.password = YOUR_MYSQL_PASSWORD
```

### 3. Run Application
* To clean compile the project and download all Maven dependencies, run:
```bash
mvn clean compile
``` 
* Start Application:
```bash 
mvn exec:java
```

## Project Architecture & Design Patterns
This project follows clean architectural standards to keep features highly cohesive (following the Single Responsibility Principle):

* **Model Layer**: Contains domain data structures (`Member`, `Membership`, `Payment`, etc.).
* **Repository Layer**: Handles direct SQL operations and CRUD interactions with MySQL.
* **Service Layer**: Houses the business logic (e.g. validating dates, double-subscriptions checks, and payment processing).
* **Controller/Route Layer**: Exposes the Javalin REST endpoints for external API clients.
* **GUI Layer (`com.gym.gui`)**: Self-contained Swing panels and modal dialogs that communicate directly with the services.
