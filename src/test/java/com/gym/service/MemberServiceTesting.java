package com.gym.service;

import com.gym.enums.Gender;
import com.gym.enums.MemberStatus;
import com.gym.model.Member;
import com.gym.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTesting {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    public void testRegisterMember_ShouldReturnTrue_WhenMemberIsUnique() {
        // Arrange
        Date dob = Date.valueOf(LocalDate.now().minusYears(25));
        Member member = new Member("1", "Alice Smith", Gender.FEMALE, "123456789", dob, MemberStatus.INACTIVE);

        // Tell mock repository to simulate unique status
        when(memberRepository.findById("1")).thenReturn(null);
        when(memberRepository.findByPhoneNumber("123456789")).thenReturn(null);
        when(memberRepository.insert(member)).thenReturn(true);

        // Act
        boolean result = memberService.registerMember(member);

        // Assert
        assertTrue(result);
        verify(memberRepository).insert(member);
    }

    @Test
    public void testRegisterMember_ShouldThrowException_WhenIdAlreadyExists() {
        // Arrange
        Date dob = Date.valueOf(LocalDate.now().minusYears(25));
        Member member = new Member("1", "Alice Smith", Gender.FEMALE, "123456789", dob, MemberStatus.INACTIVE);
        Member existingMember = new Member("1", "John Doe", Gender.MALE, "987654321", dob, MemberStatus.ACTIVE);

        when(memberRepository.findByID("1")).thenReturn(existingMember);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            memberService.registerMember(member);
        });

        assertEquals("Member with ID 1 is already registered.", exception.getMessage());
        // Verify insert is NEVER called
        verify(memberRepository, never()).insert(any(Member.class));
    }

    @Test
    public void testRegisterMember_ShouldThrowException_WhenPhoneNumberAlreadyExists() {
        // Arrange
        Date dob = Date.valueOf(LocalDate.now().minusYears(25));
        Member member = new Member("1", "Alice Smith", Gender.FEMALE, "123456789", dob, MemberStatus.INACTIVE);
        Member existingPhoneMember = new Member("2", "John Doe", Gender.MALE, "123456789", dob, MemberStatus.ACTIVE);

        // Simulate unique ID check but duplicate Phone check
        when(memberRepository.findById("1")).thenReturn(null);
        when(memberRepository.findByPhoneNumber("123456789")).thenReturn(existingPhoneMember);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            memberService.registerMember(member);
        });

        assertEquals("Member with phone number 123456789 already exists.", exception.getMessage());
        verify(memberRepository, never()).insert(any(Member.class));
    }
}
