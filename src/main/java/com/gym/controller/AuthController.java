package com.gym.controller;

import com.gym.model.Staff;
import com.gym.service.StaffService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class AuthController {
    private final StaffService staffService;
    public AuthController(StaffService service){
        this.staffService=service;
    }
    //DTO (Data transfer object( for incoming http payload)
    public static  class LoginRequest{
        public String identifier; //can be Name or phone number
        public String password;
    }

    public static  class LoginResponse{
        private  String staffID;
        private String staffName;
        private String shift;
        private String role;

        public LoginResponse(Staff staff){
            this.staffID= staff.getId();
            this.staffName = staff.getName();
            this.role = staff.getRole() !=null ? staff.getRole().name() : null;
            this.shift = staff.getShift() !=null ? staff.getShift().name() : null;
        }

        public String getStaffID() {
            return staffID;
        }

        public String getRole() {
            return role;
        }

        public String getShift() {
            return shift;
        };

        public String getStaffName() {
            return staffName;
        }
    }
    public void login(Context ctx){
        try{
            String identifier = null;
            String password = null;

            if ("POST".equalsIgnoreCase(ctx.method().name())) {
                LoginRequest getStaff = ctx.bodyAsClass(LoginRequest.class);
                identifier = getStaff != null ? getStaff.identifier : null;
                password = getStaff != null ? getStaff.password : null;
            } else {
                identifier = ctx.queryParam("identifier");
                password = ctx.queryParam("password");
                if (identifier == null && ctx.body().length() > 0) {
                    try {
                        LoginRequest getStaff = ctx.bodyAsClass(LoginRequest.class);
                        identifier = getStaff != null ? getStaff.identifier : null;
                        password = getStaff != null ? getStaff.password : null;
                    } catch (Exception ignored) {}
                }
            }

            if(identifier == null || identifier.isBlank()){
                throw new IllegalArgumentException("Name or Phone number cannot be empty");
            }
            if(password == null || password.isBlank()){
                throw new IllegalArgumentException("Password cannot be empty");
            }
            Staff staff = staffService.authenticate(identifier, password);
            LoginResponse res = new LoginResponse(staff);
            ctx.status(HttpStatus.OK).json(res);
        }catch (IllegalArgumentException e ){
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        }catch (IllegalStateException e ){
            ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
        }catch (Exception e ){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
        }
    }


}
