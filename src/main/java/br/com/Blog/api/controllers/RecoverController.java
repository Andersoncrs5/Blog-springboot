package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.RecoverPasswordDTO;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.services.RecoverEmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/v1/recover")
@RequiredArgsConstructor
public class RecoverController {

    private final RecoverEmailService service;

    @RateLimit(capacity = 8, refillTokens = 2, refillSeconds = 20)
    @PostMapping("/request")
    public ResponseEntity<?> requestToken(@RequestParam String email) {
        service.toCreateTokenOfRecover(email);
        return ResponseEntity.ok().build();
    }

    @RateLimit(capacity = 8, refillTokens = 2, refillSeconds = 20)
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid RecoverPasswordDTO dto) {
        service.toValidToken(dto);
        return ResponseEntity.ok().build();
    }
}
