package br.com.Blog.api.services;

import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.CommentRepository;
import br.com.Blog.api.repositories.PostRepository;
import br.com.Blog.api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Async
    public ResponseEntity<?> Get(Long id){
        try {
            if (id == null)
                return new ResponseEntity<>("Id is required", HttpStatus.BAD_REQUEST);

            Comment comment = this.repository.findById(id).orElse(null);

            if(comment == null)
                return new ResponseEntity<>("Comment not found", HttpStatus.NOT_FOUND);

            return new ResponseEntity<>(comment, HttpStatus.FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    @Transactional
    public ResponseEntity<?> Create(Comment comment, Long idUser, Long idPost){
        try {
            if (idUser == null)
                return new ResponseEntity<>("Id of user is required", HttpStatus.BAD_REQUEST);

            if (idPost == null)
                return new ResponseEntity<>("Id of post is required", HttpStatus.BAD_REQUEST);

            User user = this.userRepository.findById(idUser).orElse(null);
            Post post = this.postRepository.findById(idPost).orElse(null);

            if (user == null)
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            if (post == null)
                return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);

            comment.setName(user.getName());
            comment.setUser(user);
            comment.setPost(post);
            Comment commentModifed = this.repository.save(comment);
            return new ResponseEntity<>(commentModifed, HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    @Transactional
    public ResponseEntity<?> Delete(Long id){
        try {
            if (id == null)
                return new ResponseEntity<>("Id is required", HttpStatus.BAD_REQUEST);

            Comment comment = this.repository.findById(id).orElse(null);

            if(comment == null)
                return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);

            this.repository.delete(comment);
            return new ResponseEntity<>(comment, HttpStatus.FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    @Transactional
    public ResponseEntity<?> Update(Comment comment){
        try {
            Comment commentToUpdate = this.repository.findById(comment.getId()).orElse(null);

            if (commentToUpdate == null)
                return new ResponseEntity<>("Comment not found", HttpStatus.NOT_FOUND);

            commentToUpdate.setContent(comment.getContent());

            commentToUpdate.setIsEdited(true);
            Comment commentUpdated = this.repository.save(commentToUpdate);

            return new ResponseEntity<>(commentUpdated, HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    public ResponseEntity<?> GetAllCommentsOfPost(Long idPost){
        try {
            if (idPost == null)
                return new ResponseEntity<>("Id is required", HttpStatus.BAD_REQUEST);

            Comment comment = this.repository.findById(idPost).orElse(null);

            if(comment == null)
                return new ResponseEntity<>("Comment not found", HttpStatus.NOT_FOUND);


            return new ResponseEntity<>(this.repository.findAllByPostId(idPost), HttpStatus.FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
