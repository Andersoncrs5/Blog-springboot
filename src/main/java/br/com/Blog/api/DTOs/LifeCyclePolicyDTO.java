package br.com.Blog.api.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LifeCyclePolicyDTO {
    @NotBlank
    private String bucketName;
    @NotBlank
    private String prefix;
}
