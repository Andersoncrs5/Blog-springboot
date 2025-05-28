package br.com.Blog.api.DTOs;

import br.com.Blog.api.entities.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

public record PostDTO(
        @Pattern(regexp = "^[^<>]*$", message = "invalid character")
        @NotBlank(message = "Field title is required")
        String title,

        @Pattern(regexp = "^[^<>]*$", message = "invalid character")
        @NotBlank(message = "Field content is required")
        @Size(min = 50, message = "Size min is 50")
        String content,

        @Min(0)
        @Max(999)
        @Positive
        Integer readingTime,

        @Pattern(regexp = "^[^<>]*$", message = "invalid character")
        @Size(max = 500, message = "Image URL too long")
        @URL(message = "Invalid image URL")
        String imageUrl,

        @Pattern(regexp = "^[^<>]*$", message = "invalid character")
        @Size(max = 500, message = "slug too long")
        String slug
) {
    public Post MappearToPost() {
        Post post = new Post();

        post.setTitle(title);
        post.setContent(content);
        post.setReadingTime(readingTime);
        post.setImageUrl(imageUrl);
        post.setSlug(slug);

        return post;
    }

}
