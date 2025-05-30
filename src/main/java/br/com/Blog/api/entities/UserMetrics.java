package br.com.Blog.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_metrics")
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class UserMetrics {

    @Id
    private Long id;

    @JsonIgnore
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "followers_count")
    private Long followersCount = 0L;

    @Column(name = "following_count")
    private Long followingCount = 0L;

    @Column(name = "posts_count")
    private Long postsCount = 0L;

    @Column(name = "posts_count_create_day")
    private Long postsCountCreateByDay = 0L;

    @Column(name = "comments_count")
    private Long commentsCount = 0L;

    @Column(name = "comments_count_create_day")
    private Long commentsCountCreateByDay = 0L;

    @Column(name = "likes_given_count")
    private Long likesGivenCount = 0L;

    @Column(name = "likes_given_count_create_day")
    private Long likesGivenCountCreateByDay = 0L;

    @Column(name = "deslikes_given_count")
    private Long deslikesGivenCount = 0L;

    @Column(name = "deslikes_given_count_create_day")
    private Long deslikesGivenCountCreateByDay = 0L;

    @Column(name = "shares_count")
    private Long sharesCount = 0L;

    @Column(name = "reports_received_count")
    private Long reportsReceivedCount = 0L;

    @Column(name = "reputation_score")
    private Double reputationScore = 0.0;

    @Column(name = "media_uploads_count")
    private Long mediaUploadsCount = 0L;

    @Column(name = "saved_posts_count")
    private Long savedPostsCount = 0L;

    @Column(name = "saved_posts_count_create_day")
    private Long savedPostsCountCreateByDay = 0L;

    @Column(name = "saved_comments_count")
    private Long savedCommentsCount = 0L;

    @Column(name = "saved_comments_count_create_day")
    private Long savedCommentsCountCreateByDay = 0L; // =

    @Column(name = "saved_media_count")
    private Long savedMediaCount = 0L;

    @Version
    private Long version;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
