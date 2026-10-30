package org.example.webquizapi.controller;

import org.example.webquizapi.dto.AppUserDto;
import org.example.webquizapi.service.AppUserService;
import org.example.webquizapi.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AppUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppUserService appUserService;

    @MockitoBean
    private TokenService tokenService;

    @Test
    @DisplayName("GET /user returns 200 OK and a valid JSON")
    void getUsers_returnsValidResponseEntity() throws Exception {
        var mockUserResponse = List.of(new AppUserDto(1L, "test@test.com", null));
        when(appUserService.getAllUsers()).thenReturn(mockUserResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user")
                        .with(user("test@test.com")))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                  {
                                    "id": 1,
                                    "email": "test@test.com"
                                  }
                                ]
                                """)
                );
    }

    @Test
    @DisplayName("GET /user returns 401 UNAUTHORIZED")
    void getUsers_returnsUnauthorizedStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /register returns 201 CREATED")
    void register_returnsValidResponseEntity() throws Exception {
        when(appUserService.save(null)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@gmail.com",
                                  "password": "PasswordForTest"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /register returns 500 INTERNAL SERVER ERROR")
    void register_returnsInternalServerErrorEmptyPassword() throws Exception {
        when(appUserService.save(null)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@gmail.com",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /token returns 200 OK")
    void token_returnsValidResponseEntity() throws Exception {
        when(tokenService.generateToken(null)).thenReturn("token");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/token")
                .with(user("test@test.com")))
                .andExpectAll(status().isOk());
    }
}
