package br.com.Blog.api.DTOs;

import br.com.Blog.api.entities.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CommentDTO(

        Long id,
        @Pattern(regexp = "^[^<>]*$", message = "invalid character")
        @NotBlank(message = "Field content is required")
        String content,

        @Pattern(regexp = "^[^<>]*$", message = "invalid character")
        @Size(max = 150, message = "Size max of 150")
        String name
) {
    public Comment MappearCommentToCreate(){
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setName(name);

        return comment;
    }

    public Comment MappearCommentToUpdate(){
        Comment comment = new Comment();

        comment.setId(id);
        comment.setContent(content);
        comment.setName(name);

        return comment;
    }

}
