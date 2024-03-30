package com.mycompany.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CEO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "ceo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Company> companies = new ArrayList<>();

    // Constructors
    public CEO() {
    }

    public CEO(String name) {
        this.name = name;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    // Helper method to add a company to the CEO
    public void addCompany(Company company) {
        companies.add(company);
        company.setCeo(this);
    }

    // Helper method to remove a company from the CEO
    public void removeCompany(Company company) {
        companies.remove(company);
        company.setCeo(null);
    }
}
