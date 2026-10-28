package org.example.webquizapi.service.impl;

import org.example.webquizapi.dto.CompletionDto;
import org.example.webquizapi.dto.QuizDto;
import org.example.webquizapi.entity.AppUser;
import org.example.webquizapi.entity.Completion;
import org.example.webquizapi.entity.Quiz;
import org.example.webquizapi.exception.QuizNotFoundException;
import org.example.webquizapi.exception.UserNotFoundException;
import org.example.webquizapi.mapper.QuizMapper;
import org.example.webquizapi.repository.AppUserRepository;
import org.example.webquizapi.repository.CompletionRepository;
import org.example.webquizapi.repository.QuizRepository;
import org.example.webquizapi.service.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final AppUserRepository appUserRepository;
    private final CompletionRepository completionRepository;
    private final QuizMapper quizMapper;

    public QuizServiceImpl(QuizRepository quizRepository,
                           AppUserRepository appUserRepository,
                           CompletionRepository completionRepository,
                           QuizMapper quizMapper) {
        this.quizRepository = quizRepository;
        this.appUserRepository = appUserRepository;
        this.completionRepository = completionRepository;
        this.quizMapper = quizMapper;
    }

    @Override
    public List<QuizDto> getAll() {
        return quizRepository.findAll()
                .stream()
                .map(quizMapper::entityToDto)
                .toList();
    }

    @Override
    public List<QuizDto> getByAuthor(String email) {
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        return quizRepository.findByCreator(user)
                .stream()
                .map(quizMapper::entityToDto)
                .toList();
    }

    @Override
    public QuizDto getById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(QuizNotFoundException::new);

        return quizMapper.entityToDto(quiz);
    }

    @Override
    public QuizDto save(QuizDto dto) {
        Quiz quiz = quizMapper.dtoToEntity(dto);
        AppUser user = appUserRepository.findByEmail(dto.getAuthor())
                .orElseThrow(UserNotFoundException::new);

        quiz.setCreator(user);
        quiz = quizRepository.save(quiz);

        return quizMapper.entityToDto(quiz);
    }

    @Override
    public boolean solveQuiz(int id, List<Integer> answer, String email) {
        Quiz quiz = quizRepository.findById((long) id)
                .orElseThrow(QuizNotFoundException::new);

        if (answer.containsAll(quiz.getAnswer())) {
            AppUser user = appUserRepository.findByEmail(email)
                    .orElseThrow(UserNotFoundException::new);

            Completion completed = new Completion(null, user, quiz, OffsetDateTime.now());
            completionRepository.save(completed);

            quiz.addCompletion(completed);
            quizRepository.save(quiz);

            return true;
        }

        return false;
    }

    @Override
    public Page<CompletionDto> getSolvedQuizzesByUser(String email, int pageNumber) {
        return completionRepository.findByEmail(email, PageRequest.of(pageNumber, 10, Sort.by("completedAt").descending()));
    }
}
