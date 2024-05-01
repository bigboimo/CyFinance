package com.example.demo.users;

import com.example.demo.assets.Assets;
import com.example.demo.earnings.Earnings;
import com.example.demo.expenses.Expenses;
import com.example.demo.liabilities.Liabilities;
import com.example.demo.receipts.Receipts;
import com.example.demo.userGroups.Groups;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


/**
 * The User entity represents a user in the system.
 * It has relationships with several other entities:
 * - One-to-Many with Assets: A user can own multiple assets.
 * - One-to-Many with Earnings: A user can have multiple earnings records.
 * - One-to-Many with Expenses: A user has a single record of expenses.
 * - Many-to-Many with Groups: A user can belong to multiple groups.
 */


@Entity
@Table(name="users")
public class User {

    @Id
    private String email;

    private String name;
    private String password;
    private String role;
    @Column(columnDefinition = "integer default 0")
    private int assetsTotal;
    @Column(columnDefinition = "integer default 0")
    private int liabilitiesTotal;
    @Column(columnDefinition = "integer default 0")
    private int netWorth;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Assets> assets;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Liabilities> liabilities;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Earnings> earnings;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Expenses> expenses;

    @JsonIgnore
    private String profilePictureFile;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @Getter
    @Setter
    private Set<Receipts> receipts;

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

    public Set<Earnings> getEarnings() {
        return earnings;
    }

    public void setEarnings(Set<Earnings> earnings) {
        this.earnings = earnings;
    }

    public void addEarnings(Earnings earning) {
        this.earnings.add(earning);
        earning.setUser(this);
    }

    public void removeEarnings(Earnings earning) {
        this.earnings.remove(earning);
        earning.setUser(null);
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

    public void addAssets(Assets assets) {
        this.assets.add(assets);
    }

    public void removeAssets(Assets assets) { this.assets.remove(assets); }

    public Set<Liabilities> getLiabilities() {
        return liabilities;
    }

    public void addLiabilities(Liabilities liabilities) {
        this.liabilities.add(liabilities);
    }

    public void removeLiabilities(Liabilities liabilities) { this.liabilities.remove(liabilities); }

    public void setAssets(Set<Assets> assets) { this.assets = assets; }

    public Set<Expenses> getExpenses() {
        return expenses;
    }

    public void setExpenses(Set<Expenses> expenses) {
        this.expenses = expenses;
    }

    public void addExpense(Expenses expense) {
        this.expenses.add(expense);
        expense.setUser(this); // Assuming `Expenses` has a `setUser(User user)` method for the back-reference.
    }

    public void removeExpense(Expenses expense) {
        this.expenses.remove(expense);
        expense.setUser(null); // This disassociates the expense from the user.
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

    public int getAssetsTotal() {
        return assetsTotal;
    }

    public void setAssetsTotal(int assetsTotal) {
        this.assetsTotal = assetsTotal;
    }

    public int getLiabilitiesTotal() {
        return liabilitiesTotal;
    }

    public void setLiabilitiesTotal(int liabilitiesTotal) {
        this.liabilitiesTotal = liabilitiesTotal;
    }

    public String getProfilePictureFile() {
        return profilePictureFile;
    }

    public void setProfilePictureFile(String profilePictureFile) {
        this.profilePictureFile = profilePictureFile;
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
