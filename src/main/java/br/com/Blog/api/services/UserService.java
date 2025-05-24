package br.com.Blog.api.services;

import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.UserMetrics;
import br.com.Blog.api.repositories.CommentRepository;
import br.com.Blog.api.repositories.PostRepository;
import br.com.Blog.api.repositories.UserMetricsRepository;
import br.com.Blog.api.repositories.UserRepository;
import br.com.Blog.api.services.response.ResponseTokens;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserMetricsRepository metricRepository;
    private final UserMetricsService metricsService;
    private final CustomUserDetailsService userDetailsService;

    @Async
    @Transactional
    public User Create(User user){
        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User userCreated = this.repository.save(user);

        UserMetrics metrics = new UserMetrics();
        metrics.setUser(userCreated);

        this.metricRepository.save(metrics);

        return userCreated;
    }

    @Async
    public User Get(Long id){
        if (id == null || id <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");


        User user = this.repository.findById(id).orElse(null);

        if(user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        return user;
    }

    @Async
    @Transactional
    public void Delete(Long id){
        User user = this.Get(id);
        this.repository.delete(user);
    }

    @Async
    @Transactional
    public User Update(Long id, User user){
        User userForUpdate = this.Get(id);

        userForUpdate.setName(user.getName());
        userForUpdate.setPassword(passwordEncoder.encode(user.getPassword()));

        return this.repository.save(userForUpdate);
    }

    @Async
    @Transactional(readOnly = true)
    public ResponseEntity<?> ListPostsOfUser(Long id, Pageable pageable){
        User user = this.Get(id);
        return new ResponseEntity<>(this.postRepository.findAllByUser(user, pageable), HttpStatus.OK);
    }

    @Async
    @Transactional(readOnly = true)
    public ResponseEntity<?> ListCommentsOfUser(Long id, Pageable pageable){
        User user = this.Get(id);

        Page<Comment> list = this.commentRepository.findAllByUser(user, pageable);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Async
    @Transactional
    public Map<String, String> Login(String email, String password){
        User user = this.repository.findByEmail(email);

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        String token = jwtService.generateToken((UserDetails) authentication.getPrincipal(), user.getId());
        String refresh = jwtService.generateRefreshtoken((UserDetails) authentication.getPrincipal(), user.getId());

        user.setRefreshToken(refresh);

        this.repository.save(user);

        ResponseTokens res = new ResponseTokens(token, refresh);
        return res.showTokens();
    }

    @Async
    @Transactional
    public void logout(Long id) {
        User user = this.Get(id);
        UserMetrics metrics = this.metricsService.get(user);

        metrics.setLastLogin(LocalDateTime.now());

        this.metricRepository.save(metrics);
    }

    @Async
    @Transactional
    public Map<String, String> refreshToken(String refreshToken) {
        Claims claims = jwtService.extractAllClaims(refreshToken);

        if (claims.getExpiration().before(new Date())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String username = claims.getSubject();
        Long userId = claims.get("userId", Long.class);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String token = jwtService.generateToken(userDetails, userId);
        String refresh = jwtService.generateRefreshtoken(userDetails, userId);

        ResponseTokens res = new ResponseTokens(token, refresh);
        return res.showTokens();
    }

}