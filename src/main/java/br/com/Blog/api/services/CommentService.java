package br.com.Blog.api.services;

import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
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

    @Async
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

        this.repository.save(comment);

        return new ResponseEntity<>("Comment created with success!", HttpStatus.CREATED);
    }

    @Async
    @Transactional
    public ResponseEntity<?> Delete(Long id){
        Comment comment = this.Get(id);

        this.repository.delete(comment);
        return new ResponseEntity<>("Comment deleted with success", HttpStatus.OK);

    }

    @Async
    @Transactional
    public ResponseEntity<?> Update(Long id, Comment comment){
        Comment commentToUpdate = this.Get(id);

        commentToUpdate.setContent(comment.getContent());

        commentToUpdate.setIsEdited(true);
        Comment commentUpdated = this.repository.save(commentToUpdate);

        return new ResponseEntity<>(commentUpdated, HttpStatus.OK);
    }

    @Async
    public ResponseEntity<?> GetAllCommentsOfPost(Long postId, Pageable pageable){
        Post post = this.postService.Get(postId);

        Page<Comment> comments = this.repository.findAllByPost(post, pageable);

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

}