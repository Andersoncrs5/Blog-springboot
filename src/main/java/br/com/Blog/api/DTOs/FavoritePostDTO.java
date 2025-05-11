package br.com.Blog.api.DTOs;

import jakarta.validation.constraints.NotNull;

public record FavoritePostDTO(
        @NotNull
        Long idUser,
        @NotNull
        Long idPost
) {
}
