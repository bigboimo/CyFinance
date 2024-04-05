package com.example.demo.earnings;

import com.example.demo.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * The Earnings entity represents the earnings information of a user.
 * It is linked to the User entity in a many-to-one relationship,
 * meaning a user can have multiple earnings records.
 */


@Entity
@Table(name = "earnings")
public class Earnings {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private float primaryMonthlyIncome;

    private float secondaryMonthlyIncome;



    // Many-to-One relationship with User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email", nullable = false)
    @JsonIgnore // Prevents recursion in JSON serialization
    private User user;

    public Earnings(float primaryMonthlyIncome, float secondaryMonthlyIncome){
        this.primaryMonthlyIncome = primaryMonthlyIncome;
        this.secondaryMonthlyIncome = secondaryMonthlyIncome;
    }

    public Earnings() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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
    @Override
    public String toString() {
        return "Earnings{" + "\"id\":" + id +
                ", \"primaryMonthlyIncome\":" + primaryMonthlyIncome +
                ", \"secondaryMonthlyIncome\":" + secondaryMonthlyIncome +
                '}';
    }
}
