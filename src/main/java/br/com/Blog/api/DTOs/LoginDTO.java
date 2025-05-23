package br.com.Blog.api.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginDTO(
        @Email
        @Pattern(regexp = "^[^<>]*$", message = "invalid character")
        @Size(max = 150, message = "Size max of 150")
        @NotBlank(message = "Field email is required")
        String email,

        @Pattern(regexp = "^[^<>]*$", message = "invalid character")
        @Size(max = 100, message = "Size max of 100")
        @NotBlank(message = "Field password is required")
        String password
) {
}
