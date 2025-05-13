package br.com.Blog.api.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_comments")
@EntityListeners(AuditingEntityListener.class)
@Data
public class FavoriteComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Version
    private Long version;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
