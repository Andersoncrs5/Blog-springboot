package br.com.Blog.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment_metrics")
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CommentMetrics {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "comment_id", nullable = false, unique = true)
    private Comment comment;

    @Column(nullable = false)
    private Long likes = 0L;

    @Column(nullable = false)
    private Long dislikes = 0L;

    @Column(name = "report_count", nullable = false)
    private Long reportCount = 0L;

    @Column(name = "edited_times", nullable = false)
    private Integer editedTimes = 0;

    @Column(name = "engagement_score", nullable = false)
    private Double engagementScore = 0.0;

    @Column(name = "last_interaction_at")
    private LocalDateTime lastInteractionAt;

    @Column(nullable = false)
    private Long favorites = 0L;

    @Column(name = "replies_count", nullable = false)
    private Long repliesCount = 0L;

    @Column(name = "views_count", nullable = false)
    private Long viewsCount = 0L;

    @Column(name = "last_edited_at")
    private LocalDateTime lastEditedAt;

    @Version
    private Long version;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
