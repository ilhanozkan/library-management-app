package com.ilhanozkan.libraryManagementSystem.service.impl;

import com.ilhanozkan.libraryManagementSystem.common.exception.ResourceNotFoundException;
import com.ilhanozkan.libraryManagementSystem.common.exception.user.UserNotFoundException;
import com.ilhanozkan.libraryManagementSystem.model.dto.request.UserRequestDTO;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.PagedResponse;
import com.ilhanozkan.libraryManagementSystem.model.dto.response.UserResponseDTO;
import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserRole;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserStatus;
import com.ilhanozkan.libraryManagementSystem.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private UUID userId;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user1 = User.builder()
                .id(userId)
                .name("John")
                .surname("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .password("hashedPassword1")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();

        user2 = User.builder()
                .id(UUID.randomUUID())
                .name("Jane")
                .surname("Smith")
                .username("janesmith")
                .email("jane.smith@example.com")
                .password("hashedPassword2")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();

        userRequestDTO = new UserRequestDTO(
            "newuser",
            "new.user@example.com",
            "Password123",
            "New",
            "User",
            UserRole.PATRON,
            UserStatus.ACTIVE
        );
    }

    @Test
    void shouldGetUsers() {
        // Arrange
        List<User> users = Arrays.asList(user1, user2);
        Page<User> userPage = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);

        given(userRepository.findAll(pageable)).willReturn(userPage);

        // Act
        PagedResponse<UserResponseDTO> result = userService.getUsers(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).username()).isEqualTo("johndoe");
        assertThat(result.getContent().get(1).username()).isEqualTo("janesmith");
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldGetUserById() {
        // Arrange
        given(userRepository.findById(userId)).willReturn(Optional.of(user1));

        // Act
        UserResponseDTO result = userService.getUserById(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("johndoe");
        assertThat(result.email()).isEqualTo("john.doe@example.com");

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundById() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        given(userRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(nonExistentId));

        verify(userRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void shouldGetUserByEmail() {
        // Arrange
        String email = "john.doe@example.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user1));

        // Act
        UserResponseDTO result = userService.getUserByEmail(email);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("johndoe");
        assertThat(result.email()).isEqualTo(email);

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundByEmail() {
        // Arrange
        String email = "nonexistent@example.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail(email));

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Arrange
        String invalidEmail = "invalid-email";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.getUserByEmail(invalidEmail));
    }

    @Test
    void shouldCreateUser() {
        // Arrange
        given(userRepository.existsByUsername(userRequestDTO.username())).willReturn(false);
        given(userRepository.existsByEmail(userRequestDTO.email())).willReturn(false);
        given(passwordEncoder.encode(userRequestDTO.password())).willReturn("encodedPassword");

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .name(userRequestDTO.name())
                .surname(userRequestDTO.surname())
                .username(userRequestDTO.username())
                .email(userRequestDTO.email())
                .password("encodedPassword")
                .role(userRequestDTO.role())
                .status(UserStatus.ACTIVE)
                .build();

        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // Act
        UserResponseDTO result = userService.createUser(userRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("newuser");
        assertThat(result.email()).isEqualTo("new.user@example.com");

        verify(userRepository, times(1)).existsByUsername(userRequestDTO.username());
        verify(userRepository, times(1)).existsByEmail(userRequestDTO.email());
        verify(passwordEncoder, times(1)).encode(userRequestDTO.password());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithExistingUsername() {
        // Arrange
        given(userRepository.existsByUsername(userRequestDTO.username())).willReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.createUser(userRequestDTO));

        verify(userRepository, times(1)).existsByUsername(userRequestDTO.username());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldUpdateUser() throws BadRequestException {
        // Arrange
        given(userRepository.findById(userId)).willReturn(Optional.of(user1));

        UserRequestDTO updateRequest = new UserRequestDTO(
            "johndoe",
            "john.doe@example.com",
            "newPassword",
            "Updated",
            "User",
            UserRole.PATRON,
            UserStatus.ACTIVE
        );

        User updatedUser = User.builder()
                .id(userId)
                .name("Updated")
                .surname("User")
                .username("johndoe")
                .email("john.doe@example.com")
                .password("hashedPassword1")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();

        given(userRepository.save(any(User.class))).willReturn(updatedUser);

        // Act
        UserResponseDTO result = userService.updateUser(userId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Updated");
        assertThat(result.surname()).isEqualTo("User");

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldUpdateUserWithNewUsername() throws BadRequestException {
        // Arrange
        given(userRepository.findById(userId)).willReturn(Optional.of(user1));
        given(userRepository.existsByUsername("newusername")).willReturn(false);

        UserRequestDTO updateRequest = new UserRequestDTO(
                "newusername",
                "john.doe@example.com",
                "newPassword",
                "John",
                "Doe",
                UserRole.PATRON,
                UserStatus.ACTIVE
        );

        User updatedUser = User.builder()
                .id(userId)
                .name("John")
                .surname("Doe")
                .username("newusername")
                .email("john.doe@example.com")
                .password("hashedPassword1")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();

        given(userRepository.save(any(User.class))).willReturn(updatedUser);

        // Act
        UserResponseDTO result = userService.updateUser(userId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("newusername");

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).existsByUsername("newusername");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldDeleteUser() {
        // Arrange
        given(userRepository.findById(userId)).willReturn(Optional.of(user1));
        doNothing().when(userRepository).delete(user1);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user1);
    }

    @Test
    void shouldThrowExceptionWhenDeleteNonExistentUser() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        given(userRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(nonExistentId));

        verify(userRepository, times(1)).findById(nonExistentId);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void shouldDeactivateUser() {
        // Arrange
        given(userRepository.findById(userId)).willReturn(Optional.of(user1));

        User deactivatedUser = User.builder()
                .id(userId)
                .name("John")
                .surname("Doe")
                .username("johndoe")
                .email("john.doe@example.com")
                .password("hashedPassword1")
                .role(UserRole.PATRON)
                .status(UserStatus.INACTIVE)
                .build();

        given(userRepository.save(any(User.class))).willReturn(deactivatedUser);

        // Act
        UserResponseDTO result = userService.deactivateUser(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(UserStatus.INACTIVE);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }
}