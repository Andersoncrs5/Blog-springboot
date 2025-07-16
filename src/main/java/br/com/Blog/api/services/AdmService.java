package br.com.Blog.api.services;

import br.com.Blog.api.DTOs.BlockTimeDTO;
import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.Role;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.setUnitOfWorkRepository.UnitOfWorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdmService {

    private final UnitOfWorkRepository unit;

    @Transactional(readOnly = true)
    public Page<User> getAllUser(Pageable pageable, Specification<User> specs) {
        return unit.userRepository.findAll(specs, pageable);
    }

    public Page<User> listAllAdm(Pageable pageable) {
        final String ADMIN_ROLE_NAME = "ROLE_ADMIN";

        log.info("Attempting to list all users with role: {}. Page: {}, Size: {}", ADMIN_ROLE_NAME, pageable.getPageNumber(), pageable.getPageSize());

        Page<User> adminUsers = this.unit.userRepository.findAllByRoles_Name(ADMIN_ROLE_NAME, pageable);

        log.info("Found {} administrators on page {} of {}.", adminUsers.getNumberOfElements(), adminUsers.getNumber(), adminUsers.getTotalPages());

        return adminUsers;
    }

    @Transactional
    public void AddRoleAdmToUser(User user) {
        log.info("Attempting to add 'ROLE_ADMIN' to user ID: {}", user != null ? user.getId() : "null");

        if (user == null || user.getId() == null) {
            log.warn("Adding admin role failed: Provided user object or its ID is null. Cannot proceed.");
            throw new IllegalArgumentException("User object and its ID must not be null.");
        }

        Optional<Role> roleAdmin = this.unit.roleRepository.findByName("ROLE_ADMIN");

        if (roleAdmin.isEmpty()) {
            // Log that the role was not found
            log.error("Adding admin role failed: 'ROLE_ADMIN' not found in the database. Please ensure it exists.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "role not found");
        }

        if (user.getRoles().contains(roleAdmin.get())) {
            log.info("User ID: {} already has 'ROLE_ADMIN'. No action taken.", user.getId());
            return;
        }

        user.getRoles().add(roleAdmin.get());

        this.unit.userRepository.save(user);
        log.info("Successfully added 'ROLE_ADMIN' to user ID: {}", user.getId());
    }

    @Transactional
    public void RemoveRoleAdmToUser(User user) {
        log.info("Attempting to remove 'ROLE_ADMIN' from user ID: {}", user != null ? user.getId() : "null");

        if (user == null || user.getId() <= 0) {
            log.warn("Removing admin role failed: Provided user object or its ID is null. Cannot proceed.");
            throw new IllegalArgumentException("User object and its ID must not be null.");
        }

        Optional<Role> roleAdmin = this.unit.roleRepository.findByName("ROLE_ADMIN");

        if (roleAdmin.isEmpty()) {
            log.error("Removing admin role failed: 'ROLE_ADMIN' not found in the database. Please ensure it exists.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "role not found");
        }

        if (!user.getRoles().contains(roleAdmin.get())) {
            log.info("User ID: {} does not have 'ROLE_ADMIN'. No action taken.", user.getId());
            return;
        }

        user.getRoles().remove(roleAdmin.get());

        this.unit.userRepository.save(user);
        log.info("Successfully removed 'ROLE_ADMIN' from user ID: {}", user.getId());
    }

    @Transactional
    public void blockUser(User user, BlockTimeDTO dto) {
        log.info("Attempting to block user ID: {}. Block duration: {} days, {} months, {} minutes.",user != null ? user.getId() : "null", dto.days(), dto.months(), dto.minutes());

        if (user == null || user.getId() <= 0) {
            log.warn("User blocking failed: Provided user object or its ID is null. Cannot proceed.");
            throw new IllegalArgumentException("User object and its ID must not be null.");
        }

        LocalDateTime time = LocalDateTime.now();

        if (dto.days() != null && dto.days() > 0) {
            time = time.plusDays(dto.days());
            log.debug("Added {} days. Current block time: {}", dto.days(), time);
        }
        if (dto.months() != null && dto.months() > 0) {
            time = time.plusMonths(dto.months());
            log.debug("Added {} months. Current block time: {}", dto.months(), time);
        }

        if (dto.minutes() != null && dto.minutes() > 0) {
            time = time.minusMinutes(dto.minutes());
            log.debug("Subtracted {} minutes. Current block time: {}", dto.minutes(), time);
        }

        user.setLoginBlockAt(time);

        this.unit.userRepository.save(user);

        log.info("User ID: {} successfully blocked until: {}", user.getId(), time);
    }

    @Transactional
    public void unBlockUser(User user) {
        log.info("Attempting to unblock user ID: {}", user != null ? user.getId() : "null");

        if (user == null || user.getId() <= 0) {
            log.warn("User unblocking failed: Provided user object or its ID is null. Cannot proceed.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User object and its ID must not be null.");
        }

        if (user.getLoginBlockAt() == null) {
            log.info("User ID: {} is already unblocked. No action taken.", user.getId());
            return;
        }

        user.setLoginBlockAt(null);

        this.unit.userRepository.save(user);

        log.info("User ID: {} successfully unblocked.", user.getId());
    }

    @Async
    @Transactional
    public void removePost(Post post) {

        log.info("Attempting to remove post with ID: {}", post != null ? post.getId() : "null");

        if (post == null || post.getId() <= 0) {
            log.warn("Post removal failed: Provided post object or its ID {} is invalid or null.", post != null ? post.getId() : "null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post and its ID must not be null.");
        }

        this.unit.postRepository.delete(post);
        log.info("Successfully removed post with ID: {}", post.getId());
    }

    @Async
    @Transactional
    public void removeComment(Comment comment) {
        log.info("Attempting to remove comment with ID: {}", comment != null ? comment.getId() : "null");

        if (comment == null || comment.getId() == null || comment.getId() <= 0) {
            log.warn("Comment removal failed: Provided comment object or its ID {} is invalid or null.", comment != null ? comment.getId() : "null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment and its ID must not be null.");
        }

        this.unit.commentRepository.delete(comment);
        log.info("Successfully removed comment with ID: {}", comment.getId());
    }



}