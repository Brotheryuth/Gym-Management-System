package com.gym.controller;

import com.gym.model.MembershipPlan;
import com.gym.service.MembershipPlanService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;


public class MembershipPlanController {
    private  final MembershipPlanService membershipPlanService;

    public MembershipPlanController(MembershipPlanService membershipPlanService){
        this.membershipPlanService=membershipPlanService;
    }

    /**
     * create plan by accepting body from web
     * @param ctx
     */
    public  void createPlan(Context  ctx){
        try{
        MembershipPlan newPlan = ctx.bodyAsClass(MembershipPlan.class);
        // newPlan ID carried the UUID
        if(newPlan == null){
            throw new IllegalArgumentException("Plan cannot be null");
        }
        MembershipPlan createPlan  = membershipPlanService.createPlan(newPlan);
        // create plan carried the '1' ... ID
        if(createPlan ==null) throw new IllegalStateException("Cannot create Plan");

        ctx.status(HttpStatus.CREATED).json(createPlan);
        }catch (IllegalArgumentException e ){
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        }
        catch (Exception e ){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    /**
     * getPlan by ID
     * @param ctx
     */
    public  void getByID(Context ctx){
        String planID  = ctx.pathParam("id");
        try{
            MembershipPlan findPlan = membershipPlanService.findById(planID);
            if(findPlan == null) throw new IllegalStateException("Plan not Found");
            ctx.status(HttpStatus.OK).json(findPlan);

        }catch (IllegalStateException e ){
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }
        catch (Exception e ){
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        }
    }

    public void getPlanByDuration(Context ctx) {
        int getDuration = ctx.pathParamAsClass("duration", Integer.class).get();
        try {
            MembershipPlan plan = membershipPlanService.getPlanByDuration(getDuration);
            if (plan == null) {
                throw new IllegalStateException("Plan not found ");
            }
            ctx.status(HttpStatus.OK).json(plan);
        }catch (IllegalArgumentException e ){
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        }catch (IllegalStateException e ){
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }catch (Exception e ){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }
    /**
     * Update plan
     * @param ctx
     */
    public  void updatePlan(Context ctx){
        try{
        String id = ctx.pathParam("id");
        MembershipPlan reqBody = ctx.bodyAsClass(MembershipPlan.class);
        if(reqBody ==null) throw new IllegalStateException("Cannot Update plan");
        MembershipPlan updatePlan = membershipPlanService.updatePlan(id , reqBody.getPlanName(), reqBody.getPlanPrice(), reqBody.getDuration());
        if(updatePlan ==null) throw  new IllegalStateException("Cannot update plan");
        ctx.status(HttpStatus.OK).json(updatePlan);
        }catch (IllegalArgumentException e ){
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        }
        catch (IllegalStateException e ){
            ctx.status(HttpStatus.CONFLICT).result(e.getMessage());
        }catch (Exception e ){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    public void getAll(Context ctx ){
        try{
            ctx.status(HttpStatus.OK).json(membershipPlanService.findAll());
        }catch (Exception e ){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    public void deletePlan(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            if (membershipPlanService.deletePlan(id)) {
                ctx.status(HttpStatus.OK).result("Membership plan deleted successfully!");
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Membership plan not found");
            }
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (IllegalStateException e) {
            ctx.status(HttpStatus.CONFLICT).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    public void getSubscriberCount(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            if (membershipPlanService.findById(id) == null) {
                throw new IllegalStateException("Plan not found");
            }
            int count = membershipPlanService.getSubscriberCount(id);
            ctx.status(HttpStatus.OK).json(java.util.Map.of("subscriberCount", count));
        } catch (IllegalStateException e) {
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }
}
