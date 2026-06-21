package com.gym.service;

import com.gym.model.Staff;
import com.gym.enums.StaffShift;
import com.gym.enums.StaffRole;
import com.gym.repository.StaffRepository;

import java.util.List;

public class StaffService {
    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    /**
     * Registers a new staff member in the system.
     * Enforces phone number uniqueness.
     *
     * @param staff the staff member to register
     * @return true if successful
     */
    public boolean registerStaff(Staff staff) {
        if (staff == null) {
            throw new IllegalArgumentException("Staff member data cannot be null.");
        }

        // Validate uniqueness of phone number (excluding N/A)
        if (staff.getPhoneNumber() != null && !staff.getPhoneNumber().equalsIgnoreCase("N/A")) {
            Staff existing = findByPhoneNumber(staff.getPhoneNumber());
            if (existing != null) {
                throw new IllegalArgumentException("Staff member with phone number " + staff.getPhoneNumber() + " already exists.");
            }
        }

        return staffRepository.insert(staff);
    }

    /**
     * Authenticates staff using username (name) or phone number and password.
     *
     * @param identifier name or phone number
     * @param password the password
     * @return the authenticated Staff member
     */
    public Staff authenticate(String identifier, String password) {
        if (identifier == null || identifier.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Identifier and password cannot be empty.");
        }

        String searchKey = identifier.trim();
        for (Staff staff : staffRepository.findAll()) {
            boolean matchesIdentifier = searchKey.equalsIgnoreCase(staff.getName()) 
                    || searchKey.equals(staff.getPhoneNumber());
            
            if (matchesIdentifier && staff.getPassword().equals(password)) {
                return staff;
            }
        }

        throw new IllegalArgumentException("Invalid name/phone number or password.");
    }

    /**
     * Updates staff salary details.
     *
     * @param staffId the staff ID
     * @param newSalary the new salary
     * @return the updated Staff object
     */
    public Staff updateSalary(String staffId, double newSalary) {
        Staff staff = findById(staffId);
        if (staff == null) {
            throw new IllegalArgumentException("Staff member not found.");
        }

        staff.setSalary(newSalary); // will throw exception if negative
        boolean success = staffRepository.update(staff);
        return success ? staff : null;
    }

    /**
     * Updates staff working shift.
     *
     * @param staffId the staff ID
     * @param newShift the new shift
     * @return the updated Staff object
     */
    public Staff updateShift(String staffId, StaffShift newShift) {
        Staff staff = findById(staffId);
        if (staff == null) {
            throw new IllegalArgumentException("Staff member not found.");
        }

        staff.setShift(newShift);
        boolean success = staffRepository.update(staff);
        return success ? staff : null;
    }

    public Staff findById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return staffRepository.findById(id.trim());
    }

    public Staff findByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank() || phoneNumber.equalsIgnoreCase("N/A")) {
            return null;
        }
        for (Staff staff : staffRepository.findAll()) {
            if (phoneNumber.trim().equals(staff.getPhoneNumber())) {
                return staff;
            }
        }
        return null;
    }

    public List<Staff> findAll() {
        return staffRepository.findAll();
    }

    public boolean deleteStaff(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        return staffRepository.delete(id);
    }
}
