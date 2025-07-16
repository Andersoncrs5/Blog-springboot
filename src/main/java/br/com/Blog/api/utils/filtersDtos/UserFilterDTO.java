package br.com.Blog.api.utils.filtersDtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserFilterDTO {
    private LocalDateTime createdAtBefore;
    private LocalDateTime createdAtAfter;
    private LocalDateTime loginBlockAt;
    private String name;
    private String email;
}
