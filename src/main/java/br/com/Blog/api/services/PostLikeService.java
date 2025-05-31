package br.com.Blog.api.services;

import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.PostLike;
import br.com.Blog.api.entities.PostMetrics;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
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
    private final PostMetricsService metricsService;
    private final UserMetricsService userMetricsService;

    @Async
    @Transactional
    public PostLike reactToPost(User user, Post post, LikeOrUnLike action) {
        boolean alreadyReacted = this.repository.existsByUserAndPost(user, post);

        if (alreadyReacted) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already reacted to this post");
        }

        PostLike like = new PostLike();

        like.setUser(user);
        like.setPost(post);
        like.setStatus(action);

        if (action == LikeOrUnLike.LIKE) {
            this.metricsService.sumOrReduceLike(post, ActionSumOrReduceComment.SUM);
            this.userMetricsService.sumOrRedLikesGivenCount(user, SumOrReduce.SUM);
        }

        if (action == LikeOrUnLike.UNLIKE) {
            this.userMetricsService.sumOrRedDisikesGivenCount(user, SumOrReduce.SUM);
            this.metricsService.sumOrReduceDislike(post, ActionSumOrReduceComment.SUM);
        }

        return this.repository.save(like);
    }

    @Async
    @Transactional
    public void removeReaction(Long id) {
        if (id == null || id <= 0 ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");
        }

        PostLike like = this.repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        PostMetrics metrics = this.metricsService.get(like.getPost());

        if (like.getStatus() == LikeOrUnLike.LIKE) {
            this.userMetricsService.sumOrRedLikesGivenCount(like.getUser(), SumOrReduce.REDUCE);
            metrics.setLikes(Math.max(0, metrics.getLikes() - 1));
            this.metricsService.sumOrReduceLike(like.getPost(), ActionSumOrReduceComment.REDUCE);
        } else {
            this.userMetricsService.sumOrRedDisikesGivenCount(like.getUser(), SumOrReduce.REDUCE);
            this.metricsService.sumOrReduceDislike(like.getPost(), ActionSumOrReduceComment.REDUCE);
        }

        this.repository.delete(like);
    }

    @Async
    @Transactional(readOnly = true)
    public boolean exists(User user, Post post) {
        return this.repository.existsByUserAndPost(user, post);
    }

    @Async
    @Transactional
    public Page<PostLike> getAllByUser(User user, Pageable pageable) {
        return this.repository.findAllByUser(user, pageable);
    }

}