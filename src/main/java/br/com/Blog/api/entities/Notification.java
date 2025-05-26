package br.com.Blog.api.entities;

import br.com.Blog.api.entities.enums.StatusNotification;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications") // Nome corrigido para condizer com o plural correto
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title = "";

    @Lob
    @Column(nullable = false)
    private String message = "";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusNotification status;

    @Column(name = "post_id")
    private Long postId = 0L;

    @Column(name = "is_read")
    private boolean isRead = false;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}