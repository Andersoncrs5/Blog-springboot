package br.com.Blog.api.DTOs;

import br.com.Blog.api.entities.Category;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryDTO(
        @Pattern(regexp = "^[^<>]*$", message = "invalid character")
        @NotBlank(message = "Field name is required")
        @Size(max = 100, message = "Size max is 100")
        String name
) {
    public Category MappearToCategory(){
        Category category = new Category();

        category.setName(name);

        return category;
    }

}
