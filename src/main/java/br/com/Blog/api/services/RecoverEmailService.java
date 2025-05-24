package br.com.Blog.api.services;

import br.com.Blog.api.DTOs.RecoverPasswordDTO;
import br.com.Blog.api.entities.RecoverEmail;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.RecoverEmailRepository;
import br.com.Blog.api.repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecoverEmailService {

    private final UserRepository userRepository;
    private final RecoverEmailRepository repository;
    private final EmailService emailService;
    private final PasswordEncoder encoder;

    @Async
    @Transactional
    public ResponseEntity<?> toCreateTokenOfRecover(String email) {
        try {
            User user = userRepository.findByEmail(email);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }

            String token = UUID.randomUUID().toString();
            LocalDateTime expiration = LocalDateTime.now().plusHours(1);

            repository.findByUser(user).ifPresent(repository::delete);

            RecoverEmail recover = new RecoverEmail();
            recover.setToken(token);
            recover.setExpiresAt(expiration);
            recover.setUser(user);

            repository.save(recover);

            String link = "http://127.0.0.1/recover/check-token?token=" + token;

            Map<String, String> variaveis = Map.of(
                    "token", token,
                    "link", link
            );

            String html = emailService.loadTemplateHtml("recover-email.html", variaveis);

            emailService.sendEmailWithHtml(user.getEmail(), "Password recover", html);

            return ResponseEntity.ok().build();

        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error the to send the e-mail.");
        }
    }

    @Async
    @Transactional
    public ResponseEntity<?> toValidToken(RecoverPasswordDTO dto) {
        RecoverEmail recover = repository.findByToken(dto.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"Token invalid"));

        if (recover.getExpiresAt().isBefore(LocalDateTime.now())) {
            this.repository.delete(recover);
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT ,"Token expired");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords must match.");
        }

        User user = userRepository.findByEmail(recover.getUser().getEmail());

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(this.encoder.encode(dto.getPassword()));
        this.userRepository.save(user);

        String msg = "Password updated with success!!!";

        emailService.sendEmail(user.getEmail(), "Password updated", msg);

        this.repository.delete(recover);

        return ResponseEntity.ok().build();
    }

    @Async
    public ResponseEntity<?> messageWelcome(String email) {
        try {
            if (email == null || email.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
            }

            String link = "http://127.0.0.1/";
            String name = email.split("@")[0];

            Map<String, String> variaveis = Map.of(
                    "link", link,
                    "name", name
            );

            String html = emailService.loadTemplateHtml("welcome-message.html", variaveis);

            emailService.sendEmailWithHtml(email, "Welcome to BlogSpace!", html);

            return ResponseEntity.ok().build();
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while trying to send the email.");
        }
    }

}
