package br.com.Blog.api.services;

import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
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
public class PostService {

    private final PostRepository repository;
    private final UserRepository userRepository;

    @Async
    @Transactional
    public ResponseEntity<?> Create(Post post, Long idUser){
        try {
            if (idUser == null)
                return new ResponseEntity<>("Id is required", HttpStatus.BAD_REQUEST);

            User user = this.userRepository.findById(idUser).orElse(null);

            if (user == null)
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

            post.setUser(user);
            Post postModifed = this.repository.save(post);
            return new ResponseEntity<>(postModifed, HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    @Transactional
    public ResponseEntity<?> Update(Post post){
        try {
            Post postForUpdate = this.repository.findById(post.getId()).orElse(null);

            if (postForUpdate == null)
                return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);

            postForUpdate.setTitle(post.getTitle());
            postForUpdate.setContent(post.getContent());

            Post PostUpdated = this.repository.save(postForUpdate);

            return new ResponseEntity<>(PostUpdated, HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    public ResponseEntity<?> Get(Long id){
        try {
            if (id == null)
                return new ResponseEntity<>("Id is required", HttpStatus.BAD_REQUEST);

            Post post = this.repository.findById(id).orElse(null);

            if(post == null)
                return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);

            return new ResponseEntity<>(post, HttpStatus.FOUND);
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

            Post post = this.repository.findById(id).orElse(null);

            if(post == null)
                return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);

            this.repository.delete(post);
            return new ResponseEntity<>("Post deleted", HttpStatus.FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    public ResponseEntity<?> GetAll(){
        try {
            return new ResponseEntity<>(this.repository.findAll(), HttpStatus.FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
