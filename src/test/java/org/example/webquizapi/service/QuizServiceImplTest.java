package org.example.webquizapi.service;

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
import org.example.webquizapi.service.impl.QuizServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceImplTest {
    private final Quiz mockQuiz = new Quiz(1L, "Test title", "test text", List.of("1", "2"), List.of(2));
    private final QuizDto mockQuizDto = new QuizDto(mockQuiz.getId(), mockQuiz.getTitle(), mockQuiz.getText(), mockQuiz.getOptions(), mockQuiz.getAnswer(), "test@test.com");
    private final AppUser mockAppUser = new AppUser(1L, "test@test.com", "password", List.of(mockQuiz), null);

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private CompletionRepository completionRepository;

    @Mock
    private QuizMapper quizMapper;

    @InjectMocks
    private QuizServiceImpl quizService;

    @Test
    void getAll_shouldReturnQuizDtoList() {
        when(quizRepository.findAll()).thenReturn(List.of(mockQuiz));
        when(quizMapper.entityToDto(any(Quiz.class))).thenReturn(mockQuizDto);

        var quizzes = quizService.getAll();

        assertThat(quizzes.getFirst().getId()).isEqualTo(mockQuizDto.getId());

        verify(quizRepository).findAll();
        verify(quizMapper).entityToDto(any());
    }

    @Test
    void getByAuthor_shouldReturnQuizDtoList() {
        when(appUserRepository.findByEmail(anyString())).thenReturn(Optional.of(mockAppUser));
        when(quizRepository.findByCreator(any(AppUser.class))).thenReturn(List.of(mockQuiz));
        when(quizMapper.entityToDto(any(Quiz.class))).thenReturn(mockQuizDto);

        var quizzes = quizService.getByAuthor("test@test.com");

        assertThat(quizzes.getFirst().getId()).isEqualTo(mockQuizDto.getId());

        verify(appUserRepository).findByEmail(anyString());
        verify(quizRepository).findByCreator(any());
        verify(quizMapper).entityToDto(any());
    }

    @Test
    void getByAuthor_shouldThrowException() {
        when(appUserRepository.findByEmail(anyString()))
                .thenThrow(UserNotFoundException.class);

        assertThatThrownBy(() -> quizService.getByAuthor(anyString()))
                .isInstanceOf(UserNotFoundException.class);

        verify(quizRepository, never()).findByCreator(any());
        verify(quizMapper, never()).entityToDto(any());
    }

    @Test
    void getById_shouldReturnQuizDto() {
        when(quizRepository.findById(anyLong())).thenReturn(Optional.of(mockQuiz));
        when(quizMapper.entityToDto(any(Quiz.class))).thenReturn(mockQuizDto);

        var quizDto = quizService.getById(1L);

        assertThat(quizDto.getId()).isEqualTo(mockQuizDto.getId());

        verify(quizRepository).findById(any());
        verify(quizMapper).entityToDto(any());
    }

    @Test
    void getById_shouldThrowException() {
        when(quizRepository.findById(anyLong()))
                .thenThrow(QuizNotFoundException.class);

        assertThatThrownBy(() -> quizService.getById(anyLong()))
                .isInstanceOf(QuizNotFoundException.class);

        verify(quizMapper, never()).entityToDto(any());
    }

    @Test
    void save_shouldReturnQuizDto() {
        when(quizMapper.dtoToEntity(any(QuizDto.class))).thenReturn(mockQuiz);
        when(appUserRepository.findByEmail(anyString())).thenReturn(Optional.of(mockAppUser));
        when(quizRepository.save(any(Quiz.class))).thenReturn(mockQuiz);
        when(quizMapper.entityToDto(any(Quiz.class))).thenReturn(mockQuizDto);

        var save = quizService.save(mockQuizDto);

        assertThat(save.getId()).isEqualTo(mockQuizDto.getId());

        verify(quizMapper).dtoToEntity(any());
        verify(quizMapper).entityToDto(any());
        verify(appUserRepository).findByEmail(any());
        verify(quizRepository).save(any());
    }

    @Test
    void solveQuiz_shouldReturnFalse() {
        when(quizRepository.findById(anyLong())).thenReturn(Optional.of(mockQuiz));

        boolean solve = quizService.solveQuiz(1, List.of(3), "test@test.com");

        assertThat(solve).isFalse();

        verify(appUserRepository, never()).findByEmail(anyString());
        verify(completionRepository, never()).save(any());
        verify(quizRepository, never()).save(any());
    }

    @Test
    void solveQuiz_shouldReturnTrue() {
        when(quizRepository.findById(anyLong())).thenReturn(Optional.of(mockQuiz));
        when(appUserRepository.findByEmail(anyString())).thenReturn(Optional.of(mockAppUser));
        when(completionRepository.save(any(Completion.class))).thenReturn(new Completion(1L, mockAppUser, mockQuiz, OffsetDateTime.now()));
        when(quizRepository.save(any(Quiz.class))).thenReturn(mockQuiz);

        boolean solve = quizService.solveQuiz(1, List.of(2), "test@test.com");

        assertThat(solve).isTrue();

        verify(quizRepository).findById(anyLong());
        verify(appUserRepository).findByEmail(anyString());
        verify(completionRepository).save(any());
        verify(quizRepository).save(any());
    }

    @Test
    void getSolvedQuizzes_shouldReturnPage() {
        OffsetDateTime completedAt = OffsetDateTime.parse("2026-10-25T14:30:15.123+01:00");
        Page<CompletionDto> completionPage = new PageImpl<>(List.of(new CompletionDto(1L, completedAt)));

        when(completionRepository.findByEmail(anyString(), any(Pageable.class))).thenReturn(completionPage);

        var page = quizService.getSolvedQuizzesByUser("test@test.com", 0);

        assertThat(page.hasContent()).isTrue();
        assertThat(page.getContent().getFirst().getCompletedAt()).isEqualTo(completedAt);

        verify(completionRepository).findByEmail(anyString(), any());
    }
}
