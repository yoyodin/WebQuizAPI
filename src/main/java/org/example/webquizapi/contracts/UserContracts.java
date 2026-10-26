package org.example.webquizapi.contracts;

import jakarta.validation.constraints.*;

public class UserContracts {
    public record CreateUserRequest(@NotNull @NotEmpty @NotBlank @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Invalid email format") String email,
                                    @NotNull @NotEmpty @NotBlank @Size(min = 12, message = "Password must be at least 5 characters long!") String password) {}

    public record GetUserResponse(Long id, String email) {}
}
