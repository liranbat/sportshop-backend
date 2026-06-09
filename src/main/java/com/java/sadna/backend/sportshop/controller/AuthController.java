package com.java.sadna.backend.sportshop.controller;

import com.java.sadna.backend.sportshop.api.generated.authusers.api.AuthApi;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.LoginRequest;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.RegisterRequest;
import com.java.sadna.backend.sportshop.api.generated.authusers.model.UserResponse;
import com.java.sadna.backend.sportshop.mapper.UserDtoToUserResponseMapper;
import com.java.sadna.backend.sportshop.security.SecurityContextUtils;
import com.java.sadna.backend.sportshop.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final UserDtoToUserResponseMapper userDtoToUserResponseMapper;
    
    // Spring injects request-scoped proxies, so even though this controller is a singleton,
    // each call reads/writes the cookies of its own HTTP request.
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;

    public AuthController(AuthService authService,
                          UserDtoToUserResponseMapper userDtoToUserResponseMapper,
                          HttpServletRequest httpServletRequest,
                          HttpServletResponse httpServletResponse) {
        this.authService = authService;
        this.userDtoToUserResponseMapper = userDtoToUserResponseMapper;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public ResponseEntity<UserResponse> register(RegisterRequest registerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userDtoToUserResponseMapper.map(authService.register(registerRequest)));
    }

    @Override
    public ResponseEntity<UserResponse> login(LoginRequest loginRequest) {
        return ResponseEntity.ok(
                userDtoToUserResponseMapper.map(authService.login(loginRequest, httpServletResponse))
        );
    }

    // Intentionally open even though it changes state: an unauthenticated caller still
    // gets 204 + cleared cookies so the frontend can treat logout as always-succeeds.
    @Override
    public ResponseEntity<Void> logout() {
        authService.logout(SecurityContextUtils.currentUserId().orElse(null), httpServletResponse);
        return ResponseEntity.noContent().build();
    }

    // Open by design: refresh runs without a valid access token; the refresh_token
    // cookie itself is verified inside AuthService.
    @Override
    public ResponseEntity<UserResponse> refresh() {
        return ResponseEntity.ok(
                userDtoToUserResponseMapper.map(authService.refresh(httpServletRequest, httpServletResponse))
        );
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getMe() {
        // @PreAuthorize already guaranteed an authenticated principal; the throw inside
        // currentUserIdOrThrow only fires if something other than JwtCookieAuthenticationFilter
        // populated the SecurityContext with a non-Long principal.
        Long userId = SecurityContextUtils.currentUserIdOrThrow();
        return ResponseEntity.ok(userDtoToUserResponseMapper.map(authService.getMe(userId)));
    }
}
