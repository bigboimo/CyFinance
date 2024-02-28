package com.example.demo.earnings;

import com.example.demo.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Earnings {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private float primaryMonthlyIncome;

    private float secondaryMonthlyIncome;



    @OneToOne
    @JsonIgnore
    private User user;

    public Earnings(float first, float second){
        this.primaryMonthlyIncome = first;
        this.secondaryMonthlyIncome = second;
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
                ", \"food\":" + primaryMonthlyIncome +
                ", \"rantandBills\":" + secondaryMonthlyIncome +
                '}';
    }
}
