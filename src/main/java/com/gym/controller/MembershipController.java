package com.gym.controller;


import com.gym.service.MembershipService;

public class MembershipController {
    private final MembershipService membershipService ;

    public  MembershipController(MembershipService membershipService){
        this.membershipService=membershipService;
    }

}
