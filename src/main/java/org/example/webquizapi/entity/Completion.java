package org.example.webquizapi.entity;

import jakarta.persistence.Entity;

import java.time.OffsetDateTime;

@Entity
public class Completion {
    private OffsetDateTime completedAt;

    public Completion() {
    }

    public Completion(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
