package com.example.demo.users;

import com.example.demo.assets.Assets;
import com.example.demo.earnings.Earnings;
import com.example.demo.expenses.Expenses;
import com.example.demo.userGroups.Groups;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name="users")
public class User {

    @Id
    private String email;

    private String name;
    private String password;
    private String role;
    private int netWorth;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Assets> assets;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "earnings_id")
    private Earnings earnings;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "expenses_id")
    private Expenses expenses;

    @ManyToMany
    @JoinTable(
            name = "users_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Groups> groups;

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User() {}

    public Earnings getEarnings() {
        return earnings;
    }

    public void setEarnings(Earnings earnings) {
        this.earnings = earnings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getNetWorth() {
        return netWorth;
    }

    public void setNetWorth(int netWorth) {
        this.netWorth = netWorth;
    }

    public Set<Assets> getAssets() {
        return assets;
    }

    public void addAsset(Assets asset) {
        this.assets.add(asset);
    }

    public void removeAsset(Assets asset) { this.assets.remove(asset); }

    public void setAssets(Set<Assets> assets) { this.assets = assets; }

    public Expenses getExpenses() {
        return expenses;
    }

    public void setExpenses(Expenses expenses) {
        this.expenses = expenses;
    }

    public Set<Groups> getGroups() {
        return groups;
    }

    public void addGroups(Groups group) {
        this.groups.add(group);
    }

    public void removeGroups(Groups group) { this.groups.remove(group); }

    public void setGroups(Set<Groups> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + '\"' +
                ", \"email\":\"" + email + '\"' +
                ", \"password\":\"" + password + '\"' +
                ", \"role\":\"" + role + '\"' +
                '}';
    }
}
