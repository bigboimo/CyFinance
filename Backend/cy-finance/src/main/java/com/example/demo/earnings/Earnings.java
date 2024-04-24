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
        this.primaryMonthlyIncome = primaryMonthlyIncome;
        this.secondaryMonthlyIncome = secondaryMonthlyIncome;
        this.user = user;
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
