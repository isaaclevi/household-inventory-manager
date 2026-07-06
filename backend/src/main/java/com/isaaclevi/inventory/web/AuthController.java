package com.isaaclevi.inventory.web;

import com.isaaclevi.inventory.model.AppUser;
import com.isaaclevi.inventory.repository.AppUserRepository;
import com.isaaclevi.inventory.security.JwtService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public record RegisterRequest(@NotBlank String username,
                                  @NotBlank @Size(min = 8) String password,
                                  String displayName) {}

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    public record AuthResponse(String token, String username, String displayName) {}

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AppUserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        String username = request.username().trim().toLowerCase();
        if (users.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That username is already taken");
        }
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName() != null ? request.displayName() : request.username());
        users.save(user);
        return new AuthResponse(jwtService.issueToken(username), username, user.getDisplayName());
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        String username = request.username().trim().toLowerCase();
        AppUser user = users.findByUsername(username)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong username or password"));
        return new AuthResponse(jwtService.issueToken(username), username, user.getDisplayName());
    }
}
