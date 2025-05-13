package br.com.Blog.api.services;

import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.UserMetrics;
import br.com.Blog.api.repositories.CommentRepository;
import br.com.Blog.api.repositories.PostRepository;
import br.com.Blog.api.repositories.UserMetricsRepository;
import br.com.Blog.api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Async
    @Transactional
    public ResponseEntity<?> Create(User user){
        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User userCreated = this.repository.save(user);

        UserMetrics metrics = new UserMetrics();
        metrics.setUser(userCreated);
        this.metricRepository.save(metrics);

        return new ResponseEntity<>("User created with success!", HttpStatus.CREATED);
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
    public ResponseEntity<?> Delete(Long id){
        User user = this.Get(id);
        this.repository.delete(user);

        return new ResponseEntity<>("User deleted", HttpStatus.OK);
    }

    @Async
    @Transactional
    public ResponseEntity<?> Update(Long id, User user){
        User userForUpdate = this.Get(id);

        userForUpdate.setName(user.getName());
        userForUpdate.setPassword(passwordEncoder.encode(user.getPassword()));

        this.repository.save(userForUpdate);
        return new ResponseEntity<>("User updated with success", HttpStatus.OK);
    }

    @Async
    public ResponseEntity<?> ListPostsOfUser(Long id, Pageable pageable){
        User user = this.Get(id);
        return new ResponseEntity<>(this.postRepository.findAllByUser(user, pageable), HttpStatus.OK);
    }

    @Async
    public ResponseEntity<?> ListCommentsOfUser(Long id, Pageable pageable){
        User user = this.Get(id);

        Page<Comment> list = this.commentRepository.findAllByUser(user, pageable);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Async
    public ResponseEntity<?> Login(String email, String password){
        User user = this.repository.findByEmail(email);

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        String token = jwtService.generateToken((UserDetails) authentication.getPrincipal(), user.getId());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @Async
    @Transactional
    public ResponseEntity<?> logout(Long id) {
        User user = this.Get(id);
        UserMetrics metrics = this.metricsService.get(user);

        metrics.setLastLogin(LocalDateTime.now());

        this.metricRepository.save(metrics);

        return new ResponseEntity<>("Logout make with successfully!", HttpStatus.OK);
    }

}