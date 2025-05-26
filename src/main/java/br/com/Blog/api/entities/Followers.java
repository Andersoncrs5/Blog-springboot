package br.com.Blog.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "followers")
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
public class Followers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower", nullable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(name = "followed", nullable = false)
    private User followed;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
