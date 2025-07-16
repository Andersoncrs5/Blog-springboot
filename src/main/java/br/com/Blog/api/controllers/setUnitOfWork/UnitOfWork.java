package br.com.Blog.api.controllers.setUnitOfWork;

import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.services.*;
import br.com.Blog.api.services.response.ResponseDefault;
import br.com.Blog.api.utils.mappers.UserConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnitOfWork {

    public final CategoryService categoryService;
    public final CommentService commentService;
    public final CommentMetricsService commentMetricsService;
    public final FavoriteCommentService favoriteCommentService;
    public final FavoritePostService favoritePostService;
    public final FollowersService followersService;
    public final PostLikeService postLikeService;
    public final PostMetricsService postMetricsService;
    public final PostService postService;
    public final RecoverEmailService recoverEmailService;
    public final UserMetricsService userMetricsService;
    public final UserService userService;
    public final JwtService jwtService;
    public final CommentLikeService commentLikeService;
    public final NotificationsService notificationsService;
    public final ResponseDefault responseDefault;
    public final RedisService redisService;
    public final UserPreferenceService userPreferenceService;
    public final UserConfigService userConfigService;
    public final AdmService admService;

    // MAPPEAR
    public final UserConfigMapper userConfigMapper;

}
