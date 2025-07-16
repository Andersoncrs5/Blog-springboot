package br.com.Blog.api.services;

import br.com.Blog.api.entities.Followers;
import br.com.Blog.api.entities.Notification;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.StatusNotification;
import br.com.Blog.api.repositories.FollowersRepository;
import br.com.Blog.api.repositories.NotificationRepository;
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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationsService {

    private final NotificationRepository repository;
    private final FollowersRepository followersRepository;

    @Transactional(readOnly = true)
    public Notification get(Long notiId) {
        // Log entry into the method
        log.info("Attempting to retrieve notification by ID: {}", notiId);

        if (notiId == null || notiId <= 0) {
            // Log the reason for throwing an exception due to invalid input
            log.warn("Notification retrieval failed: Provided notification ID {} is invalid or missing.", notiId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID is required");
        }

        Notification notification = this.repository.findById(notiId).orElse(null);

        if (notification == null) {
            // Log that the notification was not found
            log.info("Notification retrieval failed: Notification with ID {} not found in the database.", notiId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }

        // Log successful retrieval
        log.info("Successfully retrieved notification with ID: {} for user ID: {}", notification.getId(), notification.getUser() != null ? notification.getUser().getId() : "N/A");
        return notification;
    }

    @Transactional
    public Notification create(User user, Notification notification) {
        log.info("Attempting to create a new notification. User ID: {}, Initial title: {}", user != null ? user.getId() : "null", notification != null ? notification.getTitle() : "null");

        if (user.getId() <= 0) {
            log.warn("Notification creation failed: User object or user ID is null. Cannot associate notification.");
            throw new IllegalArgumentException("User and its ID must not be null for notification creation.");
        }
        if (notification == null) {
            log.warn("Notification creation failed: Notification object is null.");
            throw new IllegalArgumentException("Notification object must not be null for creation.");
        }

        notification.setUser(user);
        Notification savedNotification = this.repository.save(notification);

        log.info("Successfully created new notification with ID: {} for user ID: {}. Title: {}", savedNotification.getId(), savedNotification.getUser().getId(), savedNotification.getTitle());
        return savedNotification;
    }

    @Transactional
    @Async
    public void notifyFollowersAboutPostCreated(Post post) {
        log.info("Starting asynchronous notification process for post created. Post ID: {}, Actor user ID: {}", post != null ? post.getId() : "null", post != null && post.getUser() != null ? post.getUser().getId() : "null");

        if (post == null || post.getUser().getId() <= 0 || post.getId() <= 0) {
            log.warn("Notification for post created failed: Post or actor user or post ID is null. Cannot proceed.");
            return;
        }

        User actor = post.getUser();
        log.info("Fetching followers for actor user ID: {} (post creator).", actor.getId());

        List<Followers> followers = this.followersRepository.findAllByFollowed(actor);
        log.info("Found {} followers for actor user ID: {}. Preparing to send notifications.", followers.size(), actor.getId());

        for (Followers f : followers) {
            if (f.getFollower() == null) {
                log.warn("Skipping notification for a follower record with null follower user. Follower record ID: {}", f.getId());
                continue;
            }
            Notification notification = new Notification();
            notification.setUser(f.getFollower());
            notification.setTitle("New post from " + actor.getName());
            notification.setMessage("Check out the new post: " + post.getTitle());
            notification.setStatus(StatusNotification.POST_CREATED);
            notification.setPostId(post.getId());

            this.repository.save(notification);
            log.info("Notification created for follower user ID: {} about new post ID: {} from actor user ID: {}. Notification ID: {}",                    f.getFollower().getId(), post.getId(), actor.getId(), notification.getId());
        }
        log.info("Finished asynchronous notification process for post ID: {}. Total notifications sent: {}", post.getId(), followers.size());
    }

    @Transactional(readOnly = true)
    public Page<Notification> getAllOfUser(User user, Pageable pageable, Specification<Notification> spec) {
        log.info("Fetching all notifications for user ID: {}. Page: {}, Size: {}, With additional spec: {}", user != null ? user.getId() : "null", pageable.getPageNumber(), pageable.getPageSize(), spec != null ? "Yes" : "No");

        if (user == null || user.getId() <= 0) {
            log.warn("Retrieving notifications failed: User object or user ID is null. Cannot fetch notifications.");
            throw new IllegalArgumentException("User and its ID must not be null to retrieve notifications.");
        }

        Page<Notification> notificationsPage = this.repository.findAllByUser(user, pageable, spec);

        log.info("Found {} notifications for user ID: {} on page {} of {}.", notificationsPage.getNumberOfElements(), user.getId(), notificationsPage.getNumber(), notificationsPage.getTotalPages());
        return notificationsPage;
    }

    @Transactional
    public void markHowRead(Notification notification) {
        log.info("Attempting to mark notification ID: {} as read.", notification != null ? notification.getId() : "null");

        if (notification == null || notification.getId() <= 0) {
            log.warn("Marking notification as read failed: Notification object or ID is null. Cannot proceed.");
            throw new IllegalArgumentException("Notification object and its ID must not be null to mark as read.");
        }

        if (notification.isRead()) {
            log.info("Notification ID: {} is already marked as read. No action needed.", notification.getId());
            return;
        }

        notification.setRead(true);
        this.repository.save(notification);

        log.info("Successfully marked notification ID: {} as read.", notification.getId());
    }
}
