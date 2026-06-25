package com.gym.controller;

import com.gym.model.Member;
import com.gym.service.MemberService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class MemberController {
    private  final MemberService memberService;

    public MemberController(MemberService memberService){
        this.memberService =memberService;
    }
    
    public void getAll(Context ctx){
        ctx.status(HttpStatus.OK).json(memberService.findAll());
    }

    /**
     * a method to get id from param (/member/1)
     * @param ctx
     * @return Okay is found otherwise NOT_FOUND
     */
    public void findById(Context ctx ){
        String id = ctx.pathParam("id");
        try{
            Member newMember = memberService.findById(id);
            if(newMember ==null) throw new IllegalStateException("Member is null");
            ctx.status(HttpStatus.OK).json(newMember);
        }catch (IllegalStateException e ){
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    public void getByPhoneNumber(Context ctx){
        try{
            String phoneNumber = ctx.pathParam("phoneNumber");
            Member newMember = memberService.findByPhoneNumber(phoneNumber);
            if(newMember ==null) throw new IllegalStateException("Member is null");
            ctx.status(HttpStatus.OK).json(newMember);
        }catch (IllegalStateException e ){
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
    }

    public void registerMember(Context ctx){
        try{
            Member newMember= ctx.bodyAsClass(Member.class); // = req.body
        if(newMember ==null) throw new IllegalArgumentException("Request Body is Empty");

        if(memberService.registerMember(newMember)){
        ctx.status(HttpStatus.CREATED).json(newMember);
        }
        else{
            throw new IllegalStateException();
        }

        }catch (IllegalArgumentException e ){
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        }catch (Exception e ){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    public void deleteMember(Context ctx){
        try{
            String id = ctx.pathParam("id");
            if(memberService.deleteMember(id)){
            ctx.status(HttpStatus.OK).result("Member delete successful!");
            }else{
                ctx.status(HttpStatus.NOT_FOUND).result("Member with "+id+" Not Found");
            }
        }catch (Exception e ){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    public void updateMember(Context ctx){
        try{
            Member updateMember = ctx.bodyAsClass(Member.class);
            String id = ctx.pathParam("id");
            if(updateMember ==null) throw new IllegalStateException("Update Member body is empty ");
            updateMember = memberService.updateMember(id,updateMember);
            if(updateMember !=null) {
                ctx.status(HttpStatus.OK).json(updateMember);
            }else{
                throw new IllegalStateException("Duplicate phone number or body is null");
            }
        } catch (IllegalArgumentException e) {
        ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
    } catch (IllegalStateException e) {
        ctx.status(HttpStatus.CONFLICT).result(e.getMessage());
    } catch (Exception e) {
        ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
    }
}
}
