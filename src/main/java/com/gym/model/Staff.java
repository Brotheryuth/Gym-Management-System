package com.gym.model;

import com.gym.enums.Gender;
import com.gym.enums.StaffRole;
import com.gym.enums.StaffShift;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;
import java.util.regex.Pattern;

public class Staff {
    private final String id;
    private String name;
    private Gender gender;
    private Date dob;
    private double salary;
    private String phoneNumber;
    private String password;
    private StaffRole role;
    private StaffShift shift;
    private Date hireDate;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s\\-\\(\\)]{7,15}$");

    public Staff() {
        this.id = java.util.UUID.randomUUID().toString();
    }

    /**
     * Constructor to create a new staff record (ID is auto-generated).
     *
     * @param name The staff name
     * @param gender The staff gender
     * @param dob Date of birth
     * @param salary Staff salary
     * @param phoneNumber Staff phone number
     * @param password Account password
     * @param role Staff role
     * @param shift Working shift
     * @param hireDate Hire date
     */
    public Staff(String name, Gender gender, Date dob, double salary, String phoneNumber, String password, StaffRole role, StaffShift shift, Date hireDate) {
        this.id = UUID.randomUUID().toString();
        setName(name);
        setGender(gender);
        setDob(dob);
        setSalary(salary);
        setPhoneNumber(phoneNumber);
        setPassword(password);
        setRole(role);
        setShift(shift);
        setHireDate(hireDate);
    }

    /**
     * Constructor to load an existing staff record from database.
     *
     * @param id The existing record ID
     * @param name The staff name
     * @param gender The staff gender
     * @param dob Date of birth
     * @param salary Staff salary
     * @param phoneNumber Staff phone number
     * @param password Account password
     * @param role Staff role
     * @param shift Working shift
     * @param hireDate Hire date
     */
    public Staff(String id, String name, Gender gender, Date dob, double salary, String phoneNumber, String password, StaffRole role, StaffShift shift, Date hireDate) {
        this.id = id;
        setName(name);
        setGender(gender);
        setDob(dob);
        setSalary(salary);
        setPhoneNumber(phoneNumber);
        setPassword(password);
        setRole(role);
        setShift(shift);
        setHireDate(hireDate);
    }

    /**
     * Calculates the age dynamically based on DOB.
     *
     * @return The calculated age as an integer
     */
    public int getAge() {
        if (this.dob == null) {
            return 18;
        }
        LocalDate dobLocalDate = this.dob.toLocalDate();
        return Period.between(dobLocalDate, LocalDate.now()).getYears();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank.");
        }
        this.name = name.trim();
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null.");
        }
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        if (dob == null) {
            this.dob = null; // optional
            return;
        }

        LocalDate dobLocalDate = dob.toLocalDate();
        int calculatedAge = Period.between(dobLocalDate, LocalDate.now()).getYears();
        if (calculatedAge < 5 || calculatedAge > 100) {
            throw new IllegalArgumentException("Age from Date of Birth (" + calculatedAge + ") must be between 5 and 100.");
        }
        this.dob = dob;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().equalsIgnoreCase("N/A")) {
            this.phoneNumber = "N/A";
            return;
        }
        String cleanPhone = phoneNumber.trim();
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new IllegalArgumentException("Invalid phone number format.");
        }
        this.phoneNumber = cleanPhone;
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
                this.id,
                this.name,
                this.gender,
                this.dob,
                getAge(),
                this.salary,
                this.phoneNumber,
                this.role,
                this.shift,
                this.hireDate
        );
    }
}
