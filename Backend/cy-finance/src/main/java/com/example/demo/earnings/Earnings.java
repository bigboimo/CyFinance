package com.example.demo.earnings;

import com.example.demo.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents the earnings information of a user with a many-to-one relationship,
 * indicating that a user can have multiple earnings records. This class manages
 * details about both primary and secondary monthly income of the user.
 */
@Entity
@Table(name = "earnings")
public class Earnings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private float primaryMonthlyIncome;
    private float secondaryMonthlyIncome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email")
    @JsonIgnore // This annotation prevents recursion in JSON serialization.
    private User user;

    // Default constructor for JPA
    public Earnings() {
    }

    // Constructor with all parameters
    public Earnings(float primaryMonthlyIncome, float secondaryMonthlyIncome, User user) {
        if (user == null) {
            throw new NullPointerException("User cannot be null");
        }
        this.primaryMonthlyIncome = primaryMonthlyIncome;
        this.secondaryMonthlyIncome = secondaryMonthlyIncome;
        this.user = user;
    }

    /*
    This method is used to determine logical equality between two Earnings objects based on their properties.
    This is crucial for comparisons within collections or when you need to check if two instances represent the same logical data in tests or in business logic.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Checks if the references are the same - a quick win optimization.
        if (o == null || getClass() != o.getClass()) return false; // Checks if the object is not null and is of the same class.

        Earnings earnings = (Earnings) o; // Type casting the object to an Earnings object for comparison.
        return id == earnings.id && // Compares IDs.
                Float.compare(earnings.primaryMonthlyIncome, primaryMonthlyIncome) == 0 && // Compares primary incomes for equality.
                Float.compare(earnings.secondaryMonthlyIncome, secondaryMonthlyIncome) == 0 && // Compares secondary incomes for equality.
                (user != null ? user.equals(earnings.user) : earnings.user == null); // Compares user objects considering nulls.
    }

    /*
    The hashCode method must be consistent with equals (if objects are equal according to equals(), they must have the same hash code)
    This is used in hash-based collections like HashMap and HashSet.
   */
    @Override
    public int hashCode() {
        int result = id; // Start with the hash of the ID.
        result = 31 * result + (primaryMonthlyIncome != +0.0f ? Float.floatToIntBits(primaryMonthlyIncome) : 0); // Hash primary income.
        result = 31 * result + (secondaryMonthlyIncome != +0.0f ? Float.floatToIntBits(secondaryMonthlyIncome) : 0); // Hash secondary income.
        result = 31 * result + (user != null ? user.hashCode() : 0); // Hash the user, accounting for null.
        return result;
    }


    // Getter and setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getPrimaryMonthlyIncome() {
        return primaryMonthlyIncome;
    }

    public void setPrimaryMonthlyIncome(float primaryMonthlyIncome) {
        this.primaryMonthlyIncome = primaryMonthlyIncome;
    }

    public float getSecondaryMonthlyIncome() {
        return secondaryMonthlyIncome;
    }

    public void setSecondaryMonthlyIncome(float secondaryMonthlyIncome) {
        this.secondaryMonthlyIncome = secondaryMonthlyIncome;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Earnings{" +
                "id=" + id +
                ", primaryMonthlyIncome=" + primaryMonthlyIncome +
                ", secondaryMonthlyIncome=" + secondaryMonthlyIncome +
                '}';
    }
}
