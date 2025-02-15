package br.com.Blog.api.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "categories")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150, nullable = false, unique = true)
    @NotBlank(message = "Field name is required")
    private String name;

    private Boolean IsActive = true;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
