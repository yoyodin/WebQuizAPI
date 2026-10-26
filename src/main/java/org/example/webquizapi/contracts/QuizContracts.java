package org.example.webquizapi.contracts;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class QuizContracts {
    public record CreateQuizRequest(@NotNull @NotEmpty String title,
                                    @NotNull @NotEmpty String text,
                                    @NotNull @Size(min = 2) List<String> options,
                                    List<Integer> answer) {}

    public record QuizResponse(Long id,
                               String title,
                               String text,
                               String author,
                               List<String> options,
                               List<Integer> answer) {}
}
