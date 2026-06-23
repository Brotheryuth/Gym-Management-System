package com.gym.controller;

import com.gym.model.Staff;
import com.gym.service.StaffService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;

public class StaffController {
    private final StaffService staffService;
    public StaffController(StaffService service){
        staffService=service;
    }
    public static  class StaffTemp{
        public String id;
        public String name;
        public String gender;
        public String dob;
        public double salary;
        public String phoneNumber;
        public String role;
        public String shift;
        public String hireDate;
        public StaffTemp(Staff s) {
            this.id = s.getId();
            this.name = s.getName();
            this.gender = s.getGender() != null ? s.getGender().name() : null;
            this.dob = s.getDob() != null ? s.getDob().toString() : null;
            this.salary = s.getSalary();
            this.phoneNumber = s.getPhoneNumber();
            this.role = s.getRole() != null ? s.getRole().name() : null;
            this.shift = s.getShift() != null ? s.getShift().name() : null;
            this.hireDate = s.getHireDate() != null ? s.getHireDate().toString() : null;
        }
    }

    /**
     * get all staff
     * @param ctx (req ,res)
     *&#064;brief  hold those actual staff to temporary staff so password is being hidden as expected
     */
    public void getAll(Context ctx){
        try{
            List<StaffTemp> temp = staffService.findAll().stream().map(StaffTemp::new).toList();
            ctx.status(HttpStatus.OK).json(temp);
        }catch (Exception e ){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }

    public  void getByID(Context ctx){
        try{
            String id = ctx.pathParam("id");
            Staff getStaff  = staffService.findById(id);
            if(getStaff==null) throw new IllegalStateException("Staff not found");
            ctx.status(HttpStatus.OK).json(new StaffTemp(getStaff));
        }catch (IllegalStateException e ){
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }catch (Exception e ){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }
    public void registerStaff(Context ctx) {
        try {
            Staff newStaff = ctx.bodyAsClass(Staff.class);
            if (newStaff == null) throw new IllegalArgumentException("Request body is empty.");

            if (newStaff.getName() == null || newStaff.getName().isBlank()) {
                throw new IllegalArgumentException("Staff name is required.");
            }
            if (newStaff.getPassword() == null || newStaff.getPassword().isBlank()) {
                throw new IllegalArgumentException("Password is required.");
            }
            boolean registered = staffService.registerStaff(newStaff);
            if (!registered) throw new IllegalStateException("Registration failed.");
            ctx.status(HttpStatus.CREATED).json(new StaffTemp(newStaff));
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (IllegalStateException e) {
            ctx.status(HttpStatus.CONFLICT).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }
    public void deleteStaff(Context ctx) {
        String id = ctx.pathParam("id");
        try {
            if (staffService.deleteStaff(id)) {
                ctx.status(HttpStatus.OK).result("Staff member deleted successfully.");
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Staff member not found.");
            }
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }
    }

