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
@Table(name = "users")
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

    public String ThemeName;

    public String PrimaryColor;

    public String SecondaryColor;

    public String AccentColor;

    @Enumerated(EnumType.STRING)
    public FontTypeEnum FontType;

    public Integer FontSize;

    public Double LineHeight;

    public Double LetterSpacing;

    public String BorderColor;

    public Integer BorderSize;

    public Integer BorderRadius;

    @Enumerated(EnumType.STRING)
    public LayoutPreferenceEnum LayoutPreference;

    public Boolean ShowProfilePictureInComments  = true;

    public Boolean EnableAnimations  = true;

    public Boolean NotificationsEnabled  = true;

    public String TimeZone;

    @Version
    private Long version;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
