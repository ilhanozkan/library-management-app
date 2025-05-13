package com.ilhanozkan.libraryManagementSystem.repository;

import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserRole;
import com.ilhanozkan.libraryManagementSystem.model.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveUser() {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .name("Test")
                .surname("User")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void shouldFindUserById() {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .name("Test")
                .surname("User")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();
        User savedUser = userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    public void shouldFindUserByUsername() {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .name("Test")
                .surname("User")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        // Act
        User foundUser = userRepository.findByUsername("testuser");

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void shouldFindUserByEmail() {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .name("Test")
                .surname("User")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    public void shouldCheckIfUserExistsByUsername() {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .name("Test")
                .surname("User")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        // Act
        boolean exists = userRepository.existsByUsername("testuser");
        boolean notExists = userRepository.existsByUsername("nonexistentuser");

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    public void shouldCheckIfUserExistsByEmail() {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .name("Test")
                .surname("User")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        // Act
        boolean exists = userRepository.existsByEmail("test@example.com");
        boolean notExists = userRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    public void shouldDeleteUser() {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .name("Test")
                .surname("User")
                .role(UserRole.PATRON)
                .status(UserStatus.ACTIVE)
                .build();
        User savedUser = userRepository.save(user);

        // Act
        userRepository.deleteById(savedUser.getId());
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());

        // Assert
        assertThat(deletedUser).isEmpty();
    }
} 