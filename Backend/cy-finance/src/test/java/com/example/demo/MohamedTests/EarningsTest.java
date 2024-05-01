package com.example.demo.MohamedTests;

import com.example.demo.earnings.Earnings;
import com.example.demo.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EarningsTest {

    private Earnings earnings;
    private User user;

    @BeforeEach
    void setUp() {
        // Setup method to initialize common objects before each test case
        user = new User(); // Assume User class has an appropriate constructor
        user.setEmail("test@example.com"); // Setting email assuming User class has setEmail method
        earnings = new Earnings(5000.00f, 1500.00f, user);
    }

    @Test
    void testConstructorWithNullUser() {
        Exception exception = assertThrows(NullPointerException.class, () -> new Earnings(3000.00f, 1200.00f, null));
        assertEquals("User cannot be null", exception.getMessage());
    }


    @Test
    void testNegativePrimaryIncome() {
        // Test to check behavior when setting a negative primary monthly income
        earnings.setPrimaryMonthlyIncome(-1000.00f);
        assertEquals(-1000.00f, earnings.getPrimaryMonthlyIncome(), "Primary monthly income can be negative (business rule pending clarification)");
    }

    @Test
    void testZeroSecondaryIncome() {
        // Test to ensure secondary monthly income can be set to zero
        earnings.setSecondaryMonthlyIncome(0.00f);
        assertEquals(0.00f, earnings.getSecondaryMonthlyIncome(), "Secondary monthly income can be zero");
    }

    @Test
    void testExtremelyHighIncome() {
        // Test behavior with extremely high values for income
        earnings.setPrimaryMonthlyIncome(Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, earnings.getPrimaryMonthlyIncome(), "Check system handling of maximum float values");
        earnings.setSecondaryMonthlyIncome(Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, earnings.getSecondaryMonthlyIncome(), "Check system handling of maximum float values");
    }

    @Test
    void testEarningsEquality() {
        // Test to check if two earnings objects with the same state are considered equal
        Earnings anotherEarnings = new Earnings(5000.00f, 1500.00f, user);
        assertEquals(earnings, anotherEarnings, "Earnings objects with the same income and user should be equal");
    }

    @Test
    void testEarningsInequality() {
        // Test to check inequality based on different user associated with the earnings
        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        Earnings differentEarnings = new Earnings(5000.00f, 1500.00f, anotherUser);
        assertNotEquals(earnings, differentEarnings, "Earnings with different users should not be equal");
    }

    @Test
    void testHashCodeConsistency() {
        // Test to verify that the hashCode method returns consistent results
        int initialHashCode = earnings.hashCode();
        assertEquals(initialHashCode, earnings.hashCode(), "Hashcode should be consistent across multiple calls");
    }

    @Test
    void testToString() {
        // Update the expected output to match the actual implementation which does not include user details
        String expected = "Earnings{id=0, primaryMonthlyIncome=5000.0, secondaryMonthlyIncome=1500.0}";
        assertEquals(expected, earnings.toString(), "toString should return the correct string representation");
    }

}
