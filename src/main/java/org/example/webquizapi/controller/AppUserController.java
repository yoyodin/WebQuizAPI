package org.example.webquizapi.controller;

import jakarta.validation.Valid;
import org.example.webquizapi.dto.AppUserDto;
import org.example.webquizapi.contracts.UserContracts.*;
import org.example.webquizapi.service.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class AppUserController {
    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping
    public List<GetUserResponse> getUsers() {
        return appUserService.getAllUsers()
                .stream()
                .map(u -> new GetUserResponse(u.getId(), u.getEmail()))
                .toList();
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest userRequest) {
        AppUserDto dto = new AppUserDto(null, userRequest.email(), userRequest.password());
        dto = appUserService.save(dto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
