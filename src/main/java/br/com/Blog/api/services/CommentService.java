package br.com.Blog.api.services;

import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.CommentMetrics;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    @Autowired
    private CommentRepository repository;

    @Transactional
    public Comment Get(Long id){
        log.info("Starting search comment by id");
        if (id <= 0) {
            log.info("Id came null! returning ResponseStatusException");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");
        }

        log.info("searching comment by id: " + id + " in database");
        Comment comment = this.repository.findById(id).orElse(null);

        if(comment == null) {
            log.info("Comment not found with id: " + id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }

        log.info("Comment founded!");
        return comment;
    }

    @Transactional
    public Comment Create(Comment comment, User user, Post post) {
        log.info("Creating new comment...");
        comment.setName(user.getName());
        comment.setUser(user);
        comment.setPost(post);

        Comment save = this.repository.save(comment);
        log.info("Comment created!!");
        return save;
    }

    @Transactional
    public Comment Delete(Comment comment){
        log.info("deleting comment...");
        this.repository.delete(comment);
        log.info("Comment deleted");
        return comment;
    }

    @Transactional
    public Comment Update(Comment commentToUpdate, Comment comment) {
        log.info("Updating comment....");
        commentToUpdate.setContent(comment.getContent());

        commentToUpdate.setIsEdited(true);

        Comment save = this.repository.save(commentToUpdate);
        log.info("Comment updated!");
        return save;
    }

    @Transactional(readOnly = true)
    public Page<Comment> GetAllCommentsOfPost(Post post, Pageable pageable){
        log.info("Getting all comments of post");
        Page<Comment> allByPost = this.repository.findAllByPost(post, pageable);
        log.info("returning all comments of post");
        return allByPost;
    }

    @Transactional(readOnly = true)
    public Page<Comment> getAllCommentOfUser(User user, Specification<Comment> specs, Pageable pageable) {
        log.info("Getting all comments of user");
        Specification<Comment> specWithUser = specs.and((root, query, cb) ->
                cb.equal(root.get("user"), user)
        );
        log.info("returning all comments of user");
        return this.repository.findAll(specWithUser, pageable);
    }

}