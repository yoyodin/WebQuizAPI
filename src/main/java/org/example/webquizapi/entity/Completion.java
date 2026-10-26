package org.example.webquizapi.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"app_user_id", "quiz_id"}))
public class Completion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    private OffsetDateTime completedAt;

    public Completion() {
    }

    public Completion(Long id, AppUser appUser, Quiz quiz, OffsetDateTime completedAt) {
        this.id = id;
        this.appUser = appUser;
        this.quiz = quiz;
        this.completedAt = completedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
