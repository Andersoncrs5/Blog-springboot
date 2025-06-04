package br.com.Blog.api.services;

import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.PostMetrics;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.repositories.PostMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PostMetricsService {

    private final PostMetricsRepository repository;

    @Async
    @Transactional
    public void sumOrReduceLikeOrDislike(PostMetrics metric, ActionSumOrReduceComment action, LikeOrUnLike likeOrUnLike) {
        if (action == ActionSumOrReduceComment.SUM && likeOrUnLike == LikeOrUnLike.LIKE ) {
            metric.setLikes(metric.getLikes() + 1);
        }

        if(action == ActionSumOrReduceComment.REDUCE && likeOrUnLike == LikeOrUnLike.LIKE ) {
            metric.setLikes(metric.getLikes() - 1);
        }

        if (action == ActionSumOrReduceComment.SUM && likeOrUnLike == LikeOrUnLike.UNLIKE ) {
            metric.setDislikes(metric.getDislikes() + 1);
        }

        if(action == ActionSumOrReduceComment.REDUCE && likeOrUnLike == LikeOrUnLike.UNLIKE ) {
            metric.setDislikes(metric.getDislikes() - 1);
        }

        this.repository.save(metric);
    }

    @Async
    @Transactional
    public void create(Post post) {
        PostMetrics metrics = new PostMetrics();
        metrics.setPost(post);
        metrics.setId(null);

        this.repository.save(metrics);
    }

    @Async
    @Transactional(readOnly = true)
    public PostMetrics get(Post post) {
        if (post == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post is required!");
        }

        Optional<PostMetrics> metric = this.repository.findByPost(post);

        if (metric.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post metric not found");
        }

        return metric.get();
    }

    @Async
    @Transactional
    public void sumOrReduceComments(PostMetrics metric, ActionSumOrReduceComment action) {
        if (action == ActionSumOrReduceComment.SUM) {
            metric.setComments(metric.getComments() + 1);
        } else {
            metric.setComments(metric.getComments() - 1);
        }

        metric.setLastInteractionAt(LocalDateTime.now());

        this.repository.save(metric);
    }

    @Async
    @Transactional
    public void sumOrReduceFavorite(PostMetrics metric, ActionSumOrReduceComment action) {
        if (action == ActionSumOrReduceComment.SUM) {
            metric.setFavorites(metric.getFavorites() + 1);
        }

        if (action == ActionSumOrReduceComment.REDUCE) {
            metric.setFavorites(metric.getFavorites() - 1);
        }

        metric.setLastInteractionAt(LocalDateTime.now());
        this.repository.save(metric);
    }

    @Async
    @Transactional
    public void clicks(PostMetrics metric) {
        metric.setClicks(metric.getClicks() + 1);
        metric.setEditedTimes(metric.getEditedTimes() + 1);

        metric.setLastInteractionAt(LocalDateTime.now());
        this.repository.save(metric);
    }

    @Async
    @Transactional
    public void viewed(PostMetrics metric){
        metric.setViewed(metric.getViewed() + 1);

        metric.setLastInteractionAt(LocalDateTime.now());
        this.repository.save(metric);
    }

}
