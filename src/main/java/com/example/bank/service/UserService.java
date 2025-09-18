package com.example.bank.service;

import com.example.bank.dto.*;
import com.example.bank.entity.User;
import com.example.bank.exception.*;
import com.example.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public UserResponse create(UserCreateRequest req) {
        if (users.existsByUsername(req.username())) throw new ConflictException("Username already exists");
        if (users.existsByEmail(req.email())) throw new ConflictException("Email already exists");
        User u = User.builder()
                .username(req.username())
                .password(encoder.encode(req.password()))
                .email(req.email())
                .build();
        users.save(u);
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail());
    }

    public UserResponse get(Long id, Authentication auth) {
        User u = users.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        ensureSelf(u, auth);
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail());
    }

    public UserResponse update(Long id, UserUpdateRequest req, Authentication auth) {
        User u = users.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        ensureSelf(u, auth);
        if (req.email() != null) u.setEmail(req.email());
        if (req.password() != null && !req.password().isBlank()) u.setPassword(encoder.encode(req.password()));
        users.save(u);
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail());
    }

    public void delete(Long id, boolean hasAccounts, Authentication auth) {
        User u = users.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        ensureSelf(u, auth);
        if (hasAccounts) throw new ConflictException("Cannot delete user with existing bank account(s)");
        users.delete(u);
    }

    private void ensureSelf(User u, Authentication auth) {
        if (auth == null || !u.getUsername().equals(auth.getName()))
            throw new ForbiddenException("Cannot access another user's details");
    }
}
