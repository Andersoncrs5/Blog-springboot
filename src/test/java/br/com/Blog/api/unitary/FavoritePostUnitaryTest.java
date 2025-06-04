package br.com.Blog.api.unitary;

import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.FavoritePost;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.FavoritePostRepository;
import br.com.Blog.api.services.FavoritePostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavoritePostUnitaryTest {

    @Mock
    private FavoritePostRepository repository;

    @InjectMocks
    private FavoritePostService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testThrowBadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->  {
            this.service.get(0L);
        });

        ResponseStatusException exception1 = assertThrows(ResponseStatusException.class, () ->  {
            this.service.get(-1L);
        });

        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertNotNull(exception1);
        assertEquals(HttpStatus.BAD_REQUEST, exception1.getStatusCode());
    }

    @Test
    void testThrowNotFound() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->  {
            this.service.get(9L);
        });

        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(this.repository, times(1)).findById(9L);
    }

    @Test
    void testGetFavoritePost(){
        User user = new User();

        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        Category category = new Category();

        category.setId(1L);
        category.setName("TI");
        category.setUser(user);

        Post postSaved = new Post();
        postSaved.setId(1L);
        postSaved.setTitle("post 1");
        postSaved.setContent("post 1");
        postSaved.setReadingTime(9);
        postSaved.setSlug("123456");
        postSaved.setUser(user);
        postSaved.setCategory(category);

        FavoritePost favoritePost = new FavoritePost();
        favoritePost.setId(1L);
        favoritePost.setUser(user);
        favoritePost.setPost(postSaved);

        when(this.repository.findById(1L)).thenReturn(Optional.of(favoritePost));

        FavoritePost favoriteFound = this.service.get(1L);

        assertNotNull(favoriteFound);
        assertEquals(favoriteFound.getId(), favoritePost.getId());
        assertEquals(user, favoritePost.getUser());
        assertEquals(postSaved, favoritePost.getPost());

        verify(this.repository, times(1)).findById(1L);
    }

    @Test
    void testDeleteFavorite() {
        User user = new User();

        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        Category category = new Category();

        category.setId(1L);
        category.setName("TI");
        category.setUser(user);

        Post postSaved = new Post();
        postSaved.setId(1L);
        postSaved.setTitle("post 1");
        postSaved.setContent("post 1");
        postSaved.setReadingTime(9);
        postSaved.setSlug("123456");
        postSaved.setUser(user);
        postSaved.setCategory(category);

        FavoritePost favoritePost = new FavoritePost();
        favoritePost.setId(1L);
        favoritePost.setUser(user);
        favoritePost.setPost(postSaved);

        doNothing().when(repository).delete(favoritePost);

        FavoritePost favoritePostDeleted = this.service.Delete(favoritePost);

        assertNotNull(favoritePostDeleted);
        assertEquals(favoritePostDeleted.getId(), favoritePost.getId());
        assertEquals(user, favoritePostDeleted.getUser());
        assertEquals(postSaved, favoritePostDeleted.getPost());

        verify(repository, times(1)).delete(favoritePost);
    }

    @Test
    void testExistsFavoriteTrue() {
        User user = new User();

        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        Category category = new Category();

        category.setId(1L);
        category.setName("TI");
        category.setUser(user);

        Post postSaved = new Post();
        postSaved.setId(1L);
        postSaved.setTitle("post 1");
        postSaved.setContent("post 1");
        postSaved.setReadingTime(9);
        postSaved.setSlug("123456");
        postSaved.setUser(user);
        postSaved.setCategory(category);

        FavoritePost favoritePost = new FavoritePost();
        favoritePost.setId(1L);
        favoritePost.setUser(user);
        favoritePost.setPost(postSaved);

        when(repository.existsByUserAndPost(user, postSaved)).thenReturn(true);

        boolean check = this.service.existsItemSalve(user, postSaved);

        assertTrue(check);

        verify(repository, times(1)).existsByUserAndPost(user, postSaved);
    }

    @Test
    void testExistsFavoriteFalse() {
        User user = new User();

        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        Category category = new Category();

        category.setId(1L);
        category.setName("TI");
        category.setUser(user);

        Post postSaved = new Post();
        postSaved.setId(1L);
        postSaved.setTitle("post 1");
        postSaved.setContent("post 1");
        postSaved.setReadingTime(9);
        postSaved.setSlug("123456");
        postSaved.setUser(user);
        postSaved.setCategory(category);

        when(repository.existsByUserAndPost(user, postSaved)).thenReturn(false);

        boolean check = this.service.existsItemSalve(user, postSaved);

        assertFalse(check);

        verify(repository, times(1)).existsByUserAndPost(user, postSaved);
    }

}
