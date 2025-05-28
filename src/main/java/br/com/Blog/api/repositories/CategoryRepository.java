package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.Category;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(@NotBlank(message = "Field name is required") String name);
}
