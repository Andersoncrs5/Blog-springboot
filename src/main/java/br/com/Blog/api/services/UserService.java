package br.com.Blog.api.services;

import br.com.Blog.api.DTOs.LoginDTO;
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
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Async
    @Transactional
    public ResponseEntity<?> Create(User user){
        try {
            User userCreated = this.repository.save(user);
            return new ResponseEntity<>(userCreated, HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    public ResponseEntity<?> Get(Long id){
        try {
            if (id == null)
                return new ResponseEntity<>("Id is required", HttpStatus.BAD_REQUEST);


            User user = this.repository.findById(id).orElse(null);

            if(user == null)
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            return new ResponseEntity<>(user, HttpStatus.FOUND);
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


            User user = this.repository.findById(id).orElse(null);

            if(user == null)
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            this.repository.delete(user);

            return new ResponseEntity<>("User deleted", HttpStatus.FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    @Transactional
    public ResponseEntity<?> Update(User user){
        try {
            User userUpdated = this.repository.save(user);
            return new ResponseEntity<>(userUpdated, HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    public ResponseEntity<?> ListPostsOfUser(Long id){
        try {
            if (id == null)
                return new ResponseEntity<>("Id is required", HttpStatus.BAD_REQUEST);


            User user = this.repository.findById(id).orElse(null);

            if(user == null)
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            List<Post> list = this.postRepository.findAllByUserId(id);

            return new ResponseEntity<>(list, HttpStatus.FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    public ResponseEntity<?> ListCommentsOfUser(Long id){
        try {
            if (id == null)
                return new ResponseEntity<>("Id is required", HttpStatus.BAD_REQUEST);


            User user = this.repository.findById(id).orElse(null);

            if(user == null)
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            List<Comment> list = this.commentRepository.findAllByUserId(id);

            return new ResponseEntity<>(list, HttpStatus.FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    public ResponseEntity<?> Login(LoginDTO dto){
        try {
            User user = this.repository.findByEmail(dto.email());

            if (user == null)
                return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);

            if(!Objects.equals(user.getPassword(), dto.password()))
                return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);

            return new ResponseEntity<>(true, HttpStatus.FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
