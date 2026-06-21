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
        if (fullName == null || fullName.isBlank() || fullName.trim().isEmpty()) {
            System.out.println("Invalid name. Setting default name: 'Unknown'");
            this.fullName = "Unknown";
            return;
        }
        this.fullName = fullName.trim();
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        if (gender == null) {
            System.out.println("Invalid gender. Setting default: MALE");
            this.gender = Gender.MALE;
            return;
        }
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            this.phoneNumber = "N/A";
            return;
        }
        String cleanPhone = phoneNumber.trim();
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            System.out.println("Invalid phone number. Setting default: N/A");
            this.phoneNumber = "N/A";
            return;
        }
        this.phoneNumber = cleanPhone;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        if (dob == null) {
            System.out.println("Invalid Date of Birth. Setting default: 18 years ago.");
            this.dob = Date.valueOf(LocalDate.now().minusYears(18));
            return;
        }

        // Dynamically validate that the calculated age falls between 5 and 100
        LocalDate dobLocalDate = dob.toLocalDate();
        int calculatedAge = Period.between(dobLocalDate, LocalDate.now()).getYears();
        if (calculatedAge < 5 || calculatedAge > 100) {
            System.out.println("Invalid age from Date of Birth (" + calculatedAge + "). Setting default: 18 years ago.");
            this.dob = Date.valueOf(LocalDate.now().minusYears(18));
            return;
        }
        this.dob = dob;
    }

    public MemberStatus getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(MemberStatus memberStatus) {
        if (memberStatus == null) {
            System.out.println("Invalid status. Setting default: INACTIVE");
            this.memberStatus = MemberStatus.INACTIVE;
            return;
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
