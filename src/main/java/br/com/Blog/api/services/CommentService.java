package br.com.Blog.api.services;

import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.CommentMetrics;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final CommentMetricsService metricsService;

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
    public Comment Create(Comment comment, User user, Post post){
        comment.setName(user.getName());
        comment.setUser(user);
        comment.setPost(post);

        return this.repository.save(comment);
    }

    @Async
    @Transactional
    public Comment Delete(Long id){
        Comment comment = this.Get(id);

        this.repository.delete(comment);
        return comment;
    }

    @Async
    @Transactional
    public Comment Update(Long id, Comment comment){
        Comment commentToUpdate = this.Get(id);

        commentToUpdate.setContent(comment.getContent());

        commentToUpdate.setIsEdited(true);

        return this.repository.save(commentToUpdate);
    }

    @Async
    @Transactional(readOnly = true)
    public Page<Comment> GetAllCommentsOfPost(Post post, Pageable pageable){
        return this.repository.findAllByPost(post, pageable);
    }

    @Async
    @Transactional(readOnly = true)
    public Page<Comment> getAllCommentOfUser(User user, Specification<Comment> specs, Pageable pageable) {

        Specification<Comment> specWithUser = specs.and((root, query, cb) ->
                cb.equal(root.get("user"), user)
        );

        return this.repository.findAll(specWithUser, pageable);
    }

    @Async
    @Transactional(readOnly = true)
    public CommentMetrics getMetric(Long commentId) {
        Comment comment = this.Get(commentId);

        return this.metricsService.get(comment);
    }

}