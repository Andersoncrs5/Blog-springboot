package br.com.Blog.api.services;

import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.PostRepository;
import br.com.Blog.api.repositories.UserRepository;
import br.com.Blog.api.services.response.ResponseTokens;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public User create(User user){
        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return this.repository.save(user);
    }

    @Transactional(readOnly = true)
    public User get(Long id) {
        if (id <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");

        User user = this.repository.findById(id).orElse(null);

        if(user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        redisTemplate.opsForValue().set(String.valueOf(id), user);

        return user;
    }

    @Transactional(readOnly = true)
    public User getV2(Long id) {
        if (id == null || id <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");

        User userCached = (User) redisTemplate.opsForValue().get(id.toString());

        if (userCached != null)
            return userCached;

        User user = this.repository.findById(id).orElse(null);

        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        redisTemplate.opsForValue().set(id.toString(), user, Duration.ofMinutes(15));

        return user;
    }

    @Transactional
    public void delete(User user){
        this.repository.delete(user);
    }

    @Transactional
    public User update(User userForUpdate, User user){
        userForUpdate.setName(user.getName());
        userForUpdate.setPassword(passwordEncoder.encode(user.getPassword()));

        return this.repository.save(userForUpdate);
    }

    @Transactional(readOnly = true)
    public Page<Post> listPostsOfUser(User user, Pageable pageable, Specification<Post> spec){
        Specification<Post> specification = spec.and((root, query, cb) ->
            cb.equal(root.get("user"), user)
        );

        return postRepository.findAll(specification, pageable);
    }

    @Transactional
    public Map<String, String> login(String email, String password){
        User user = this.repository.findByEmail(email);

        if (user == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        if (!this.passwordEncoder.matches(password, user.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

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

    @Transactional
    public User logout(User user) {
        user.setRefreshToken("");
        return this.repository.save(user);
    }

    @Transactional
    public Map<String, String> refreshToken(String refreshToken) {
        if (refreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RefreshToken is required");
        }

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