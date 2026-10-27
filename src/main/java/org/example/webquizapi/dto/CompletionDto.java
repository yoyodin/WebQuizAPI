package org.example.webquizapi.dto;

import java.time.OffsetDateTime;

public class CompletionDto {
    private Long testId;
    private OffsetDateTime completedAt;

    public CompletionDto(Long testId, OffsetDateTime completedAt) {
        this.testId = testId;
        this.completedAt = completedAt;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
