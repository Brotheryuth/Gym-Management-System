package com.gym.model;

import com.gym.enums.Gender;
import com.gym.enums.MemberStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;

public class MemberTesting {

    @Test
    public void testGetAge_ShouldCalculateCorrectYears() {
        // 1. Arrange: Create a member born 25 years ago
        Date dob = Date.valueOf(LocalDate.now().minusYears(25));
        Member member = new Member("John Doe", Gender.MALE, "123456789", dob, MemberStatus.ACTIVE);

        // 2. Act: Calculate the age
        int calculatedAge = member.getAge();

        // 3. Assert: Verify the age is exactly 25
        assertEquals(25, calculatedAge);
    }

    @Test
    public void testSetDob_ShouldFallbackToDefaultAgeIfTooYoung() {
        // 1. Arrange: Create a member with a birthdate only 2 years ago (invalid: must be >= 5)
        Date invalidDob = Date.valueOf(LocalDate.now().minusYears(2));
        Member member = new Member("Baby Doe", Gender.MALE, "123456789", invalidDob, MemberStatus.ACTIVE);

        // 2. Act & Assert: The setter logs a warning and falls back to 18 years ago
        int expectedAge = 18;
        assertEquals(expectedAge, member.getAge());
    }
}
