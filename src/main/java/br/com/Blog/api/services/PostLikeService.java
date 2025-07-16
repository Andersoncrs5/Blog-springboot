package br.com.Blog.api.services;

import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.PostLike;
import br.com.Blog.api.entities.PostMetrics;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.entities.enums.SumOrReduce;
import br.com.Blog.api.repositories.PostLikeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class PostLikeService {

    @Autowired
    private PostLikeRepository repository;

    @Transactional
    public PostLike reactToPost(User user, Post post, LikeOrUnLike action) {
        log.info("Attempting to react to post. User ID: {}, Post ID: {}, Action: {}", user != null ? user.getId() : "null", post != null ? post.getId() : "null", action);

        boolean alreadyReacted = this.repository.existsByUserAndPost(user, post);

        if (alreadyReacted) {
            log.warn("Reaction to post failed: User ID {} has already reacted to Post ID {}.",user != null ? user.getId() : "null", post != null ? post.getId() : "null");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already reacted to this post");
        }

        PostLike like = new PostLike();
        like.setUser(user);
        like.setPost(post);
        like.setStatus(action);

        PostLike savedLike = this.repository.save(like);
        log.info("Successfully reacted to post. New PostLike ID: {}, User ID: {}, Post ID: {}, Reaction type: {}", savedLike.getId(), user.getId(), post.getId(), action);
        return savedLike;
    }

    @Transactional
    public PostLike removeReaction(Long id) {
        log.info("Attempting to remove reaction with ID: {}", id);

        if (id <= 0) {
            log.warn("Remove reaction failed: Provided ID {} is invalid or missing.", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");
        }

        PostLike like = this.repository.findById(id).orElseThrow(
                () -> {
                    log.info("Remove reaction failed: PostLike with ID {} not found.", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
        );

        this.repository.delete(like);

        log.info("Successfully removed reaction with ID: {}. User ID: {}, Post ID: {}", like.getId(), like.getUser() != null ? like.getUser().getId() : "N/A", like.getPost() != null ? like.getPost().getId() : "N/A");
        return like;
    }

    @Transactional(readOnly = true)
    public boolean exists(User user, Post post) {
        log.info("Checking if user ID: {} has reacted to post ID: {}", user != null ? user.getId() : "null", post != null ? post.getId() : "null");

        boolean exists = this.repository.existsByUserAndPost(user, post);

        log.info("Existence check completed: User ID {} {}reacted to Post ID {}.", user != null ? user.getId() : "null", exists ? "has already " : "has NOT ", post != null ? post.getId() : "null");
        return exists;
    }

    @Transactional(readOnly = true)
    public Page<PostLike> getAllByUser(User user, Pageable pageable) {
        log.info("Fetching all post reactions for user ID: {}. Page: {}, Size: {}", user != null ? user.getId() : "null", pageable.getPageNumber(), pageable.getPageSize());

        if (user == null || user.getId() <= 0) {
            log.warn("Fetching reactions by user failed: User object or user ID is null. Cannot proceed.");
            throw new IllegalArgumentException("User and its ID must not be null to fetch reactions.");
        }

        Page<PostLike> postLikesPage = this.repository.findAllByUser(user, pageable);

        log.info("Found {} post reactions for user ID: {} on page {} of {}.", postLikesPage.getNumberOfElements(), user.getId(), postLikesPage.getNumber(), postLikesPage.getTotalPages());
        return postLikesPage;
    }
}