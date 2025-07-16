package br.com.Blog.api.services;

import br.com.Blog.api.entities.Followers;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.FollowersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@RequestMapping("/v1/followers")
public class FollowersService {

    @Autowired
    private FollowersRepository repository;

    @Transactional
    public Followers follow(User user, User followed) {
        log.info("Following user...");
        boolean alreadyFollowing = repository.existsByFollowerAndFollowed(user, followed);

        if (alreadyFollowing) {
            log.info("User " + user.getName() + " already follow " + followed.getName());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already following this user");
        }

        Followers follower = new Followers();
        follower.setFollower(user);
        follower.setFollowed(followed);
        follower.setId(null);

        Followers save = repository.save(follower);
        log.info("Follow saved");
        return save;
    }

    @Transactional
    public Followers unfollow(User user, User followed) {
        log.info("Attempting to unfollow. Follower user ID: {}, Followed user ID: {}",user != null ? user.getId() : "null", followed != null ? followed.getId() : "null");

        Followers followerRecord = repository.findByFollowerAndFollowed(user, followed);

        if (followerRecord == null) {
            log.info("Unfollow failed: Follower record not found for user ID: {} and followed user ID: {}. User was not following.",user != null ? user.getId() : "null", followed != null ? followed.getId() : "null");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not following this user");
        }

        repository.delete(followerRecord);
        log.info("Successfully unfollowed. Follower record ID: {} deleted. Follower user ID: {}, Followed user ID: {}",followerRecord.getId(), user.getId(), followed.getId());
        return followerRecord;
    }

    @Transactional(readOnly = true)
    public Page<Followers> getAllFollowed(User user, Pageable pageable) {
        log.info("Fetching all followed users for user ID: {}. Page: {}, Size: {}",user != null ? user.getId() : "null", pageable.getPageNumber(), pageable.getPageSize());

        Page<Followers> followedPage = repository.findAllByFollower(user, pageable);

        log.info("Found {} followed users for user ID: {} on page {} of {}.", followedPage.getNumberOfElements(), user.getId(), followedPage.getNumber(), followedPage.getTotalPages());
        return followedPage;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getMutualFollowed(User user1, User user2, Pageable pageable) {
        log.info("Fetching mutual followed users between user1 ID: {} and user2 ID: {}. Page: {}, Size: {}", user1 != null ? user1.getId() : "null", user2 != null ? user2.getId() : "null", pageable.getPageNumber(), pageable.getPageSize());

        Page<User> mutuals = repository.findMutualFollowed(user1, user2, pageable);

        log.info("Found {} mutual followed users between user1 ID: {} and user2 ID: {} on page {} of {}.",mutuals.getNumberOfElements(), user1.getId(), user2.getId(), mutuals.getNumber(), mutuals.getTotalPages());
        return ResponseEntity.ok(mutuals);
    }

    @Transactional(readOnly = true)
    public Boolean areFollowing(User user, User followed) {
        log.info("Checking if user ID: {} is following user ID: {}",user != null ? user.getId() : "null", followed != null ? followed.getId() : "null");

        Boolean isFollowing = repository.existsByFollowerAndFollowed(user, followed);

        log.info("Check complete: User ID: {} is {}following user ID: {}", user != null ? user.getId() : "null", isFollowing ? "" : "NOT ", followed != null ? followed.getId() : "null");
        return isFollowing;
    }
}
