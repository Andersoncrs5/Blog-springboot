package br.com.Blog.api.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "favorite_posts")
@Data
public class FavoritePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
