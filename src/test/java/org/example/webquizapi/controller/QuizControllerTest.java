package org.example.webquizapi.controller;

import org.example.webquizapi.contracts.QuizContracts.*;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

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
        when(quizService.getByAuthor("test@test.com")).thenReturn(mockQuizzes);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/quiz").with(authentication(auth)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mockQuizzesListResponse)
                );
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
        when(quizService.getById(1L)).thenReturn(mockQuizzes.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/quiz/1")
                        .with(authentication(auth)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mockQuizResponse)
                );
    }

    @Test
    @DisplayName("GET /quiz/1 returns 404 NOT FOUND")
    void getQuizById_returnsQuizNotFound() throws Exception {
        when(quizService.getById(1L)).thenThrow(new QuizNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/quiz/1")
                .with(authentication(auth)))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("No Quiz found with the given id")
                );
    }

    @Test
    @DisplayName("GET /quiz/1 returns 404 NOT FOUND")
    void getQuizById_returnsUserNotFound() throws Exception {
        when(quizService.getById(1L)).thenThrow(new UserNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/quiz/1")
                        .with(authentication(auth)))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("User not found")
                );
    }

    @Test
    @DisplayName("POST /quiz returns 201 CREATED")
    void createQuiz_returnsCreated() throws Exception {
        var mockCreateQuizReq = new QuizDto(null, "Test quiz", "Quiz test", List.of("1", "2"), List.of(1,2), "test@test.com");

        when(quizService.save(mockCreateQuizReq)).thenReturn(mockQuizzes.getFirst());

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
    }

    @Test
    @DisplayName("POST /quiz/1/solve returns 200 OK and a valid JSON")
    void solveQuiz_returnsCorrectFeedback() throws Exception {
        when(quizService.solveQuiz(1, List.of(1,2), "test@test.com")).thenReturn(true);

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
    }

    @Test
    @DisplayName("POST /quiz/1/solve returns 200 OK and a valid JSON")
    void solveQuiz_returnsWrongFeedback() throws Exception {
        when(quizService.solveQuiz(1, List.of(1,2), "test@test.com")).thenReturn(false);

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
    }
}
