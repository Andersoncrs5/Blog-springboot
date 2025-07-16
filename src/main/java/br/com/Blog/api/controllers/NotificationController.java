package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.NotificationDto;
import br.com.Blog.api.utils.Specifications.NotificationSpecification;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.Notification;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.StatusNotification;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/notification")
public class NotificationController {

    @Autowired
    private UnitOfWork uow;

    @GetMapping("/{notId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> get(@PathVariable Long notId, HttpServletRequest request) {
        Notification notification = this.uow.notificationsService.get(notId);

        var response = this.uow.responseDefault.response("Notification found",200,request.getRequestURL().toString(), notification, true);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> create(@PathVariable Long userId, @RequestBody @Valid NotificationDto dto, HttpServletRequest request) {
        User user = this.uow.userService.getV2(userId);
        Notification not = this.uow.notificationsService.create(user, dto.mappearToNotification());

        var response = this.uow.responseDefault.response(
                "Notification created with successfully",
                201 ,
                request.getRequestURL().toString(),
                not,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public Page<Notification> getAll(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LocalDateTime createdAtAfter,
            @RequestParam(required = false) LocalDateTime createdAtBefore,
            @RequestParam(required = false) boolean isRead,
            @RequestParam(required = false) StatusNotification status

    ) {
        Specification<Notification> specs = NotificationSpecification.filterBy(createdAtBefore, createdAtAfter, isRead, status);
        Pageable pageable = PageRequest.of(page, size);
        Long userId = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.getV2(userId);
        return this.uow.notificationsService.getAllOfUser(user, pageable, specs);
    }

    @GetMapping("/markHowRead/{notId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> markHowRead(@PathVariable Long notId, HttpServletRequest request) {
        Notification notification = this.uow.notificationsService.get(notId);
        this.uow.notificationsService.markHowRead(notification);

        var response = this.uow.responseDefault.response(
                "Notification marked with read!!",
                201 ,
                request.getRequestURL().toString(),
                null,
                true
        );

        return ResponseEntity.ok().body(response);
    }

}
