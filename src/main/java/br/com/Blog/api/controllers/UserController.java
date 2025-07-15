package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.LoginDTO;
import br.com.Blog.api.DTOs.UserDTO;
import br.com.Blog.api.Specifications.PostSpecification;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.UserMetrics;
import br.com.Blog.api.services.response.ResponseDefault;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private ResponseDefault responseDefault;
    @Autowired
    private UnitOfWork uow;

//    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/getMetric")
    @RateLimit(capacity = 24, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> getMetric(HttpServletRequest request) {
        Long userId = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.getV2(userId);
        UserMetrics metric = this.uow.userMetricsService.get(user);

        var response = this.uow.responseDefault.response(
                "User metric found with successfully",
                200,
                request.getRequestURL().toString(),
                metric,
                true
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getMetricOfUser/{userId}")
    @RateLimit(capacity = 24, refillTokens = 2, refillSeconds = 8)
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getMetricOfUser(
            HttpServletRequest request,
            @PathVariable Long userId
    ) {
        User user = this.uow.userService.get(userId);
        UserMetrics metric = this.uow.userMetricsService.get(user);

        var response = responseDefault.response("User metric found with successfully",200,request.getRequestURL().toString(), metric, true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    @RateLimit(capacity = 24, refillTokens = 2, refillSeconds = 8)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> get(HttpServletRequest request) {
        Long id = this.uow.jwtService.extractId(request);

        User user = this.uow.userService.getV2(id);
        var response = responseDefault.response(
                "User found with successfully",
                200,
                request.getRequestURL().toString(),
                user,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getProfile/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 24, refillTokens = 2, refillSeconds = 8)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getProfile(@PathVariable Long id , HttpServletRequest request) {

        User user = this.uow.userService.get(id);
        var response = responseDefault.response("User found with successfully",200,request.getRequestURL().toString(), user, true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/ListPostsOfUser")
    @ResponseStatus(HttpStatus.OK)
    @RateLimit(capacity = 24, refillTokens = 2, refillSeconds = 8)
    public Page<Post> ListPostsOfUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) LocalDateTime createdAtBefore,
            @RequestParam(required = false) LocalDateTime createdAtAfter,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long likesBefore,
            @RequestParam(required = false) Long likesAfter,
            @RequestParam(required = false) Long dislikesBefore,
            @RequestParam(required = false) Long dislikesAfter,
            @RequestParam(required = false) Long commentsCount,
            @RequestParam(required = false) Long favorites,
            @RequestParam(required = false) Long viewed
    ) {
        List<String> validSortFields = List.of("createdAt", "title", "likes", "dislikes", "commentsCount", "favorites", "viewed");
        if (!validSortFields.contains(sortBy)) {
            sortBy = "createdAt";
        }

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Specification<Post> spec = PostSpecification.filterBy(
                createdAtBefore,
                createdAtAfter,
                title,
                categoryId,
                likesBefore,
                likesAfter,
                dislikesBefore,
                dislikesAfter,
                commentsCount,
                favorites,
                viewed
        );

        Long id = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.getV2(id);
        return this.uow.userService.listPostsOfUser(user, pageable, spec);
    }

    @PostMapping("/register")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 14)
    public ResponseEntity<?> register(@RequestBody @Valid UserDTO dto, HttpServletRequest request){
        User user = this.uow.userService.create(dto.MappearToUser());

        this.uow.userMetricsService.create(user);
        var response = responseDefault.response(
                "User created with successfully",
                201,
                request.getRequestURL().toString(),
                user,
                true
        );
//       this.uow.recoverEmailService.messageWelcome(user.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/")
    @RateLimit(capacity = 12, refillTokens = 2, refillSeconds = 20)
    public ResponseEntity<?> delete(HttpServletRequest request) {
        Long id = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.getV2(id);
        this.uow.userService.delete(user);
        this.uow.redisService.delete(user.getId().toString());
        var response = responseDefault.response(
                "User deleted with successfully",
                200,
                request.getRequestURL().toString(),
                null,
                true
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/")
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 20)
    public ResponseEntity<?> update(@RequestBody @Valid UserDTO dto, HttpServletRequest request) {
        Long id = this.uow.jwtService.extractId(request);
        User userExist = this.uow.userService.getV2(id);

        User user = this.uow.userService.update(userExist, dto.MappearToUser());
        this.uow.redisService.save(user.getId().toString(), user, 10);
        var response = responseDefault.response(
                "User update with successfully",
                200,
                request.getRequestURL().toString(),
                user,
                true
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    @RateLimit(capacity = 12, refillTokens = 2, refillSeconds = 20)
    public ResponseEntity<?> Login(@RequestBody @Valid LoginDTO dto){
        Map<String, String> res = this.uow.userService.login(dto.email().trim().toLowerCase(), dto.password());

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/logout")
    @RateLimit(capacity = 6, refillTokens = 2, refillSeconds = 14)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        Long id = this.uow.jwtService.extractId(request);
        User userExist = this.uow.userService.getV2(id);
        User _ = this.uow.userService.logoutV2(userExist);
        var response = responseDefault.response(
                "Logout make with successfully",
                200,
                request.getRequestURL().toString(),
                "",
                true
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/refresh/{refresh}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 8, refillTokens = 2, refillSeconds = 20)
    public ResponseEntity<?> refresh(@PathVariable String refresh, HttpServletRequest request ){
        Long id = this.uow.jwtService.extractId(request);
        User userExist = this.uow.userService.getV2(id);

        Map<String, String> res = this.uow.userService.refreshToken(refresh, userExist);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}