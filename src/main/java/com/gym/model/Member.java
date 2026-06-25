package com.gym.model;

import com.gym.enums.Gender;
import com.gym.enums.MemberStatus;
import java.sql.Date;

public class Member extends Person {
    private MemberStatus memberStatus;

    public Member() {
        super();
    }

    public Member(String fullName, Gender gender, String phoneNumber, Date dob, MemberStatus memberStatus) {
        super(fullName, gender, phoneNumber, dob);
        setMemberStatus(memberStatus);
    }

    /**
     * Constructor for retrieving an EXISTING member (e.g., loaded from Database).
     */
    public Member(String id, String fullName, Gender gender, String phoneNumber, Date dob, MemberStatus memberStatus) {
        super(id, fullName, gender, phoneNumber, dob);
        setMemberStatus(memberStatus);
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
                getId(),
                getFullName(),
                getGender(),
                getPhoneNumber(),
                getDob(),
                getAge(),
                this.memberStatus
        );
    }
}
