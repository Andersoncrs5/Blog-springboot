package br.com.Blog.api.DTOs;

import br.com.Blog.api.entities.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostDTO(
        Long id,

        @NotBlank(message = "Field title is required")
        String title,

        @NotBlank(message = "Field content is required")
        @Size(min = 50, message = "Size min is 50")
        String content
) {
    public Post MappearPostToCreate() {
        Post post = new Post();

        post.setTitle(title);
        post.setContent(content);

        return post;
    }

    public Post MappearPostToUpdate() {
        Post post = new Post();

        post.setId(id);
        post.setTitle(title);
        post.setContent(content);

        return post;
    }

}
