package com.gym.model;

import com.gym.enums.Gender;
import com.gym.enums.StaffRole;
import com.gym.enums.StaffShift;
import java.sql.Date;

public class Staff extends Person {
    private double salary;
    private String password;
    private StaffRole role;
    private StaffShift shift;
    private Date hireDate;

    public Staff() {
        super();
    }

    /**
     * Constructor to create a new staff record (ID is auto-generated).
     */
    public Staff(String name, Gender gender, Date dob, double salary, String phoneNumber, String password, StaffRole role, StaffShift shift, Date hireDate) {
        super(name, gender, phoneNumber, dob);
        setSalary(salary);
        setPassword(password);
        setRole(role);
        setShift(shift);
        setHireDate(hireDate);
    }

    /**
     * Constructor to load an existing staff record from database.
     */
    public Staff(String id, String name, Gender gender, Date dob, double salary, String phoneNumber, String password, StaffRole role, StaffShift shift, Date hireDate) {
        super(id, name, gender, phoneNumber, dob);
        setSalary(salary);
        setPassword(password);
        setRole(role);
        setShift(shift);
        setHireDate(hireDate);
    }

    // Redirect getName and setName to Person's fullName
    public String getName() {
        return getFullName();
    }

    public void setName(String name) {
        setFullName(name);
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        if (salary < 0.0) {
            throw new IllegalArgumentException("Salary cannot be negative.");
        }
        this.salary = salary;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank.");
        }
        this.password = password;
    }

    public StaffRole getRole() {
        return role;
    }

    public void setRole(StaffRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }
        this.role = role;
    }

    public StaffShift getShift() {
        return shift;
    }

    public void setShift(StaffShift shift) {
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null.");
        }
        this.shift = shift;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        if (hireDate == null) {
            throw new IllegalArgumentException("Hire date cannot be null.");
        }
        this.hireDate = hireDate;
    }

    @Override
    public String toString() {
        return String.format(
                """
                ----------------------------------
                        STAFF INFORMATION
                ----------------------------------
                ID              : %s
                Name            : %s
                Gender          : %s
                Date of Birth   : %s
                Age             : %d (dynamic)
                Salary          : $%.2f
                Phone Number    : %s
                Role            : %s
                Shift           : %s
                Hire Date       : %s
                ----------------------------------
                """,
                getId(),
                getName(),
                getGender(),
                getDob(),
                getAge(),
                this.salary,
                getPhoneNumber(),
                this.role,
                this.shift,
                this.hireDate
        );
    }
}
