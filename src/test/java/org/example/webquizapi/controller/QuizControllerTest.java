package org.example.webquizapi.controller;

import org.example.webquizapi.dto.CompletionDto;
import org.example.webquizapi.dto.QuizDto;
import org.example.webquizapi.entity.AppUser;
import org.example.webquizapi.exception.QuizNotFoundException;
import org.example.webquizapi.exception.UserNotFoundException;
import org.example.webquizapi.security.AppUserAdapter;
import org.example.webquizapi.security.TestSecurityConfig;
import org.example.webquizapi.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizController.class)
@Import(TestSecurityConfig.class)
public class QuizControllerTest {
    private final List<QuizDto> mockQuizzes = List.of(new QuizDto(1L, "Test quiz", "Quiz test", List.of("1", "2"), List.of(1,2), "test@test.com"));
    private final String mockQuizResponse = """
                                  {
                                    "id": 1,
                                    "title": "Test quiz",
                                    "text": "Quiz test",
                                    "author": "test@test.com",
                                    "options": [
                                      "1",
                                      "2"
                                    ],
                                    "answer": [
                                      1,
                                      2
                                    ]
                                  }
                                """;
    private final String mockQuizzesListResponse = "[" + mockQuizResponse + "]";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuizService quizService;

    private UsernamePasswordAuthenticationToken auth;

    @BeforeEach
    void setUp() {
        AppUser user = new AppUser();
        user.setId(42L);
        user.setEmail("test@test.com");
        user.setPassword("password12");

        AppUserAdapter principal = new AppUserAdapter(user);
        auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    @Test
    @DisplayName("GET /quiz returns 401 UNAUTHORIZED")
    void getQuizzesByAuthor_returnsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/quiz"))
                .andExpectAll(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /quiz returns 200 OK and a valid JSON")
    void getQuizzesByAuthor_returnsValidResponseEntity() throws Exception {
        when(quizService.getByAuthor(anyString())).thenReturn(mockQuizzes);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/quiz").with(authentication(auth)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mockQuizzesListResponse)
                );

        verify(quizService).getByAuthor(anyString());
    }

    @Test
    @DisplayName("GET /all returns 401 UNAUTHORIZED")
    void getAllQuizzes_returnsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/quiz/all"))
                .andExpectAll(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /all returns 200 OK and a valid JSON")
    void getAllQuizzes_returnsValidResponseEntity() throws Exception {
        when(quizService.getAll()).thenReturn(mockQuizzes);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/quiz/all").with(authentication(auth)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mockQuizzesListResponse)
                );

        verify(quizService).getAll();
    }

    @Test
    @DisplayName("GET /quiz/1 returns 401 UNAUTHORIZED")
    void getQuizById_returnsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/quiz/1"))
                .andExpectAll(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /quiz/1 returns 200 OK and a valid JSON")
    void getQuizById_returnsValidResponseEntity() throws Exception {
        when(quizService.getById(anyLong())).thenReturn(mockQuizzes.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/quiz/1")
                        .with(authentication(auth)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mockQuizResponse)
                );

        verify(quizService).getById(anyLong());
    }

    @Test
    @DisplayName("GET /quiz/1 returns 404 NOT FOUND")
    void getQuizById_returnsQuizNotFound() throws Exception {
        when(quizService.getById(anyLong())).thenThrow(new QuizNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/quiz/1")
                .with(authentication(auth)))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("No Quiz found with the given id")
                );

        verify(quizService).getById(anyLong());
    }

    @Test
    @DisplayName("GET /quiz/1 returns 404 NOT FOUND")
    void getQuizById_returnsUserNotFound() throws Exception {
        when(quizService.getById(anyLong())).thenThrow(new UserNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/quiz/1")
                        .with(authentication(auth)))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("User not found")
                );

        verify(quizService).getById(anyLong());
    }

    @Test
    @DisplayName("POST /quiz returns 201 CREATED")
    void createQuiz_returnsCreated() throws Exception {
        when(quizService.save(any(QuizDto.class))).thenReturn(mockQuizzes.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/quiz")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Test quiz",
                          "text": "Quiz test",
                          "options": ["1","2"],
                          "answer": [1,2]
                        }
                        """))
                .andExpect(status().isCreated());

        verify(quizService).save(any());
    }

    @Test
    @DisplayName("POST /quiz/1/solve returns 200 OK and a valid JSON")
    void solveQuiz_returnsCorrectFeedback() throws Exception {
        when(quizService.solveQuiz(anyInt(), anyList(), anyString())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/quiz/1/solve")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "answer": [1,2]
                        }
                        """))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "solved": true,
                                    "feedback": "Congratulations, you're right!"
                                }
                                """)
                );

        verify(quizService).solveQuiz(anyInt(), anyList(), anyString());
    }

    @Test
    @DisplayName("POST /quiz/1/solve returns 200 OK and a valid JSON")
    void solveQuiz_returnsWrongFeedback() throws Exception {
        when(quizService.solveQuiz(anyInt(), anyList(), anyString())).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/quiz/1/solve")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "answer": [1,2]
                        }
                        """))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "solved": false,
                                    "feedback": "Wrong answer! Please, try again."
                                }
                                """)
                );

        verify(quizService).solveQuiz(anyInt(), anyList(), anyString());
    }

    @Test
    @DisplayName("GET /quiz/solved returns 200 OK and a valid JSON")
    void getSolvedByAuthor_returnsSolvedQuizzes() throws Exception {
        OffsetDateTime completedAt = OffsetDateTime.parse("2026-10-25T14:30:15.123+01:00");
        Page<CompletionDto> p = new PageImpl<>(List.of(new CompletionDto(1L, completedAt)));

        when(quizService.getSolvedQuizzesByUser(anyString(), anyInt())).thenReturn(p);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/quiz/solved")
                .with(authentication(auth))
                .param("page", "0"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content[*].testId").isNotEmpty(),
                        jsonPath("$.content[*].completedAt").value(completedAt.toString())
                );

        verify(quizService).getSolvedQuizzesByUser(anyString(), anyInt());
    }
}
