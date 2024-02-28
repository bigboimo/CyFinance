package com.example.demo.expenses;

import com.example.demo.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "expenses")
public class Expenses {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private float food;

    private float rentandBills;

    private float school;

    private float otherNeeds;

    private float misc;
    @OneToOne
    private User user;


    public Expenses(float food, float rentandBills, float school, float otherNeeds, float misc) {
        this.food = food;
        this.rentandBills = rentandBills;
        this.school = school;
        this.otherNeeds = otherNeeds;
        this.misc = misc;
    }

    public Expenses() {}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getFood() {
        return food;
    }

    public void setFood(float food) {
        this.food = food;
    }

    public float getRentandBills() {
        return rentandBills;
    }

    public void setRentandBills(float rentandBills) {
        this.rentandBills = rentandBills;
    }

    public float getSchool() {
        return school;
    }

    public void setSchool(float school) {
        this.school = school;
    }

    public float getOtherNeeds() {
        return otherNeeds;
    }

    public void setOtherNeeds(float otherNeeds) {
        this.otherNeeds = otherNeeds;
    }

    public float getMisc() {
        return misc;
    }

    public void setMisc(float misc) {
        this.misc = misc;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public String toString() {
        return "Expenses{" + "\"id\":" + id +
                ", \"food\":" + food +
                ", \"Rent and bills\":" + rentandBills +
                ", \"School\":" + school +
                ", \"Other necessities\":" + otherNeeds +
                ", \"Extras\":" + misc +
                '}';
    }
}
