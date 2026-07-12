| Table | Column | Data Type | Constraint |
| :--- | :--- | :--- | :--- |
| **member** | id | INT | PK, AUTO_INCREMENT |
| | fullName | VARCHAR(50) | NOT NULL |
| | gender | VARCHAR(10) | NOT NULL, CHECK (MALE, FEMALE, OTHER) |
| | phoneNumber | VARCHAR(15) | DEFAULT 'N/A' |
| | dob | DATE | NULL |
| | status | VARCHAR(10) | NOT NULL, DEFAULT 'INACTIVE', CHECK (ACTIVE, INACTIVE) |
| **membershipPlan** | id | INT | PK, AUTO_INCREMENT |
| | planName | VARCHAR(50) | NOT NULL |
| | duration | INT | NOT NULL, CHECK (duration >= 1) |
| | planPrice | DECIMAL(10,2) | NOT NULL, CHECK (planPrice >= 0) |
| **memberships** | id | INT | PK, AUTO_INCREMENT |
| | planID | INT | NOT NULL, FK to membershipPlan(id) |
| | memberID | INT | NOT NULL, FK to member(id) ON DELETE CASCADE |
| | startDate | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| | endDate | TIMESTAMP | NOT NULL |
| | status | VARCHAR(15) | NOT NULL, DEFAULT 'PENDING', CHECK (ACTIVE, INACTIVE, EXPIRED, PENDING, CANCELLED) |
| **payment** | id | INT | PK, AUTO_INCREMENT |
| | membershipID | INT | NOT NULL, FK to memberships(id) ON DELETE CASCADE |
| | baseAmount | DECIMAL(10,2) | NOT NULL, CHECK (baseAmount >= 0) |
| | finalAmount | DECIMAL(10,2) | NOT NULL, CHECK (finalAmount >= 0) |
| | discount | DECIMAL(3,2) | DEFAULT 0.00, CHECK (discount >= 0.0 AND discount <= 1.0) |
| | method | VARCHAR(20) | DEFAULT 'BYCASH', CHECK (BYCASH, KHQR, CREDITCARD) |
| | status | VARCHAR(20) | DEFAULT 'PENDING', CHECK (PAID, PENDING, FAILED) |
| | createAt | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| | paymentDate | TIMESTAMP | NULL |
| **staff** | id | INT | PK, AUTO_INCREMENT |
| | name | VARCHAR(50) | NOT NULL |
| | gender | VARCHAR(10) | NOT NULL, CHECK (MALE, FEMALE, OTHER) |
| | dob | DATE | NULL |
| | salary | DECIMAL(10,2) | NOT NULL, CHECK (salary >= 0) |
| | phoneNumber | VARCHAR(15) | DEFAULT 'N/A' |
| | password | VARCHAR(255) | NOT NULL |
| | role | VARCHAR(20) | NOT NULL, CHECK (ADMIN, CASHIER) |
| | shift | VARCHAR(15) | NOT NULL, CHECK (MORNING, AFTERNOON, NIGHT, FULLTIME) |
| | hireDate | DATE | NULL |
