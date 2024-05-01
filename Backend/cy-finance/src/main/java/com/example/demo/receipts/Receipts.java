package com.example.demo.receipts;

import com.example.demo.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name="receipts")
@Getter
@Setter
public class Receipts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @Column(nullable = false, unique = true)
    private String path;

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadedAt;

    private String label;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Receipts() {}

    public Receipts(String path) {
        this.path = path;
    }
}
