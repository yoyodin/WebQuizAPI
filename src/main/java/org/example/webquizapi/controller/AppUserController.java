package org.example.webquizapi.controller;

import jakarta.validation.Valid;
import org.example.webquizapi.dto.AppUserDto;
import org.example.webquizapi.contracts.UserContracts.*;
import org.example.webquizapi.service.AppUserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class AppUserController {
    private final AppUserServiceImpl appUserService;

    public AppUserController(AppUserServiceImpl appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping
    public ResponseEntity<List<GetUserResponse>> getUsers() {
        List<GetUserResponse> users = appUserService.getAllUsers()
                .stream()
                .map(u -> new GetUserResponse(u.getId(), u.getEmail()))
                .toList();

        return ResponseEntity.ok(users);
    }

    @PostMapping("/register")
    public ResponseEntity<AppUserDto> createUser(@Valid @RequestBody CreateUserRequest userRequest) {
        AppUserDto dto = new AppUserDto(null, userRequest.email(), userRequest.password());
        dto = appUserService.save(dto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}
