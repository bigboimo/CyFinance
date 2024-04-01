package com.example.demo.users;

import com.example.demo.earnings.Earnings;
import com.example.demo.expenses.Expenses;
import com.example.demo.userGroups.Groups;
import com.example.demo.netWorth.NetWorth;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    private String role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "net_worth_id")
    private NetWorth netWorth;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "earnings_id")
    private Earnings earnings;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "expenses_id")
    private Expenses expenses;

    @ManyToMany
    @JoinTable(
            name = "course_like",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Groups> groups;

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public NetWorth getNetWorth() {
        return netWorth;
    }

    public void setNetWorth(NetWorth netWorth) {
        this.netWorth = netWorth;
    }

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

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                "\"name\":\"" + name + '\"' +
                ", \"email\":\"" + email + '\"' +
                ", \"password\":\"" + password + '\"' +
                ", \"role\":\"" + role + '\"' +
                '}';
    }
}
