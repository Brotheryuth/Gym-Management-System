package com.gym.model;

import com.gym.enums.Gender;
import com.gym.enums.MemberStatus;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;
import java.util.regex.Pattern;

public class Member {
    private final String id;
    private String fullName;
    private Gender gender;
    private String phoneNumber;
    private Date dob; // Date of Birth
    private MemberStatus memberStatus;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s\\-\\(\\)]{7,15}$");

    public Member() {
        this.id = java.util.UUID.randomUUID().toString();
    }

    public Member(String fullName, Gender gender, String phoneNumber, Date dob, MemberStatus memberStatus) {
        this.id = UUID.randomUUID().toString();
        setFullName(fullName);
        setGender(gender);
        setPhoneNumber(phoneNumber);
        setDob(dob);
        setMemberStatus(memberStatus);
    }

    /**
     * Constructor for retrieving an EXISTING member (e.g., loaded from Database).
     */
    public Member(String id, String fullName, Gender gender, String phoneNumber, Date dob, MemberStatus memberStatus) {
        this.id = id;
        setFullName(fullName);
        setGender(gender);
        setPhoneNumber(phoneNumber);
        setDob(dob);
        setMemberStatus(memberStatus);
    }

    /**
     * Dynamically calculates the age based on the Date of Birth (dob).
     */
    public int getAge() {
        if (this.dob == null) {
            return 18; // fallback default age
        }
        LocalDate dobLocalDate = this.dob.toLocalDate();
        return Period.between(dobLocalDate, LocalDate.now()).getYears();
    }


    public String getId() {
        return id;
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
            this.dob = null; // optional in database
            return;
        }

        // Dynamically validate that the calculated age falls between 5 and 100
        LocalDate dobLocalDate = dob.toLocalDate();
        int calculatedAge = Period.between(dobLocalDate, LocalDate.now()).getYears();
        if (calculatedAge < 5 || calculatedAge > 100) {
            throw new IllegalArgumentException("Age from Date of Birth (" + calculatedAge + ") must be between 5 and 100.");
        }
        this.dob = dob;
    }

    public MemberStatus getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(MemberStatus memberStatus) {
        if (memberStatus == null) {
            throw new IllegalArgumentException("Member status cannot be null.");
        }
        this.memberStatus = memberStatus;
    }

    @Override
    public String toString() {
        return String.format(
                """
                        ----------------------------------
                                MEMBER INFORMATION
                        ----------------------------------
                        ID              : %s
                        Name            : %s
                        Gender          : %s
                        Phone Number    : %s
                        Date of Birth   : %s
                        Age             : %d
                        Status          : %s
                        ----------------------------------
                        """,
                this.id,
                this.fullName,
                this.gender,
                this.phoneNumber,
                this.dob,
                getAge(),
                this.memberStatus
        );
    }
}
