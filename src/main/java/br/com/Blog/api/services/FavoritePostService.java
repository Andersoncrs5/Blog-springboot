package br.com.Blog.api.services;

import br.com.Blog.api.DTOs.FavoritePostDTO;
import br.com.Blog.api.entities.FavoritePost;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.FavoritePostRepository;
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
public class FavoritePostService {

    private final FavoritePostRepository repository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Async
    public ResponseEntity<?> GetAllFavoritePostOfUser(Long idUser){
        try {
            if (idUser == null)
                return new ResponseEntity<>("Id is required", HttpStatus.NOT_FOUND);

            return new ResponseEntity<>(this.repository.findAllByUserId(idUser), HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    @Transactional
    public ResponseEntity<?> Delete(Long idItem){
        try {
            if (idItem == null)
                return new ResponseEntity<>("Id is required", HttpStatus.NOT_FOUND);

            FavoritePost favoritePost = this.repository.findById(idItem).orElse(null);

            if (favoritePost == null)
                return new ResponseEntity<>("Favorite post not found", HttpStatus.NOT_FOUND);

            this.repository.delete(favoritePost);
            return new ResponseEntity<>("favoritePost deleted ", HttpStatus.FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    @Transactional
    public ResponseEntity<?> create(FavoritePostDTO fp){
        try {
            boolean check = this.repository.existsByUserIdAndPostId(fp.idUser(), fp.idPost());

            if (check == true)
                return new ResponseEntity<>("Item already exists", HttpStatus.NOT_FOUND );

            if (fp.idPost() == null && fp.idUser() == null )
                return new ResponseEntity<>("Ids are required", HttpStatus.NOT_FOUND);

            Post post = this.postRepository.findById(fp.idPost()).orElse(null);
            User user = this.userRepository.findById(fp.idUser()).orElse(null);

            if (post == null)
                return new ResponseEntity<>("post not found", HttpStatus.NOT_FOUND);

            if (user == null)
                return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);

            FavoritePost fpToCreate = new FavoritePost();

            fpToCreate.setPost(post);
            fpToCreate.setUser(user);

            FavoritePost fpCreated = this.repository.save(fpToCreate);
            return new ResponseEntity<>(fpCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    public ResponseEntity<?> existsItemSalve(Long idUser, Long idPost){
        try {
            boolean check = this.repository.existsByUserIdAndPostId(idUser, idPost);

            if (check == false)
                return new ResponseEntity<>(false, HttpStatus.NOT_FOUND );

            return new ResponseEntity<>(true, HttpStatus.FOUND );
        } catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
