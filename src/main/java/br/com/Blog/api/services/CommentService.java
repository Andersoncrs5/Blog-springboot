package br.com.Blog.api.services;

import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.CommentMetrics;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
import br.com.Blog.api.entities.enums.SumOrReduce;
import br.com.Blog.api.repositories.CommentMetricsRepository;
import br.com.Blog.api.repositories.CommentRepository;
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
public class CommentService {

    private final CommentRepository repository;
    private final UserService userService;
    private final PostService postService;
    private final CommentMetricsRepository metricsRepository;
    private final CommentMetricsService metricsService;
    private final PostMetricsService postMetricsService;
    private final UserMetricsService userMetricsService;

    @Async
    @Transactional
    public Comment Get(Long id){
        if (id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");

        Comment comment = this.repository.findById(id).orElse(null);

        if(comment == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");

        return comment;
    }

    @Async
    @Transactional
    public ResponseEntity<?> Create(Comment comment, Long userId, Long postId){
        User user = this.userService.Get(userId);
        Post post = this.postService.Get(postId);

        comment.setName(user.getName());
        comment.setUser(user);
        comment.setPost(post);

        postMetricsService.sumOrReduceComments(post, ActionSumOrReduceComment.SUM);

        Comment commentCreated = this.repository.save(comment);

        CommentMetrics metrics = new CommentMetrics();

        metrics.setComment(comment);

        this.metricsRepository.save(metrics);
        this.userMetricsService.sumOrRedCommentsCount(user, SumOrReduce.SUM);

        return new ResponseEntity<>("Comment created with success!", HttpStatus.CREATED);
    }

    @Async
    @Transactional
    public ResponseEntity<?> Delete(Long id){
        Comment comment = this.Get(id);

        postMetricsService.sumOrReduceComments(comment.getPost(), ActionSumOrReduceComment.REDUCE);
        this.userMetricsService.sumOrRedCommentsCount(comment.getUser(), SumOrReduce.REDUCE);
        this.repository.delete(comment);
        return new ResponseEntity<>("Comment deleted with success", HttpStatus.OK);

    }

    @Async
    @Transactional
    public ResponseEntity<?> Update(Long id, Comment comment){
        Comment commentToUpdate = this.Get(id);

        commentToUpdate.setContent(comment.getContent());
        this.metricsService.sumEdited(commentToUpdate);

        commentToUpdate.setIsEdited(true);
        Comment commentUpdated = this.repository.save(commentToUpdate);

        return new ResponseEntity<>(commentUpdated, HttpStatus.OK);
    }

    @Async
    @Transactional
    public ResponseEntity<?> GetAllCommentsOfPost(Long postId, Pageable pageable){
        Post post = this.postService.Get(postId);

        Page<Comment> comments = this.repository.findAllByPost(post, pageable);

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @Async
    @Transactional
    public CommentMetrics getMetric(Long commentId) {
        Comment comment = this.Get(commentId);

        return this.metricsService.get(comment);
    }

}