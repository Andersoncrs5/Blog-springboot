package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.LoginDTO;
import br.com.Blog.api.DTOs.UserDTO;
import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.services.RecoverEmailService;
import br.com.Blog.api.services.UserService;
import br.com.Blog.api.services.response.ResponseDefault;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final JwtService jwtService;
    private final ResponseDefault responseDefault;
    private final RecoverEmailService emailService;

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("me")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 8)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> get(HttpServletRequest request) {
        Long id = jwtService.extractId(request);

        User user = this.service.Get(id);
        var response = responseDefault.response("User found with successfully",200,request.getRequestURL().toString(), user, true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/ListPostsOfUser")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 12)
    public ResponseEntity<?> ListPostsOfUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Long id = jwtService.extractId(request);
        return this.service.ListPostsOfUser(id, pageable);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/ListCommentsOfUser")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> ListCommentsOfUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);
        return this.service.ListCommentsOfUser(id, pageable);
    }

    @PostMapping("/register")
    @RateLimit(capacity = 16, refillTokens = 2, refillSeconds = 14)
    public ResponseEntity<?> register(@RequestBody @Valid UserDTO dto, HttpServletRequest request){
        User user = this.service.Create(dto.MappearUserToCreate());

        var response = responseDefault.response("User created with successfully",201,request.getRequestURL().toString(), user, true);
        this.emailService.messageWelcome(user.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    @RateLimit(capacity = 8, refillTokens = 2, refillSeconds = 20)
    public ResponseEntity<?> delete(HttpServletRequest request) {
        Long id = jwtService.extractId(request);

        var response = responseDefault.response("User deleted with successfully",200,request.getRequestURL().toString(), "", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping
    @RateLimit(capacity = 8, refillTokens = 2, refillSeconds = 20)
    public ResponseEntity<?> update(@RequestBody @Valid UserDTO dto, HttpServletRequest request) {
        Long id = jwtService.extractId(request);

        var user = this.service.Update(id, dto.MappearUserToUpdate());
        var response = responseDefault.response("User update with successfully",200,request.getRequestURL().toString(), user, true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    @RateLimit(capacity = 8, refillTokens = 2, refillSeconds = 20)
    public ResponseEntity<?> Login(@RequestBody @Valid LoginDTO dto){
        Map<String, String> res = this.service.Login(dto.email(), dto.password());

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/logout")
    @RateLimit(capacity = 8, refillTokens = 2, refillSeconds = 20)
    public ResponseEntity<?> logout(HttpServletRequest request) {
        Long id = jwtService.extractId(request);

        this.service.logout(id);
        var response = responseDefault.response("Logout make with successfully",200,request.getRequestURL().toString(), "", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/refresh/{refresh}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 8, refillTokens = 2, refillSeconds = 20)
    public ResponseEntity<?> refresh(@PathVariable String refresh ){
        Map<String, String> res = this.service.refreshToken(refresh);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}