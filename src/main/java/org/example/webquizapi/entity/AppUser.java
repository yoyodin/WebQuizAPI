package org.example.webquizapi.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class AppUser {
    @Id
    @GeneratedValue
    private Long id;
    private String email;
    private String password;

    @OneToMany(mappedBy = "creator")
    private List<Quiz> createdTests = new ArrayList<>();

    @OneToMany(mappedBy = "appUser")
    private List<Completion> completions = new ArrayList<>();

    public AppUser() {
    }

    public AppUser(Long id, String email, String password, List<Quiz> createdTests, List<Completion> completions) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.createdTests = createdTests;
        this.completions = completions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Quiz> getCreatedTests() {
        return createdTests;
    }

    public void setCreatedTests(List<Quiz> createdTests) {
        this.createdTests = createdTests;
    }

    public List<Completion> getCompletions() {
        return completions;
    }

    public void setCompletions(List<Completion> completions) {
        this.completions = completions;
    }
}
