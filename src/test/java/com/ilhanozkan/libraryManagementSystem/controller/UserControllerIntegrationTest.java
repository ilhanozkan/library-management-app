package com.ilhanozkan.libraryManagementSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilhanozkan.libraryManagementSystem.model.dto.request.UserRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserRole;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserStatus;
import com.ilhanozkan.libraryManagementSystem.repository.UserRepository;
import com.ilhanozkan.libraryManagementSystem.repository.BorrowingRepository;
import com.ilhanozkan.libraryManagementSystem.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private User testLibrarian;
    private User testPatron;
    private String librarianToken;
    private String patronToken;

    @BeforeEach
    void setUp() {
        // Clean up repositories
        borrowingRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        testLibrarian = User.builder()
                .username("librarian")
                .email("librarian@test.com")
                .password("password")
                .name("Test")
                .surname("Librarian")
                .role(UserRole.LIBRARIAN)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(testLibrarian);

        testPatron = User.builder()
                .username("patron")
                .email("patron@test.com")
                .password("password")
                .name("Test")
                .surname("Patron")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(testPatron);

        // Generate JWT tokens
        librarianToken = jwtService.generateToken(testLibrarian.getUsername());
        patronToken = jwtService.generateToken(testPatron.getUsername());
    }

    @Test
    public void shouldGetAllUsers() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andDo(print());
    }

    @Test
    public void shouldGetUserById() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get("/users/{id}", testPatron.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("patron")))
                .andExpect(jsonPath("$.email", is("patron@test.com")))
                .andDo(print());
    }

    @Test
    public void shouldGetUserByEmail() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get("/users/email/{email}", "patron@test.com")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("patron")))
                .andExpect(jsonPath("$.name", is("Test")))
                .andDo(print());
    }

    @Test
    public void shouldCreateUser() throws Exception {
        // Given
        UserRequestDTO userRequestDTO = new UserRequestDTO(
            "newuser",
            "newuser@example.com",
            "password",
            "New",
            "User",
            UserRole.PATRON,
            UserStatus.ACTIVE
        );

        // When
        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)));

        // Then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.email", is("newuser@example.com")))
                .andExpect(jsonPath("$.name", is("New")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andDo(print());
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        // Given
        UserRequestDTO userRequestDTO = new UserRequestDTO(
            "updateduser",
            "updated@example.com",
            "newpassword",
            "Updated",
            "User",
            UserRole.PATRON,
            UserStatus.ACTIVE
        );

        // When
        ResultActions response = mockMvc.perform(put("/users/{id}", testPatron.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("updateduser")))
                .andExpect(jsonPath("$.email", is("updated@example.com")))
                .andExpect(jsonPath("$.name", is("Updated")))
                .andDo(print());
    }

    @Test
    public void shouldDeactivateUser() throws Exception {
        // When
        ResultActions response = mockMvc.perform(put("/users/{id}/deactivate", testPatron.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("INACTIVE")))
                .andDo(print());
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        // When
        ResultActions response = mockMvc.perform(delete("/users/{id}", testPatron.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andDo(print());

        // Verify user was deleted
        mockMvc.perform(get("/users/{id}", testPatron.getId()))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void shouldNotCreateUserWithExistingUsername() throws Exception {
        // Given
        UserRequestDTO userRequestDTO = new UserRequestDTO(
            "patron",
            "another@example.com",
            "password",
            "Another",
            "User",
            UserRole.PATRON,
            UserStatus.ACTIVE
        );

        // When
        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)));

        // Then
        response.andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void shouldNotCreateUserWithExistingEmail() throws Exception {
        // Given
        UserRequestDTO userRequestDTO = new UserRequestDTO(
            "anotheruser",
            "patron@test.com",
            "password",
            "Another",
            "User",
            UserRole.PATRON,
            UserStatus.ACTIVE
        );

        // When
        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDTO)));

        // Then
        response.andExpect(status().isBadRequest())
                .andDo(print());
    }
} 