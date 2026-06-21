package com.gym.route;

import com.gym.controller.MemberController;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;
public class MemberRoute implements EndpointGroup {
    private  final MemberController memberController;

    public MemberRoute(MemberController memberController){
        this.memberController = memberController;
    }

    @Override
    public void addEndpoints() {
        get(memberController::getAll); //GET api/members
        post(memberController::registerMember); //POST api/members

        get("phone/{phoneNumber}",memberController::getByPhoneNumber); // api/member/phone/0987654321
        path("id",()->{
            get(memberController::getByID);
            put(memberController::updateMember);
            delete(memberController::deleteMember);
        });
    }
}
