package org.example.webquizapi.service;

import org.example.webquizapi.dto.QuizDto;
import org.example.webquizapi.entity.AppUser;
import org.example.webquizapi.entity.Quiz;
import org.example.webquizapi.exception.UserNotFoundException;
import org.example.webquizapi.mapper.QuizMapper;
import org.example.webquizapi.repository.AppUserRepository;
import org.example.webquizapi.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final AppUserRepository appUserRepository;
    private final QuizMapper quizMapper;

    public QuizService(QuizRepository quizRepository,
                       AppUserRepository appUserRepository,
                       QuizMapper quizMapper) {
        this.quizRepository = quizRepository;
        this.appUserRepository = appUserRepository;
        this.quizMapper = quizMapper;
    }

    public List<QuizDto> getAll() {
        return quizRepository.findAll()
                .stream()
                .map(quizMapper::entityToDto)
                .toList();
    }

    public QuizDto save(QuizDto dto) {
        Quiz quiz = quizMapper.dtoToEntity(dto);
        AppUser user = appUserRepository.findByEmail(dto.getAuthor())
                .orElseThrow(UserNotFoundException::new);

        quiz.setCreator(user);
        quiz = quizRepository.save(quiz);

        return quizMapper.entityToDto(quiz);
    }
}
