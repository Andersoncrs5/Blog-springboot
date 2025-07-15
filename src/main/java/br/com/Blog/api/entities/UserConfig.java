package br.com.Blog.api.entities;

import br.com.Blog.api.entities.enums.FontTypeEnum;
import br.com.Blog.api.entities.enums.LayoutPreferenceEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_config")
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
public class UserConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user", updatable = false, nullable = false)
    private User user;

    @Column(nullable = false)
    public String ThemeName;

    @Column(nullable = false)
    public String PrimaryColor;

    @Column(nullable = false)
    public String SecondaryColor;

    @Column(nullable = false)
    public String AccentColor;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public FontTypeEnum FontType;

    @Column(nullable = false)
    public Integer FontSize;

    @Column(nullable = false)
    public Double LineHeight;

    @Column(nullable = false)
    public Double LetterSpacing;

    @Column(nullable = false)
    public String BorderColor;

    @Column(nullable = false)
    public Integer BorderSize;

    @Column(nullable = false)
    public Integer BorderRadius;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public LayoutPreferenceEnum LayoutPreference;

    @Column(nullable = false)
    public Boolean ShowProfilePictureInComments  = true;

    @Column(nullable = false)
    public Boolean EnableAnimations  = true;

    @Column(nullable = false)
    public Boolean NotificationsEnabled  = true;

    @Column(nullable = false)
    public String TimeZone;

    @Version
    private Long version;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
