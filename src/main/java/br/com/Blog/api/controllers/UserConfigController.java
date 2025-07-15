package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.UserConfigDTO;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.UserConfig;
import br.com.Blog.api.entities.UserMetrics;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user-config")
@RequiredArgsConstructor
public class UserConfigController {
    
    private final UnitOfWork unit;
    
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 24, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> get(HttpServletRequest request) {
        Long userId = this.unit.jwtService.extractId(request);
        User user = this.unit.userService.getV2(userId);
        UserConfig config = this.unit.userConfigService.getInCached(user);

        var response = this.unit.responseDefault.response(
                "User config founded!",
                200,
                request.getRequestURL().toString(),
                config,
                true
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 20)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> delete(HttpServletRequest request) {
        Long id = this.unit.jwtService.extractId(request);
        User user = this.unit.userService.getV2(id);

        UserConfig config = this.unit.userConfigService.get(user);
        this.unit.userConfigService.delete(config);

        var response = this.unit.responseDefault.response(
                "User config deleted",
                200,
                request.getRequestURL().toString(),
                null,
                true
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @RateLimit(capacity = 8, refillTokens = 2, refillSeconds = 20)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> save(@Valid @RequestBody UserConfigDTO dto, HttpServletRequest request) {
        Long userId = this.unit.jwtService.extractId(request);
        User user = this.unit.userService.getV2(userId);

        UserConfig save = this.unit.userConfigService.save(user, this.unit.userConfigMapper.toUserConfig(dto));

        var response = this.unit.responseDefault.response(
                "User config created!",
                200,
                request.getRequestURL().toString(),
                save,
                true
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    @RateLimit(capacity = 8, refillTokens = 2, refillSeconds = 20)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> update(@Valid @RequestBody UserConfigDTO dto, HttpServletRequest request) {
        Long userId = this.unit.jwtService.extractId(request);
        User user = this.unit.userService.getV2(userId);

        UserConfig config = this.unit.userConfigService.get(user);

        UserConfig save = this.unit.userConfigService.update(this.unit.userConfigMapper.toUserConfig(dto), config);

        var response = this.unit.responseDefault.response(
                "User config created!",
                200,
                request.getRequestURL().toString(),
                save,
                true
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
