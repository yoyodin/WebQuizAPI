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
    public ResponseEntity<List<QuizResponse>> getQuizzesByAuthor(@AuthenticationPrincipal AppUserAdapter appUserAdapter) {
        List<QuizResponse> quizzes = quizService.getByAuthor(appUserAdapter.getUsername())
                .stream()
                .map(q -> new QuizResponse(q.getId(), q.getTitle(), q.getText(), q.getAuthor(), q.getOptions(), q.getAnswer()))
                .toList();

        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizDto> getQuizById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getById(id));
    }

    @GetMapping("/solved")
    public ResponseEntity<Page<CompletionResponse>> getSolvedByAuthor(@RequestParam int page,
                                                                      @AuthenticationPrincipal AppUserAdapter appUserAdapter) {
        Page<CompletionResponse> quizzes = quizService.getSolvedQuizzesByUser(appUserAdapter.getUsername(), page)
                .map(c -> new CompletionResponse(c.getTestId(), c.getCompletedAt()));

        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/all")
    public ResponseEntity<List<QuizResponse>> getAllQuizzes() {
        List<QuizResponse> quizzes = quizService.getAll()
                .stream()
                .map(q -> new QuizResponse(q.getId(), q.getTitle(), q.getText(), q.getAuthor(), q.getOptions(), q.getAnswer()))
                .toList();

        return ResponseEntity.ok(quizzes);
    }

    @PostMapping
    public ResponseEntity<QuizDto> createQuiz(@Valid @RequestBody CreateQuizRequest quizRequest,
                                              @AuthenticationPrincipal AppUserAdapter appUserAdapter) {
        QuizDto dto = new QuizDto(null, quizRequest.title(), quizRequest.text(), quizRequest.options(), quizRequest.answer(), appUserAdapter.getUsername());
        dto = quizService.save(dto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/{id}/solve")
    public ResponseEntity<SolveQuizResponse> solveQuiz(@PathVariable int id,
                                                       @RequestBody SolveQuizRequest solveQuizRequest,
                                                       @AuthenticationPrincipal AppUserAdapter appUserAdapter) {
        boolean solved = quizService.solveQuiz(id, solveQuizRequest.answer(), appUserAdapter.getUsername());
        String feedback = solved ? "Congratulations, you're right!" : "Wrong answer! Please, try again.";

        return ResponseEntity.ok(new SolveQuizResponse(solved, feedback));
    }
}
