package br.com.Blog.api.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FavoritePostDTO(
        @NotNull
        @Positive
        Long idUser,

        @Positive
        @NotNull
        Long idPost
) {
}
