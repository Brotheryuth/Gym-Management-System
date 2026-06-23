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
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTesting {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Member member;
    private MembershipPlan plan;
    private Membership membership;
    private Payment pendingPayment;
    private Payment paidPayment;

    @BeforeEach
    public void setUp() {
        Date dob = Date.valueOf(LocalDate.now().minusYears(25));
        Date startDate = Date.valueOf(LocalDate.now());
        Date endDate = Date.valueOf(LocalDate.now().plusMonths(3));

        member = new Member("1", "Alice Smith", Gender.FEMALE, "123456789", dob, MemberStatus.INACTIVE);
        plan = new MembershipPlan("PLAN-100", "3 Month Plan", 50.0, 3);
        membership = new Membership("MS-100", member, plan, startDate, endDate, MembershipStatus.PENDING);
        
        pendingPayment = new Payment(
                "PAY-100",
                membership,
                50.0,
                0,
                PaymentMethod.BYCASH,
                PaymentStatus.PENDING,
                LocalDateTime.now().minusHours(1),
                null
        );

        paidPayment = new Payment(
                "PAY-200",
                membership,
                50.0,
                0,
                PaymentMethod.BYCASH,
                PaymentStatus.PAID,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
        );
    }

    @Test
    public void testProcessPayment_ShouldSucceedAndActivateEntities() {
        // Arrange
        when(paymentRepository.findById("PAY-100")).thenReturn(pendingPayment);
        when(paymentRepository.update(pendingPayment)).thenReturn(true);

        // Act
        boolean result = paymentService.processPayment("PAY-100", PaymentMethod.KHQR);

        // Assert
        assertTrue(result);
        assertEquals(PaymentStatus.PAID, pendingPayment.getStatus());
        assertEquals(PaymentMethod.KHQR, pendingPayment.getMethod());
        assertNotNull(pendingPayment.getPaymentDate());

        // Verify status changes cascaded to Membership and Member
        assertEquals(MembershipStatus.ACTIVE, membership.getStatus());
        assertEquals(MemberStatus.ACTIVE, member.getMemberStatus());

        // Verify repository update calls
        verify(paymentRepository).update(pendingPayment);
        verify(membershipRepository).update(membership);
        verify(memberRepository).update(member);
    }

    @Test
    public void testProcessPayment_ShouldThrowException_WhenPaymentNotFound() {
        // Arrange
        when(paymentRepository.findById("PAY-INVALID")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment("PAY-INVALID", PaymentMethod.BYCASH);
        });

        verify(paymentRepository, never()).update(any());
        verify(membershipRepository, never()).update(any());
        verify(memberRepository, never()).update(any());
    }

    @Test
    public void testProcessPayment_ShouldThrowException_WhenAlreadyPaid() {
        // Arrange
        when(paymentRepository.findById("PAY-200")).thenReturn(paidPayment);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            paymentService.processPayment("PAY-200", PaymentMethod.BYCASH);
        });

        verify(paymentRepository, never()).update(any());
        verify(membershipRepository, never()).update(any());
        verify(memberRepository, never()).update(any());
    }

    @Test
    public void testDeletePayment_ShouldSucceed_WhenPending() {
        // Arrange
        when(paymentRepository.findById("PAY-100")).thenReturn(pendingPayment);
        when(paymentRepository.delete("PAY-100")).thenReturn(true);

        // Act
        boolean result = paymentService.deletePayment("PAY-100");

        // Assert
        assertTrue(result);
        verify(paymentRepository).delete("PAY-100");
    }

    @Test
    public void testDeletePayment_ShouldThrowException_WhenPaid() {
        // Arrange
        when(paymentRepository.findById("PAY-200")).thenReturn(paidPayment);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            paymentService.deletePayment("PAY-200");
        });

        verify(paymentRepository, never()).delete(anyString());
    }
}
