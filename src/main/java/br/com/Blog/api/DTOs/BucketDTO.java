package br.com.Blog.api.DTOs;

import jakarta.validation.constraints.NotBlank;

public record BucketDTO(
        @NotBlank
        String bucket
) {
}
