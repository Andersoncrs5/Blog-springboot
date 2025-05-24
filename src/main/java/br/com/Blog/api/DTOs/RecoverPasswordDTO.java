package br.com.Blog.api.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RecoverPasswordDTO {

    @Pattern(regexp = "^[^<>]*$", message = "invalid character")
    @Size(max = 100, message = "Size max of 100")
    @NotBlank(message = "Field password is required")
    private String password;

    @Pattern(regexp = "^[^<>]*$", message = "invalid character")
    @Size(max = 100, message = "Size max of 100")
    @NotBlank(message = "Field password is required")
    private String confirmPassword;

    @Pattern(regexp = "^[^<>]*$", message = "invalid character")
    @NotBlank(message = "Field token is required")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password.toLowerCase().trim();
    }

    public void setPassword(String password) {
        this.password = password.toLowerCase().trim();
    }

    public String getConfirmPassword() {
        return confirmPassword.toLowerCase().trim();
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword.toLowerCase().trim();
    }
}
