package com.gym.model;

import com.gym.enums.Gender;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class Person {
    protected String id;
    protected String fullName;
    protected Gender gender;
    protected String phoneNumber;
    protected Date dob;

    protected static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s\\-\\(\\)]{7,15}$");

    public Person() {
        this.id = UUID.randomUUID().toString();
    }

    public Person(String fullName, Gender gender, String phoneNumber, Date dob) {
        this.id = UUID.randomUUID().toString();
        setFullName(fullName);
        setGender(gender);
        setPhoneNumber(phoneNumber);
        setDob(dob);
    }

    public Person(String id, String fullName, Gender gender, String phoneNumber, Date dob) {
        this.id = id;
        setFullName(fullName);
        setGender(gender);
        setPhoneNumber(phoneNumber);
        setDob(dob);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name cannot be null or blank.");
        }
        this.fullName = fullName.trim();
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

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        if (dob == null) {
            this.dob = null;
            return;
        }
        LocalDate dobLocalDate = dob.toLocalDate();
        int calculatedAge = Period.between(dobLocalDate, LocalDate.now()).getYears();
        if (calculatedAge < 5 || calculatedAge > 100) {
            throw new IllegalArgumentException("Age from Date of Birth (" + calculatedAge + ") must be between 5 and 100.");
        }
        this.dob = dob;
    }

    public int getAge() {
        if (this.dob == null) {
            return 18;
        }
        LocalDate dobLocalDate = this.dob.toLocalDate();
        return Period.between(dobLocalDate, LocalDate.now()).getYears();
    }
}
