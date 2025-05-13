package br.com.Blog.api.controllers;

import br.com.Blog.api.config.JwtService;
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
@RequestMapping(name = "followers")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class FollowersController {

    private final FollowersService service;
    private final JwtService jwtService;

    @PostMapping("/follow/{userId}")
    public ResponseEntity<?> follow(
            @PathVariable Long userId,
            HttpServletRequest request
    ) {

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);

        return this.service.follow(id, userId);
    }

    @PostMapping("/unfollow/{userId}")
    public ResponseEntity<?> unfollow(
            @PathVariable Long userId,
            HttpServletRequest request
    ) {

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);

        return this.service.unfollow(id, userId);
    }

    @PostMapping("/")
    public ResponseEntity<?> getAllFollowed(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);

        return this.service.getAllFollowed(id, pageable);
    }

    @PostMapping("/areFollowing/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean areFollowing(@PathVariable Long userId, HttpServletRequest request){

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);

        return this.service.areFollowing(id, userId);
    }

    @PostMapping("/mutual/{userId}")
    public ResponseEntity<?> getMutualFollowed(
            @PathVariable Long userId,
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);

        return this.service.getMutualFollowed(id, userId, pageable);
    }



}
