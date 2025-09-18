package com.example.bank.service;

import com.example.bank.dto.*;
import com.example.bank.entity.User;
import com.example.bank.exception.*;
import com.example.bank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void create_shouldCreateUser_whenUsernameAndEmailAreUnique() {
        UserCreateRequest req = new UserCreateRequest("user1", "pass", "user1@email.com");
        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@email.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        User savedUser = User.builder().id(1L).username("user1").password("encodedPass").email("user1@email.com").build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse resp = userService.create(req);

        assertEquals("user1", resp.username());
        assertEquals("user1@email.com", resp.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_shouldThrowConflictException_whenUsernameExists() {
        UserCreateRequest req = new UserCreateRequest("user1", "pass", "user1@email.com");
        when(userRepository.existsByUsername("user1")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(req));
    }

    @Test
    void create_shouldThrowConflictException_whenEmailExists() {
        UserCreateRequest req = new UserCreateRequest("user1", "pass", "user1@email.com");
        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@email.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(req));
    }

    @Test
    void get_shouldReturnUserResponse_whenUserExistsAndIsSelf() {
        User user = User.builder().id(2L).username("me").email("me@email.com").build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn("me");

        UserResponse resp = userService.get(2L, authentication);

        assertEquals(2L, resp.id());
        assertEquals("me", resp.username());
        assertEquals("me@email.com", resp.email());
    }

    @Test
    void get_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.get(3L, authentication));
    }

    @Test
    void get_shouldThrowForbiddenException_whenNotSelf() {
        User user = User.builder().id(4L).username("other").email("other@email.com").build();
        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn("notother");

        assertThrows(ForbiddenException.class, () -> userService.get(4L, authentication));
    }

    @Test
    void update_shouldUpdateEmailAndPassword_whenFieldsProvided() {
        User user = User.builder().id(5L).username("me").email("old@email.com").password("oldpass").build();
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn("me");
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNewPass");
        UserUpdateRequest req = new UserUpdateRequest("new@email.com", "newpass");

        UserResponse resp = userService.update(5L, req, authentication);

        assertEquals("new@email.com", resp.email());
        assertEquals("me", resp.username());
        verify(userRepository).save(user);
        assertEquals("encodedNewPass", user.getPassword());
    }

    @Test
    void update_shouldUpdateOnlyEmail_whenPasswordIsNullOrBlank() {
        User user = User.builder().id(6L).username("me").email("old@email.com").password("oldpass").build();
        when(userRepository.findById(6L)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn("me");
        UserUpdateRequest req = new UserUpdateRequest("new@email.com", null);

        UserResponse resp = userService.update(6L, req, authentication);

        assertEquals("new@email.com", resp.email());
        assertEquals("oldpass", user.getPassword());
        verify(userRepository).save(user);

        // Blank password
        req = new UserUpdateRequest("new@email.com", "   ");
        resp = userService.update(6L, req, authentication);
        assertEquals("oldpass", user.getPassword());
    }

    @Test
    void update_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(7L)).thenReturn(Optional.empty());
        UserUpdateRequest req = new UserUpdateRequest("x@email.com", "pass");

        assertThrows(NotFoundException.class, () -> userService.update(7L, req, authentication));
    }

    @Test
    void update_shouldThrowForbiddenException_whenNotSelf() {
        User user = User.builder().id(8L).username("other").email("other@email.com").build();
        when(userRepository.findById(8L)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn("notother");
        UserUpdateRequest req = new UserUpdateRequest("x@email.com", "pass");

        assertThrows(ForbiddenException.class, () -> userService.update(8L, req, authentication));
    }

    @Test
    void delete_shouldDeleteUser_whenNoAccountsAndIsSelf() {
        User user = User.builder().id(9L).username("me").email("me@email.com").build();
        when(userRepository.findById(9L)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn("me");

        userService.delete(9L, false, authentication);

        verify(userRepository).delete(user);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(10L, false, authentication));
    }

    @Test
    void delete_shouldThrowForbiddenException_whenNotSelf() {
        User user = User.builder().id(11L).username("other").email("other@email.com").build();
        when(userRepository.findById(11L)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn("notother");

        assertThrows(ForbiddenException.class, () -> userService.delete(11L, false, authentication));
    }

    @Test
    void delete_shouldThrowConflictException_whenHasAccounts() {
        User user = User.builder().id(12L).username("me").email("me@email.com").build();
        when(userRepository.findById(12L)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn("me");

        assertThrows(ConflictException.class, () -> userService.delete(12L, true, authentication));
    }
}