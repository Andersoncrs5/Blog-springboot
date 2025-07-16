package br.com.Blog.api.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String content) {
        log.info("Sending email to: " + to + " with suject:" + subject);
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
        log.info("Email sended!");
    }

    @Async
    public void sendEmailWithHtml(String to, String subject, String html) throws MessagingException {
        log.info("Sending email to: " + to + " with suject:" + subject + " with html");

        if (to.isBlank()) {
            log.info("Error the to send email! to came null.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is required");
        }

        if (subject.isBlank()) {
            log.info("Error the to send email! subject came null.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "subject is required");
        }

        if (html.isBlank()) {
            log.info("Error the to send email! because html pag came null");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while sending an email to. Please try again later.");
        }

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(message);
        log.info("Email sended!");
    }

    public String loadTemplateHtml(String nameFile, Map<String, String> variaveis) {
        try {
            log.info("Loading template html...");
            log.info("Searching file...");
            ClassPathResource resource = new ClassPathResource("templates/" + nameFile);

            log.info("Reading file...");
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            for (Map.Entry<String, String> entry : variaveis.entrySet()) {
                log.info("Altering file...");
                content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            log.info("Returning template html");
            return content;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}