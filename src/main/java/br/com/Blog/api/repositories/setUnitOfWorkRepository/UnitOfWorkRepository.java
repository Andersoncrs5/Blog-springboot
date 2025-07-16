package br.com.Blog.api.repositories.setUnitOfWorkRepository;

import br.com.Blog.api.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnitOfWorkRepository {

    public final CategoryRepository categoryRepository;
    public final CommentLikeRepository commentLikeRepository;
    public final CommentMetricsRepository commentMetricsRepository;
    public final CommentRepository commentRepository;
    public final FavoriteCommentRepository favoriteCommentRepository;
    public final FavoritePostRepository favoritePostRepository;
    public final FollowersRepository followersRepository;
    public final NotificationRepository notificationRepository;
    public final PostLikeRepository postLikeRepository;
    public final PostMetricsRepository postMetricsRepository;
    public final PostRepository postRepository;
    public final RecoverEmailRepository recoverEmailRepository;
    public final UserMetricsRepository userMetricsRepository;
    public final UserRepository userRepository;
    public final RoleRepository roleRepository;
    public final UserPreferenceRepository userPreferenceRepository;
    public final UserConfigRepository userConfigRepository;

}
