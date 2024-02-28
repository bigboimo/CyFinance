package com.example.demo.netWorth;

import com.example.demo.users.User;
import jakarta.persistence.*;

@Entity
@Table(name = "net_worth")
public class NetWorth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private float assets;
    private float liabilities;
    @OneToOne
    private User user;


    public NetWorth(float assets, float liabilities) {
        this.assets = assets;
        this.liabilities = liabilities;
    }

    public NetWorth() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getAssets() {
        return assets;
    }

    public void setAssets(float assets) {
        this.assets = assets;
    }

    public float getLiabilities() {
        return liabilities;
    }

    public void setLiabilities(float liabilities) {
        this.liabilities = liabilities;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "NetWorth{" +
                "\"id\":" + id +
                ", \"assets\":" + assets +
                ", \"liabilities\":" + liabilities +
                '}';
    }
}
