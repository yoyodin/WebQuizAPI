package org.example.webquizapi.controller;

import jakarta.validation.Valid;
import org.example.webquizapi.config.RoutesConfig;
import org.example.webquizapi.contracts.UserContracts.CreateUserRequest;
import org.example.webquizapi.contracts.UserContracts.GetUserResponse;
import org.example.webquizapi.dto.AppUserDto;
import org.example.webquizapi.service.AppUserService;
import org.example.webquizapi.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RoutesConfig.APP_USER_ROOT)
public class AppUserController {
    private final AppUserService appUserService;
    private final TokenService tokenService;

    public AppUserController(AppUserService appUserService, TokenService tokenService) {
        this.appUserService = appUserService;
        this.tokenService = tokenService;
    }

    @GetMapping
    public ResponseEntity<List<GetUserResponse>> getUsers() {
        List<GetUserResponse> users = appUserService.getAllUsers()
                .stream()
                .map(u -> new GetUserResponse(u.getId(), u.getEmail()))
                .toList();

        return ResponseEntity.ok(users);
    }

    @PostMapping(RoutesConfig.REGISTER)
    public ResponseEntity<AppUserDto> createUser(@Valid @RequestBody CreateUserRequest userRequest) {
        AppUserDto dto = new AppUserDto(null, userRequest.email(), userRequest.password());
        dto = appUserService.save(dto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(RoutesConfig.TOKEN)
    public String token(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }
}
