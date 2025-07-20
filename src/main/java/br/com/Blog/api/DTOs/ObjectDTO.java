package br.com.Blog.api.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ObjectDTO(
        @NotBlank
        String bucketName,
        @NotBlank
        String key,
        @NotNull
        Long postId,
        @NotNull
        Long mediaId
) {}
