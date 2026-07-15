package com.gym.service;

import com.gym.enums.*;
import com.gym.model.*;
import com.gym.repository.MemberRepository;
import com.gym.repository.MembershipRepository;
import com.gym.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTesting {

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private MembershipService membershipService;

    private Member member;
    private MembershipPlan plan;
    private Membership activeMembership;
    private Membership pendingMembership;

    @BeforeEach
    public void setUp() {
        Date dob = Date.valueOf(LocalDate.now().minusYears(25));
        Date startDate = Date.valueOf(LocalDate.now().minusDays(10));
        Date endDate = Date.valueOf(LocalDate.now().plusMonths(3));

        member = new Member("1", "Alice Smith", Gender.FEMALE, "123456789", dob, MemberStatus.ACTIVE);
        plan = new MembershipPlan("PLAN-100", "3 Month Plan", 50.0, 3);
        activeMembership = new Membership("MS-100", member, plan, startDate, endDate, MembershipStatus.ACTIVE);
        
        pendingMembership = new Membership("MS-200", member, plan, startDate, endDate, MembershipStatus.PENDING);
    }

    @Test
    public void testSubscribeMember_ShouldSucceed_AndCreatePayment() {
        // Arrange
        Date start = Date.valueOf(LocalDate.now());
        when(membershipRepository.findMemberByID("1")).thenReturn(null); // No existing subscription
        when(membershipRepository.insert(any(Membership.class))).thenReturn(true);
        when(paymentRepository.insert(any(Payment.class))).thenReturn(true);

        // Act
        Membership result = membershipService.subscribeMember(member, plan, start, 10, PaymentMethod.KHQR);

        // Assert
        assertNotNull(result);
        assertEquals(member, result.getMember());
        assertEquals(plan, result.getPlan());
        
        verify(membershipRepository).insert(any(Membership.class));
        verify(paymentRepository).insert(any(Payment.class));
    }

    @Test
    public void testSubscribeMember_ShouldThrowException_WhenAlreadySubscribed() {
        // Arrange
        Date start = Date.valueOf(LocalDate.now());
        // Mock that member already has an active membership
        when(membershipRepository.findMemberByID("1")).thenReturn(activeMembership);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            membershipService.subscribeMember(member, plan, start, 0, PaymentMethod.BYCASH);
        });

        verify(membershipRepository, never()).insert(any());
        verify(paymentRepository, never()).insert(any());
    }

    @Test
    public void testCancelMembership_ShouldSucceed_AndDeactivateMember() {
        // Arrange
        when(membershipRepository.findById("MS-100")).thenReturn(activeMembership);
        when(membershipRepository.update(activeMembership)).thenReturn(true);

        // Act
        boolean result = membershipService.cancelMembership("MS-100");

        // Assert
        assertTrue(result);
        assertEquals(MembershipStatus.CANCELLED, activeMembership.getStatus());
        assertEquals(MemberStatus.INACTIVE, member.getMemberStatus());

        verify(membershipRepository).update(activeMembership);
        verify(memberRepository).update(member);
    }

    @Test
    public void testCheckAndExpireMemberships_ShouldExpirePastSubscriptions() {
        // Arrange
        // Let's create an active membership whose end date was 5 days ago
        Date pastEndDate = Date.valueOf(LocalDate.now().minusDays(5));
        Membership expiredMs = new Membership("MS-300", member, plan, Date.valueOf(LocalDate.now().minusMonths(1)), pastEndDate, MembershipStatus.ACTIVE);
        
        // Let's create another active membership whose end date is in the future
        Date futureEndDate = Date.valueOf(LocalDate.now().plusDays(5));
        Membership runningMs = new Membership("MS-400", member, plan, Date.valueOf(LocalDate.now()), futureEndDate, MembershipStatus.ACTIVE);

        when(membershipRepository.findAll()).thenReturn(Arrays.asList(expiredMs, runningMs));

        // Act
        int expiredCount = membershipService.checkAndExpireMemberships();

        // Assert
        assertEquals(1, expiredCount);
        assertEquals(MembershipStatus.EXPIRED, expiredMs.getStatus());
        assertEquals(MemberStatus.INACTIVE, member.getMemberStatus());
        assertEquals(MembershipStatus.ACTIVE, runningMs.getStatus()); // remains active

        verify(membershipRepository, times(1)).update(expiredMs);
        verify(memberRepository, times(1)).update(member);
        verify(membershipRepository, never()).update(runningMs);
    }
}
