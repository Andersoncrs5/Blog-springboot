package br.com.Blog.api.entities;

import br.com.Blog.api.entities.enums.LikeOrUnLike;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes")
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Enumerated(EnumType.STRING)
    private LikeOrUnLike status;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
