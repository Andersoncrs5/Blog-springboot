package br.com.Blog.api.DTOs;

public record BlockTimeDTO(
        Integer minutes,
        Integer days,
        Integer months
) {
}
