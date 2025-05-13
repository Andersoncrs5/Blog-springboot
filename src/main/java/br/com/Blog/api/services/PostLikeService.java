package br.com.Blog.api.services;

import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.PostLike;
import br.com.Blog.api.entities.PostMetrics;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.entities.enums.SumOrReduce;
import br.com.Blog.api.repositories.PostLikeRepository;
import br.com.Blog.api.repositories.PostMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository repository;
    private final UserService userService;
    private final PostService postService;
    private final PostMetricsService metricsService;
    private final PostMetricsRepository metricsRepository;
    private final UserMetricsService userMetricsService;

    @Async
    @Transactional
    public ResponseEntity<?> reactToPost(Long userId, Long postId, LikeOrUnLike action) {
        User user = userService.Get(userId);
        Post post = this.postService.Get(postId);
        PostMetrics metrics = this.metricsService.get(post);

        boolean alreadyReacted = this.repository.existsByUserAndPost(user, post);

        if (alreadyReacted) {
            return new ResponseEntity<>("User already reacted to this post", HttpStatus.CONFLICT);
        }

        PostLike like = new PostLike();

        like.setUser(user);
        like.setPost(post);
        like.setStatus(action);

        if (action == LikeOrUnLike.LIKE) {
            metrics.setLikes(metrics.getLikes() + 1);
            this.userMetricsService.sumOrRedLikesGivenCount(user, SumOrReduce.SUM);
        }

        if (action == LikeOrUnLike.UNLIKE) {
            this.userMetricsService.sumOrRedDisikesGivenCount(user, SumOrReduce.SUM);
            metrics.setDislikes(metrics.getDislikes() + 1);
        }

        this.repository.save(like);
        this.metricsRepository.save(metrics);

        return new ResponseEntity<>("Reaction added successfully", HttpStatus.OK);
    }

    @Async
    @Transactional
    public ResponseEntity<?> removeReaction(Long id) {
        if (id == null || id <= 0 ) {
            return new ResponseEntity<>("Id is required", HttpStatus.BAD_REQUEST);
        }

        PostLike like = this.repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        PostMetrics metrics = this.metricsService.get(like.getPost());

        if (like.getStatus() == LikeOrUnLike.LIKE) {
            this.userMetricsService.sumOrRedLikesGivenCount(like.getUser(), SumOrReduce.REDUCE);
            metrics.setLikes(Math.max(0, metrics.getLikes() - 1));
        } else {
            this.userMetricsService.sumOrRedDisikesGivenCount(like.getUser(), SumOrReduce.REDUCE);
            metrics.setDislikes(Math.max(0, metrics.getDislikes() - 1));
        }

        this.repository.delete(like);
        this.metricsRepository.save(metrics);

        return new ResponseEntity<>("Like removed", HttpStatus.OK);
    }

    @Async
    @Transactional
    public boolean exists(Long userId, Long postId) {
        User user = this.userService.Get(userId);
        Post post = this.postService.Get(postId);

        return this.repository.existsByUserAndPost(user, post);
    }

    @Async
    @Transactional
    public ResponseEntity<?> getAllByUser(Long userId, Pageable pageable) {
        User user = this.userService.Get(userId);

        Page<PostLike> likes = this.repository.findAllByUser(user, pageable);

        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

}