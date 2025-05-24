package br.com.Blog.api.controllers;

import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.services.FollowersService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(name = "/v1/followers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class FollowersController {

    private final FollowersService service;
    private final JwtService jwtService;

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 10)
    @PostMapping("/follow/{userId}")
    public ResponseEntity<?> follow(
            @PathVariable Long userId,
            HttpServletRequest request
    ) {
        Long id = jwtService.extractId(request);
        return this.service.follow(id, userId);
    }

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 10)
    @PostMapping("/unfollow/{userId}")
    public ResponseEntity<?> unfollow(
            @PathVariable Long userId,
            HttpServletRequest request
    ) {
        Long id = jwtService.extractId(request);

        return this.service.unfollow(id, userId);
    }

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    @GetMapping("/")
    public ResponseEntity<?> getAllFollowed(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Long id = jwtService.extractId(request);

        return this.service.getAllFollowed(id, pageable);
    }

    @PostMapping("/areFollowing/{userId}")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 6)
    @ResponseStatus(HttpStatus.OK)
    public Boolean areFollowing(@PathVariable Long userId, HttpServletRequest request){
        Long id = jwtService.extractId(request);

        return this.service.areFollowing(id, userId);
    }

    @PostMapping("/mutual/{userId}")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 6)
    public ResponseEntity<?> getMutualFollowed(
            @PathVariable Long userId,
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Long id = jwtService.extractId(request);

        return this.service.getMutualFollowed(id, userId, pageable);
    }



}
