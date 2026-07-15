package com.gym.service;

import com.gym.enums.Gender;
import com.gym.enums.StaffRole;
import com.gym.enums.StaffShift;
import com.gym.model.Staff;
import com.gym.repository.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StaffServiceTesting {

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private StaffService staffService;

    private Staff staffAlice;

    @BeforeEach
    public void setUp() {
        Date dob = Date.valueOf(LocalDate.now().minusYears(30));
        Date hireDate = Date.valueOf(LocalDate.now());
        
        staffAlice = new Staff(
                "1",
                "Alice Admin",
                Gender.FEMALE,
                dob,
                1500.0,
                "099888777",
                "securepass",
                StaffRole.ADMIN,
                StaffShift.FULLTIME,
                hireDate
        );
    }

    @Test
    public void testRegisterStaff_ShouldSucceed_WhenPhoneNumberIsUnique() {
        // Arrange
        when(staffRepository.findAll()).thenReturn(Collections.emptyList());
        when(staffRepository.insert(staffAlice)).thenReturn(true);

        // Act
        boolean result = staffService.registerStaff(staffAlice);

        // Assert
        assertTrue(result);
        verify(staffRepository).insert(staffAlice);
    }

    @Test
    public void testRegisterStaff_ShouldThrowException_WhenPhoneNumberAlreadyExists() {
        // Arrange
        when(staffRepository.findAll()).thenReturn(Collections.singletonList(staffAlice));

        Date dob = Date.valueOf(LocalDate.now().minusYears(28));
        Staff newStaff = new Staff(
                "2",
                "Bob Cashier",
                Gender.MALE,
                dob,
                1000.0,
                "099888777", // duplicate phone
                "bobpass",
                StaffRole.CASHIER,
                StaffShift.MORNING,
                Date.valueOf(LocalDate.now())
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            staffService.registerStaff(newStaff);
        });

        verify(staffRepository, never()).insert(any());
    }

    @Test
    public void testAuthenticate_ShouldReturnStaff_WhenCredentialsAreValidByName() {
        // Arrange
        when(staffRepository.findAll()).thenReturn(Collections.singletonList(staffAlice));

        // Act
        Staff result = staffService.authenticate("Alice Admin", "securepass");

        // Assert
        assertNotNull(result);
        assertEquals(staffAlice.getId(), result.getId());
    }

    @Test
    public void testAuthenticate_ShouldReturnStaff_WhenCredentialsAreValidByPhone() {
        // Arrange
        when(staffRepository.findAll()).thenReturn(Collections.singletonList(staffAlice));

        // Act
        Staff result = staffService.authenticate("099888777", "securepass");

        // Assert
        assertNotNull(result);
        assertEquals(staffAlice.getId(), result.getId());
    }

    @Test
    public void testAuthenticate_ShouldThrowException_WhenPasswordIsIncorrect() {
        // Arrange
        when(staffRepository.findAll()).thenReturn(Collections.singletonList(staffAlice));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            staffService.authenticate("Alice Admin", "wrongpass");
        });
    }

    @Test
    public void testUpdateSalary_ShouldSucceed_WhenValid() {
        // Arrange
        when(staffRepository.findById("1")).thenReturn(staffAlice);
        when(staffRepository.update(staffAlice)).thenReturn(true);

        // Act
        Staff result = staffService.updateSalary("1", 2000.0);

        // Assert
        assertNotNull(result);
        assertEquals(2000.0, staffAlice.getSalary());
        verify(staffRepository).update(staffAlice);
    }
}
