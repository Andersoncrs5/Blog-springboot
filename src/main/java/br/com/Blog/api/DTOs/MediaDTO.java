package br.com.Blog.api.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public record MediaDTO(
        @URL
        String url,

        String description,

        @NotBlank
        String bucket,

        @NotBlank
        String fileName,

        @NotNull
        int order,

        @NotNull
        Long versionObject
) {
}
