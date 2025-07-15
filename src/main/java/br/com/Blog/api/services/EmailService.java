package br.com.Blog.api.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String assunto, String content) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(assunto);
        message.setText(content);

        mailSender.send(message);
    }

    @Async
    public void sendEmailWithHtml(String para, String assunto, String html) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(para);
        helper.setSubject(assunto);
        helper.setText(html, true);
        mailSender.send(message);
    }

    public String loadTemplateHtml(String nameFile, Map<String, String> variaveis) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/" + nameFile);
            String conteudo = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            for (Map.Entry<String, String> entry : variaveis.entrySet()) {
                conteudo = conteudo.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            return conteudo;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
