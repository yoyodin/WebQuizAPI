package org.example.webquizapi.controller;

import jakarta.validation.Valid;
import org.example.webquizapi.contracts.QuizContracts.*;
import org.example.webquizapi.dto.QuizDto;
import org.example.webquizapi.security.AppUserAdapter;
import org.example.webquizapi.service.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quiz")
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public List<QuizResponse> getQuizzesByAuthor(@AuthenticationPrincipal AppUserAdapter appUserAdapter) {
        return quizService.getByAuthor(appUserAdapter.getUsername())
                .stream()
                .map(q -> new QuizResponse(q.getId(), q.getTitle(), q.getText(), q.getAuthor(), q.getOptions(), q.getAnswer()))
                .toList();
    }

    @GetMapping("/solved")
    public Page<CompletionResponse> getSolvedByAuthor(@RequestParam int page,
                                                      @AuthenticationPrincipal AppUserAdapter appUserAdapter) {
        return quizService.getSolvedQuizzesByUser(appUserAdapter.getUsername(), page)
                .map(c -> new CompletionResponse(c.getTestId(), c.getCompletedAt()));
    }

    @GetMapping("/all")
    public List<QuizResponse> getAllQuizzes() {
        return quizService.getAll()
                .stream()
                .map(q -> new QuizResponse(q.getId(), q.getTitle(), q.getText(), q.getAuthor(), q.getOptions(), q.getAnswer()))
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> createQuiz(@Valid @RequestBody CreateQuizRequest quizRequest,
                                        @AuthenticationPrincipal AppUserAdapter appUserAdapter) {
        QuizDto dto = new QuizDto(null, quizRequest.title(), quizRequest.text(), quizRequest.options(), quizRequest.answer(), appUserAdapter.getUsername());
        dto = quizService.save(dto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{id}/solve")
    public SolveQuizResponse solveQuiz(@PathVariable int id,
                                       @RequestBody SolveQuizRequest solveQuizRequest,
                                       @AuthenticationPrincipal AppUserAdapter appUserAdapter) {
        boolean solved = quizService.solveQuiz(id, solveQuizRequest.answer(), appUserAdapter.getUsername());
        String feedback = solved ? "Congratulations, you're right!" : "Wrong answer! Please, try again.";
        return new SolveQuizResponse(solved, feedback);
    }
}
