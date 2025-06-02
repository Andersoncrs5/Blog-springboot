package br.com.Blog.api.unitary;

import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.PostRepository;
import br.com.Blog.api.services.PostService;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostUnitaryTest {

    @Mock
    private PostRepository repository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreatePost() {
        User user = new User();

        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        Category category = new Category();

        category.setId(1L);
        category.setName("TI");
        category.setUser(user);

        Post post = new Post();
        post.setTitle("post 1");
        post.setContent("post 1");
        post.setReadingTime(9);
        post.setSlug("123456");

        Post postSaved = new Post();
        postSaved.setId(1L);
        postSaved.setTitle("post 1");
        postSaved.setContent("post 1");
        postSaved.setReadingTime(9);
        postSaved.setSlug("123456");
        postSaved.setUser(user);
        postSaved.setCategory(category);

        when(repository.existsBySlug(post.getSlug())).thenReturn(false);
        when(repository.save(any(Post.class))).thenReturn(postSaved);

        Post postCreated = postService.Create(post, user, category);

        assertNotNull(postCreated);
        assertEquals(postSaved.getTitle(), postCreated.getTitle());
        assertEquals(postSaved.getContent(), postCreated.getContent());
        assertEquals(postSaved.getReadingTime(), postCreated.getReadingTime());
        assertEquals(user, postCreated.getUser());
        assertEquals(category, postCreated.getCategory());
        assertNotNull(postCreated.getId());

        verify(repository, times(1)).existsBySlug(post.getSlug());
        verify(repository, times(1)).save(any(Post.class));
    }

    @Test
    public void testGetPost() {
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

        when(repository.findById(postSaved.getId())).thenReturn(Optional.of(postSaved));

        Post postFound = this.postService.Get(postSaved.getId());

        assertNotNull(postFound);
        assertEquals(postSaved.getTitle(), postFound.getTitle());
        assertEquals(postSaved.getContent(), postFound.getContent());
        assertEquals(postSaved.getReadingTime(), postFound.getReadingTime());
        assertEquals(user, postFound.getUser());
        assertEquals(category, postFound.getCategory());
        assertEquals(postFound.getId(), postSaved.getId());

        verify(repository, times(1)).findById(postSaved.getId());
    }

    @Test
    public void testThrowNotFoundPost() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            this.postService.Get(999L);
        });

        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void testThrowBadRequestPost() {
        ResponseStatusException exception1 = assertThrows(ResponseStatusException.class, () -> {
            this.postService.Get(0L);
        });

        ResponseStatusException exception2 = assertThrows(ResponseStatusException.class, () -> {
            this.postService.Get(-1L);
        });

        assertNotNull(exception1);
        assertEquals(HttpStatus.BAD_REQUEST, exception1.getStatusCode());
        assertNotNull(exception2);
        assertEquals(HttpStatus.BAD_REQUEST, exception2.getStatusCode());
    }

    @Test
    public void testDeletePost() {
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

        doNothing().when(this.repository).delete(postSaved);

        Post result = this.postService.Delete(postSaved);

        assertNotNull(result);
        assertEquals(postSaved.getTitle(), result.getTitle());
        assertEquals(postSaved.getContent(), result.getContent());
        assertEquals(postSaved.getReadingTime(), result.getReadingTime());
        assertEquals(user, result.getUser());
        assertEquals(category, result.getCategory());
        assertNotNull(result.getId());

        verify(repository, times(1)).delete(postSaved);
    }

    @Test
    public void testGetAll() {
        Pageable pageable = PageRequest.of(0, 10);
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

        Post postSaved1 = new Post();
        postSaved.setId(2L);
        postSaved.setTitle("post 2");
        postSaved.setContent("post 2");
        postSaved.setReadingTime(9);
        postSaved.setSlug("1234567");
        postSaved.setUser(user);
        postSaved.setCategory(category);

        List<Post> posts = List.of(postSaved, postSaved1);
        Page<Post> page = new PageImpl<>(posts, pageable, posts.size());

        Specification<Post> spec = (root, query, cb) -> cb.conjunction();
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<Post> result = postService.GetAll(pageable, spec);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("post 2", result.getContent().get(0).getTitle());

        verify(repository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    public void testGetAllByCategory() {
        Pageable pageable = PageRequest.of(0, 10);

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

        Post postSaved1 = new Post();
        postSaved.setId(2L);
        postSaved.setTitle("post 2");
        postSaved.setContent("post 2");
        postSaved.setReadingTime(9);
        postSaved.setSlug("1234567");
        postSaved.setUser(user);
        postSaved.setCategory(category);

        List<Post> postsList = List.of(postSaved, postSaved1);

        Page<Post> page = new PageImpl<>(postsList, pageable, postsList.size());

        when(repository.findAllByCategory(category, pageable)).thenReturn(page);

        var result = this.postService.GetAllByCategory(category, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("post 2", result.getContent().get(0).getTitle());

        verify(repository, times(1)).findAllByCategory(category, pageable);
    }

    @Test
    public void testUpdatePost() {
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

        Post postToUpdate = new Post();
        postToUpdate.setTitle("post 1 updated");
        postToUpdate.setContent("post 1 updated");
        postToUpdate.setReadingTime(9);
        postToUpdate.setSlug("123456");
        postToUpdate.setUser(user);
        postToUpdate.setCategory(category);

        Post postAfterUpdated = new Post();
        postAfterUpdated.setId(1L);
        postAfterUpdated.setTitle("post 1 updated");
        postAfterUpdated.setContent("post 1 updated");
        postAfterUpdated.setReadingTime(9);
        postAfterUpdated.setSlug("123456");
        postAfterUpdated.setUser(user);
        postAfterUpdated.setCategory(category);

        when(repository.save(any(Post.class))).thenReturn(postAfterUpdated);

        Post postUpdated = this.postService.Update(postSaved, postToUpdate);

        assertNotNull(postUpdated);
        assertEquals(postUpdated.getId(), postAfterUpdated.getId());
        assertEquals(postUpdated.getTitle(), postAfterUpdated.getTitle());
        assertEquals(postUpdated.getContent(), postAfterUpdated.getContent());
        assertEquals(postUpdated.getReadingTime(), postAfterUpdated.getReadingTime());
        assertEquals(postUpdated.getSlug(), postAfterUpdated.getSlug());
        assertEquals(postUpdated.getUser(), postAfterUpdated.getUser());
        assertEquals(postUpdated.getCategory(), postAfterUpdated.getCategory());

        verify(repository, times(1)).save(any(Post.class));
    }

}