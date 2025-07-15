package br.com.Blog.api.services;

import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.PostMetrics;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.repositories.PostMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PostMetricsService {

    @Autowired
    private PostMetricsRepository repository;

    @Transactional(readOnly = true)
    public PostMetrics get(Post post) {
        if (post.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post is required!");
        }

        Optional<PostMetrics> metric = this.repository.findByPost(post);

        if (metric.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post metric not found");
        }

        return metric.get();
    }

    @Transactional
    public PostMetrics sumOrReduceLikeOrDislike(PostMetrics metric, ActionSumOrReduceComment action, LikeOrUnLike likeOrUnLike) {
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

        return this.repository.save(metric);
    }

    @Transactional
    public PostMetrics create(Post post) {
        PostMetrics metrics = new PostMetrics();
        metrics.setPost(post);
        metrics.setId(null);

        return this.repository.save(metrics);
    }

    @Transactional
    public PostMetrics sumOrReduceComments(PostMetrics metric, ActionSumOrReduceComment action) {
        if (action == ActionSumOrReduceComment.SUM) {
            metric.setComments(metric.getComments() + 1);
        }

        if (action == ActionSumOrReduceComment.REDUCE) {
            metric.setComments(metric.getComments() - 1);
        }

        metric.setLastInteractionAt(LocalDateTime.now());

        return this.repository.save(metric);
    }

    @Transactional
    public PostMetrics sumOrReduceFavorite(PostMetrics metric, ActionSumOrReduceComment action) {
        if (action == ActionSumOrReduceComment.SUM) {
            metric.setFavorites(metric.getFavorites() + 1);
        }

        if (action == ActionSumOrReduceComment.REDUCE) {
            metric.setFavorites(metric.getFavorites() - 1);
        }

        metric.setLastInteractionAt(LocalDateTime.now());
        return this.repository.save(metric);
    }

    @Transactional
    public PostMetrics clicks(PostMetrics metric) {
        metric.setClicks(metric.getClicks() + 1);

        metric.setLastInteractionAt(LocalDateTime.now());
        return this.repository.save(metric);
    }

    @Transactional
    public PostMetrics viewed(PostMetrics metric){
        metric.setViewed(metric.getViewed() + 1);

        metric.setLastInteractionAt(LocalDateTime.now());
        return this.repository.save(metric);
    }

}
