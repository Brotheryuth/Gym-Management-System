package com.gym.service;

import com.gym.model.*;
import com.gym.enums.*;
import com.gym.repository.MembershipPlanRepository;
import com.gym.repository.MembershipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MembershipPlanServiceTesting {

    @Mock
    private MembershipPlanRepository planRepository;

    @Mock
    private MembershipRepository membershipRepository;

    @InjectMocks
    private MembershipPlanService planService;

    private MembershipPlan plan3Months;
    private MembershipPlan plan6Months;

    @BeforeEach
    public void setUp() {
        plan3Months = new MembershipPlan("PLAN-100", 50.0, 3);
        plan6Months = new MembershipPlan("PLAN-200", 90.0, 6);
    }

    @Test
    public void testCreatePlan_ShouldSucceed_WhenDurationIsUnique() {
        // Arrange
        when(planRepository.findAll()).thenReturn(Collections.singletonList(plan6Months));
        when(planRepository.insert(any(MembershipPlan.class))).thenReturn(true);

        // Act
        MembershipPlan result = planService.createPlan(50.0, 3);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getDuration());
        assertEquals(50.0, result.getPlanPrice());
        verify(planRepository).insert(any(MembershipPlan.class));
    }

    @Test
    public void testCreatePlan_ShouldThrowException_WhenDurationAlreadyExists() {
        // Arrange
        when(planRepository.findAll()).thenReturn(Collections.singletonList(plan3Months));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            planService.createPlan(60.0, 3);
        });

        verify(planRepository, never()).insert(any());
    }

    @Test
    public void testDeletePlan_ShouldSucceed_WhenNotAssignedToAnyMemberships() {
        // Arrange
        when(membershipRepository.findAll()).thenReturn(Collections.emptyList());
        when(planRepository.delete("PLAN-100")).thenReturn(true);

        // Act
        boolean result = planService.deletePlan("PLAN-100");

        // Assert
        assertTrue(result);
        verify(planRepository).delete("PLAN-100");
    }

    @Test
    public void testDeletePlan_ShouldThrowException_WhenAssignedToActiveMemberships() {
        // Arrange
        Date dob = Date.valueOf(LocalDate.now().minusYears(25));
        Date startDate = Date.valueOf(LocalDate.now());
        Date endDate = Date.valueOf(LocalDate.now().plusMonths(3));
        Member member = new Member("1", "Alice Smith", Gender.FEMALE, "123456789", dob, MemberStatus.ACTIVE);
        
        Membership membership = new Membership("MS-100", member, plan3Months, startDate, endDate, MembershipStatus.ACTIVE);
        
        when(membershipRepository.findAll()).thenReturn(Collections.singletonList(membership));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            planService.deletePlan("PLAN-100");
        });

        verify(planRepository, never()).delete(anyString());
    }

    @Test
    public void testUpdatePlan_ShouldSucceed_WhenInputsAreValidAndDurationUnchanged() {
        // Arrange
        when(planRepository.findById("PLAN-100")).thenReturn(plan3Months);
        when(planRepository.update(plan3Months)).thenReturn(true);

        // Act
        MembershipPlan updated = planService.updatePlan("PLAN-100", 55.0, 3);

        // Assert
        assertNotNull(updated);
        assertEquals(55.0, plan3Months.getPlanPrice());
        verify(planRepository).update(plan3Months);
    }

    @Test
    public void testUpdatePlan_ShouldThrowException_WhenDurationUpdatedToExistingDuration() {
        // Arrange
        when(planRepository.findById("PLAN-100")).thenReturn(plan3Months);
        
        // Mock getPlanByDuration to find a duplicate
        List<MembershipPlan> allPlans = new ArrayList<>();
        allPlans.add(plan3Months);
        allPlans.add(plan6Months);
        when(planRepository.findAll()).thenReturn(allPlans);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            planService.updatePlan("PLAN-100", 50.0, 6);
        });

        verify(planRepository, never()).update(any());
    }
}
