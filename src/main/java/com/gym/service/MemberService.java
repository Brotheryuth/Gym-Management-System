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
     * @param member
     * @return True if member is register successful otherwise return false (already exist)
     */
    public boolean registerMember(Member member){

        if(member ==null) return false;
        // validate ID
        if(memberRepository.findByID(member.getId() ) !=null){
            System.out.println("Member already register!");
            return  false;
        }

        //validate Phone number
        if(memberRepository.findByPhoneNumber(member.getPhoneNumber()) !=null){
            System.out.println("Member already exist!");
            return false;
        }

        return memberRepository.insert(member);
    }
    public Member createMember(String fullName, Gender gender, Date dob, String phoneNumber){
        Member newMember = new Member(fullName,gender,phoneNumber, dob , MemberStatus.INACTIVE);
        boolean success = memberRepository.insert(newMember);
        return success? newMember : null;
    }

    public Member createMember(String fullName , String phoneNumber){
        Member newMember = new Member(fullName,Gender.MALE,phoneNumber, null,MemberStatus.INACTIVE);
        boolean success = memberRepository.insert(newMember);
        return success? newMember : null;
    }


    public Member searchByID (String ID){
        if(ID ==null || ID.isBlank()){
            return null;
        }
        return memberRepository.findByID(ID.trim());
    }

    public Member searchByPhoneNumber(String phoneNumber){
        if(phoneNumber==null || phoneNumber.isBlank()) return  null;
        return memberRepository.findByPhoneNumber(phoneNumber);
    }

    public List<Member> findAll(){
        return memberRepository.findAll();
    }
}
