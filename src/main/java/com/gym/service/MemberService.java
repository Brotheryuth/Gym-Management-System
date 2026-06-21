package com.gym.service;

import com.gym.enums.Gender;
import com.gym.enums.MemberStatus;
import com.gym.model.Member;
import com.gym.repository.MemberRepository;

import java.sql.Date;
import java.util.List;

public class MemberService {
    private MemberRepository memberRepository; // inject memberRepo to service so we can use their method

    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    /**
     * register member and check whether it already register
     * @param member the member to register
     * @return True if member is register successful otherwise throws exception
     * @throws IllegalArgumentException if member is invalid or already exists
     */
    public boolean registerMember(Member member){
        if(member == null) {
            throw new IllegalArgumentException("Member data cannot be null.");
        }
        
        // validate ID
        if(member.getId() != null && memberRepository.findByID(member.getId()) != null){
            throw new IllegalArgumentException("Member with ID " + member.getId() + " is already registered.");
        }

        //validate Phone number
        if(member.getPhoneNumber() != null && memberRepository.findByPhoneNumber(member.getPhoneNumber()) != null){
            throw new IllegalArgumentException("Member with phone number " + member.getPhoneNumber() + " already exists.");
        }

        return memberRepository.insert(member);
    }

    public Member createMember(String fullName, Gender gender, Date dob, String phoneNumber){
        Member newMember = new Member(fullName, gender, phoneNumber, dob, MemberStatus.INACTIVE);
        registerMember(newMember);
        return newMember;
    }

    public Member createMember(String fullName, String phoneNumber){
        Member newMember = new Member(fullName, Gender.MALE, phoneNumber, null, MemberStatus.INACTIVE);
        registerMember(newMember);
        return newMember;
    }

    public Member searchByID (String ID){
        if(ID == null || ID.isBlank()){
            return null;
        }
        return memberRepository.findByID(ID.trim());
    }

    public Member searchByPhoneNumber(String phoneNumber){
        if(phoneNumber == null || phoneNumber.isBlank()) return null;
        return memberRepository.findByPhoneNumber(phoneNumber);
    }

    public List<Member> findAll(){
        return memberRepository.findAll();
    }

    public boolean deleteMember(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Member ID cannot be null or blank.");
        }
        return memberRepository.delete(id.trim());
    }
}
