package br.com.Blog.api.controllers;

import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.UserMetrics;
import br.com.Blog.api.entities.UserPreference;
import br.com.Blog.api.entities.enums.SumOrReduce;
import br.com.Blog.api.services.UserPreferenceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user-preference")
public class UserPreferenceController {

    private final UnitOfWork unit;

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> get(@PathVariable Long id, HttpServletRequest request) {
        UserPreference preference = unit.userPreferenceService.get(id);

        Map<String, Object> response = this.unit.responseDefault.response(
                "Preference founded",
                200,
                request.getRequestURL().toString(),
                preference,
                true
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{categoryId}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> save(@PathVariable Long categoryId, HttpServletRequest request) {
        Long userId = this.unit.jwtService.extractId(request);
        User user = this.unit.userService.getV2(userId);

        Category category = this.unit.categoryService.get(categoryId);

        UserPreference save = this.unit.userPreferenceService.save(user, category);

        UserMetrics metrics = this.unit.userMetricsService.get(user);
        this.unit.userMetricsService.sumOrRedPreferenceCount(metrics, SumOrReduce.SUM);

        Map<String, Object> response = this.unit.responseDefault.response(
                "Preference saved with successfully",
                200,
                request.getRequestURL().toString(),
                save,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> remove(@PathVariable Long id, HttpServletRequest request) {
        UserPreference preference = unit.userPreferenceService.get(id);

        this.unit.userPreferenceService.remove(preference);

        Long userId = this.unit.jwtService.extractId(request);
        User user = this.unit.userService.getV2(userId);

        UserMetrics metrics = this.unit.userMetricsService.get(user);
        this.unit.userMetricsService.sumOrRedPreferenceCount(metrics, SumOrReduce.SUM);

        Map<String, Object> response = this.unit.responseDefault.response(
                "Preference deleted",
                200,
                request.getRequestURL().toString(),
                preference,
                true
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> getAllOfUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            HttpServletRequest request
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Long userId = this.unit.jwtService.extractId(request);
        User user = this.unit.userService.getV2(userId);

        Page<UserPreference> allOfUser = this.unit.userPreferenceService.getAllOfUser(user, pageable);

        return new ResponseEntity<>(allOfUser, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> getAllOfAnotherUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @PathVariable Long userId,
            HttpServletRequest request
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        User user = this.unit.userService.get(userId);

        Page<UserPreference> allOfUser = this.unit.userPreferenceService.getAllOfUser(user, pageable);

        return new ResponseEntity<>(allOfUser, HttpStatus.OK);
    }
}