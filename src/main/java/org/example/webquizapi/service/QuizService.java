package org.example.webquizapi.service;

import org.example.webquizapi.dto.CompletionDto;
import org.example.webquizapi.dto.QuizDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface QuizService {
    List<QuizDto> getAll();

    List<QuizDto> getByAuthor(String email);

    QuizDto getById(Long id);

    QuizDto save(QuizDto dto);

    boolean solveQuiz(int id, List<Integer> answer, String email);

    Page<CompletionDto> getSolvedQuizzesByUser(String email, int pageNumber);
}
