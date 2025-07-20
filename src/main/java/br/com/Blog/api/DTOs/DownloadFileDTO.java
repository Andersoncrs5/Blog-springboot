package br.com.Blog.api.DTOs;

public record DownloadFileDTO(
        String bucketName,
        String key,
        long expirationDays
) {
}
