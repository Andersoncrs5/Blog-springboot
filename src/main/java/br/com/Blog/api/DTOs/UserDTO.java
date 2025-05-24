package br.com.Blog.api.DTOs;

import br.com.Blog.api.entities.User;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserDTO(

        Long id,

        @Pattern(regexp = "^[^<>]*$", message = "invalid character")
        @Size(max = 100, message = "Size max of 100")
        @NotBlank(message = "Field name is required")
        String name,

        @Pattern(regexp = "^[^<>]*$", message = "invalid character")
        @Email
        @Size(max = 150, message = "Size max of 150")
        @NotBlank(message = "Field email is required")
        String email,

        @Pattern(regexp = "^[^<>]*$", message = "invalid character")
        @Size(max = 100, message = "Size max of 100")
        @NotBlank(message = "Field password is required")
        String password
) {

    public User MappearUserToCreate(){
        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        return user;
    }

    public User MappearUserToUpdate(){
        User user = new User();

        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        return user;
    }

}
