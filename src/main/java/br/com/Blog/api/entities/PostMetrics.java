package br.com.Blog.api.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_metrics")
@Data
@EntityListeners(AuditingEntityListener.class)
public class PostMetrics {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "postId" , nullable = false, unique = true)
    private Post post;

    @Column(nullable = false)
    private Long likes = 0L;

    @Column(nullable = false)
    private Long dislikes = 0L;

    @Column(nullable = false)
    private Long shares = 0L;

    @Column(nullable = false)
    private Long comments = 0L;

    @Column(nullable = false)
    private Long favorites = 0L;

    @Column(nullable = false)
    private Long bookmarks = 0L;

    @Column(nullable = false)
    private Long clicks = 0L;

    @Column
    private Long viewed;

    @Column(name = "last_interaction_at")
    private LocalDateTime lastInteractionAt;

    @Column(name = "engagement_score", nullable = false)
    private Double engagementScore = 0.0;

    @Column(name = "report_count", nullable = false)
    private Long reportCount = 0L;

    @Column(name = "edited_times", nullable = false)
    private Integer editedTimes = 0;

    @Version
    private Long version;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

