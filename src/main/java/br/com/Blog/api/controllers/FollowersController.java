package br.com.Blog.api.controllers;

import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.Followers;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.UserMetrics;
import br.com.Blog.api.entities.enums.FollowerOrFollowering;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/followers")
public class FollowersController {

    @Autowired
    private UnitOfWork uow;

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 10)
    @PostMapping("/follow/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> follow(
            @PathVariable Long userId,
            HttpServletRequest request
    ) {
        Long id = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.getV2(id);
        User followed = this.uow.userService.getV2(userId);

        Followers follower = this.uow.followersService.follow(user, followed);

        UserMetrics metrics = this.uow.userMetricsService.get(follower.getFollower());
        this.uow.userMetricsService.incrementMetric(
                metrics, FollowerOrFollowering.FOLLOWERING
        );

        this.uow.userMetricsService.incrementMetric(
                this.uow.userMetricsService.get(follower.getFollowed()), FollowerOrFollowering.FOLLOWER
        );

        var response = this.uow.responseDefault.response(
                "You are follower this the " + follower.getFollower().getName(),
                200,
                request.getRequestURL().toString(),
                follower,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED );
    }

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 10)
    @PostMapping("/unfollow/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> unfollow(
            @PathVariable Long userId,
            HttpServletRequest request
    ) {
        Long id = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.getV2(id);
        User followed = this.uow.userService.getV2(userId);

        Followers unfollowed = this.uow.followersService.unfollow(user, followed);

        UserMetrics metrics = this.uow.userMetricsService.get(unfollowed.getFollower());
        this.uow.userMetricsService.decrementMetric(
                metrics, FollowerOrFollowering.UNFOLLOWERING
        );

        this.uow.userMetricsService.decrementMetric(
                this.uow.userMetricsService.get(unfollowed.getFollowed()), FollowerOrFollowering.UNFOLLOWER
        );

        var response = this.uow.responseDefault.response(
                "You have unfollowed " + unfollowed.getFollowed().getName(),
                200,
                request.getRequestURL().toString(),
                null,
                true
        );

        return ResponseEntity.ok(response);
    }

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    @GetMapping("/")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.OK)
    public Page<Followers> getAllFollowed(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Long id = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.getV2(id);

        return this.uow.followersService.getAllFollowed(user, pageable);
    }

    @PostMapping("/areFollowing/{followedId}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 6)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Boolean> areFollowing(@PathVariable Long followedId, HttpServletRequest request){
        Long id = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.getV2(id);
        User followed = this.uow.userService.getV2(followedId);

        Boolean result = this.uow.followersService.areFollowing(user, followed);
        return Map.of("result", result);
    }

    @PostMapping("/mutual/{followedId}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 6)
    public ResponseEntity<?> getMutualFollowed(
            @PathVariable Long followedId,
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Long id = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.getV2(id);
        User followed = this.uow.userService.get(followedId);

        return this.uow.followersService.getMutualFollowed(user, followed, pageable);
    }

}