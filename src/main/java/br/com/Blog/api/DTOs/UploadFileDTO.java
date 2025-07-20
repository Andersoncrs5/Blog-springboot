package br.com.Blog.api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for file upload request with metadata")
public class UploadFileDTO{
    @Schema(description = "File to be uploaded", type = "string", format = "binary")
    @NotBlank
    private MultipartFile file;

    @Schema(description = "Metadata for the file upload (bucketName and key)")
    @NotBlank
    private String bucketName;

    @NotBlank
    private String key;
    @NotBlank
    private String content;

    @NotNull
    private Long postId;
}