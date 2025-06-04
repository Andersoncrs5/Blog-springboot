package br.com.Blog.api.unitary;

import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.PostMetrics;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.repositories.PostMetricsRepository;
import br.com.Blog.api.services.PostMetricsService;
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
public class PostMetricsUnitaryTest {

    @Mock
    private PostMetricsRepository repository;

    @InjectMocks
    private PostMetricsService service;

    @BeforeEach()
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getMetrics() {
        Post postSaved = configs();

        PostMetrics metrics = new PostMetrics();
        metrics.setId(1L);
        metrics.setPost(postSaved);

        when(repository.findByPost(any(Post.class))).thenReturn(Optional.of(metrics));

        PostMetrics result = this.service.get(postSaved);

        assertNotNull(result);
        assertEquals(postSaved, result.getPost());
        assertEquals(metrics.getId(), result.getId());

        verify(repository, times(1)).findByPost(postSaved);
    }

    @Test
    public void testThrowBadRequest() {
        Post post = new Post();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            this.service.get(post);
        });

        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(repository, never()).findByPost(any(Post.class));
    }

    @Test
    public void sumLike() {
        Post postSaved = configs();

        PostMetrics metrics = new PostMetrics();
        metrics.setId(1L);
        metrics.setLikes(0L);
        metrics.setDislikes(0L);
        metrics.setPost(postSaved);

        PostMetrics metricsAfterSum = new PostMetrics();
        metricsAfterSum.setId(1L);
        metricsAfterSum.setLikes(1L);
        metricsAfterSum.setDislikes(0L);
        metricsAfterSum.setPost(postSaved);

        when(repository.save(any(PostMetrics.class))).thenReturn(metricsAfterSum);

        PostMetrics metricsAfterMethod = this.service.sumOrReduceLikeOrDislike(metrics, ActionSumOrReduceComment.SUM, LikeOrUnLike.LIKE);

        assertNotNull(metricsAfterMethod);
        assertEquals(1L, metricsAfterMethod.getLikes());
        assertEquals(1L, metricsAfterMethod.getLikes());
        assertEquals(0L, metricsAfterMethod.getDislikes());
        assertEquals(postSaved, metricsAfterMethod.getPost());

        verify(repository, times(1)).save(any(PostMetrics.class));
    }

    @Test
    public void redLike() {
        Post postSaved = configs();

        PostMetrics metrics = new PostMetrics();
        metrics.setId(1L);
        metrics.setLikes(1L);
        metrics.setDislikes(0L);
        metrics.setPost(postSaved);

        PostMetrics metricsAfterRed = new PostMetrics();
        metricsAfterRed.setId(1L);
        metricsAfterRed.setLikes(0L);
        metricsAfterRed.setDislikes(0L);
        metricsAfterRed.setPost(postSaved);

        when(repository.save(any(PostMetrics.class))).thenReturn(metricsAfterRed);

        PostMetrics metricsAfterReduce = this.service.sumOrReduceLikeOrDislike(metrics, ActionSumOrReduceComment.REDUCE, LikeOrUnLike.LIKE);

        assertNotNull(metricsAfterReduce);
        assertEquals(0L, metricsAfterReduce.getLikes());
        assertEquals(0L, metricsAfterReduce.getDislikes());
        assertEquals(postSaved, metricsAfterReduce.getPost());

        verify(repository, times(1)).save(any(PostMetrics.class));
    }

    @Test
    public void sumDislike() {
        Post postSaved = configs();

        PostMetrics metrics = new PostMetrics();
        metrics.setId(1L);
        metrics.setLikes(0L);
        metrics.setDislikes(0L);
        metrics.setPost(postSaved);

        PostMetrics metricsAfterChanged = new PostMetrics();
        metricsAfterChanged.setId(1L);
        metricsAfterChanged.setLikes(0L);
        metricsAfterChanged.setDislikes(1L);
        metricsAfterChanged.setPost(postSaved);

        when(repository.save(any(PostMetrics.class))).thenReturn(metricsAfterChanged);

        PostMetrics metricsUpdated = this.service.sumOrReduceLikeOrDislike(metrics, ActionSumOrReduceComment.SUM, LikeOrUnLike.UNLIKE);

        assertNotNull(metricsUpdated);
        assertEquals(metricsAfterChanged.getLikes(), metricsUpdated.getLikes());
        assertEquals(metricsAfterChanged.getDislikes(), metricsUpdated.getDislikes());
        assertEquals(metricsAfterChanged.getId(), metricsUpdated.getId());
        assertEquals(postSaved, metricsUpdated.getPost());

    }

    @Test
    public void redDisLike() {
        Post postSaved = configs();

        PostMetrics metrics = new PostMetrics();
        metrics.setId(1L);
        metrics.setLikes(0L);
        metrics.setDislikes(1L);
        metrics.setPost(postSaved);

        PostMetrics metricsAfterChanged = new PostMetrics();
        metricsAfterChanged.setId(1L);
        metricsAfterChanged.setLikes(0L);
        metricsAfterChanged.setDislikes(0L);
        metricsAfterChanged.setPost(postSaved);

        when(repository.save(any(PostMetrics.class))).thenReturn(metricsAfterChanged);

        PostMetrics result = this.service.sumOrReduceLikeOrDislike(metrics, ActionSumOrReduceComment.REDUCE, LikeOrUnLike.UNLIKE);

        assertNotNull(result);
        assertEquals(metricsAfterChanged.getLikes(), result.getLikes());
        assertEquals(metricsAfterChanged.getDislikes(), result.getDislikes());
        assertEquals(metricsAfterChanged.getId(), result.getId());
        assertEquals(postSaved, result.getPost());

        verify(repository, times(1)).save(any(PostMetrics.class));
    }

    @Test
    public void testCreateMetric() {
        Post post = configs();

        PostMetrics metricCreated = new PostMetrics();
        metricCreated.setId(1L);
        metricCreated.setPost(post);

        when(repository.save(any(PostMetrics.class))).thenReturn(metricCreated);

        PostMetrics metrics = this.service.create(post);

        assertNotNull(metrics);

        assertEquals(metricCreated.getId(), metrics.getId());
        assertEquals(metricCreated.getPost(), metrics.getPost());

        verify(repository, times(1)).save(any(PostMetrics.class));
    }

    @Test
    public void testSumComments() {
        Post postSaved = configs();

        PostMetrics metrics = new PostMetrics();
        metrics.setId(1L);
        metrics.setComments(0L);
        metrics.setPost(postSaved);

        PostMetrics metricsAfterChanged = new PostMetrics();
        metricsAfterChanged.setId(1L);
        metricsAfterChanged.setComments(1L);
        metricsAfterChanged.setPost(postSaved);

        when(repository.save(any(PostMetrics.class))).thenReturn(metricsAfterChanged);

        PostMetrics result = this.service.sumOrReduceComments(metrics, ActionSumOrReduceComment.SUM);

        assertNotNull(result);
        assertEquals(metricsAfterChanged.getComments(), result.getComments());
        assertEquals(metricsAfterChanged.getId(), result.getId());
        assertEquals(postSaved, result.getPost());

        verify(repository, times(1)).save(any(PostMetrics.class));
    }

    @Test
    public void testRedComments() {
        Post postSaved = configs();

        PostMetrics metrics = new PostMetrics();
        metrics.setId(1L);
        metrics.setComments(1L);
        metrics.setPost(postSaved);

        PostMetrics metricsAfterChanged = new PostMetrics();
        metricsAfterChanged.setId(1L);
        metricsAfterChanged.setComments(0L);
        metricsAfterChanged.setPost(postSaved);

        when(repository.save(any(PostMetrics.class))).thenReturn(metricsAfterChanged);

        PostMetrics result = this.service.sumOrReduceComments(metrics, ActionSumOrReduceComment.REDUCE);

        assertNotNull(result);
        assertEquals(metricsAfterChanged.getComments(), result.getComments());
        assertEquals(metricsAfterChanged.getId(), result.getId());
        assertEquals(postSaved, result.getPost());

        verify(repository, times(1)).save(any(PostMetrics.class));
    }

    @Test
    public void testSumFavorite() {
        Post postSaved = configs();

        PostMetrics metrics = new PostMetrics();
        metrics.setId(1L);
        metrics.setFavorites(0L);
        metrics.setPost(postSaved);

        PostMetrics metricsAfterChanged = new PostMetrics();
        metricsAfterChanged.setId(1L);
        metricsAfterChanged.setFavorites(1L);
        metricsAfterChanged.setPost(postSaved);

        when(repository.save(any(PostMetrics.class))).thenReturn(metricsAfterChanged);

        PostMetrics result = this.service.sumOrReduceFavorite(metrics, ActionSumOrReduceComment.SUM);

        assertNotNull(result);
        assertEquals(metricsAfterChanged.getFavorites(), result.getFavorites());
        assertEquals(metricsAfterChanged.getId(), result.getId());
        assertEquals(postSaved, result.getPost());

        verify(repository, times(1)).save(any(PostMetrics.class));
    }

    @Test
    public void testRedFavorite() {
        Post postSaved = configs();

        PostMetrics metrics = new PostMetrics();
        metrics.setId(1L);
        metrics.setFavorites(1L);
        metrics.setPost(postSaved);

        PostMetrics metricsAfterChanged = new PostMetrics();
        metricsAfterChanged.setId(1L);
        metricsAfterChanged.setFavorites(0L);
        metricsAfterChanged.setPost(postSaved);

        when(repository.save(any(PostMetrics.class))).thenReturn(metricsAfterChanged);

        PostMetrics result = this.service.sumOrReduceFavorite(metrics, ActionSumOrReduceComment.REDUCE);

        assertNotNull(result);
        assertEquals(metricsAfterChanged.getFavorites(), result.getFavorites());
        assertEquals(metricsAfterChanged.getId(), result.getId());
        assertEquals(postSaved, result.getPost());

        verify(repository, times(1)).save(any(PostMetrics.class));
    }

    @Test
    public void testSumClicks() {
        Post postSaved = configs();

        PostMetrics metrics = new PostMetrics();
        metrics.setId(1L);
        metrics.setFavorites(0L);
        metrics.setPost(postSaved);

        PostMetrics metricsAfterChanged = new PostMetrics();
        metricsAfterChanged.setId(1L);
        metricsAfterChanged.setClicks(1L);
        metricsAfterChanged.setPost(postSaved);

        when(repository.save(any(PostMetrics.class))).thenReturn(metricsAfterChanged);

        PostMetrics result = this.service.clicks(metrics);

        assertNotNull(result);
        assertEquals(metricsAfterChanged.getClicks(), result.getClicks());
        assertEquals(metricsAfterChanged.getId(), result.getId());
        assertEquals(postSaved, result.getPost());

        verify(repository, times(1)).save(any(PostMetrics.class));
    }

    @Test
    public void testSumView() {
        Post postSaved = configs();

        PostMetrics metrics = new PostMetrics();
        metrics.setId(1L);
        metrics.setViewed(0L);
        metrics.setPost(postSaved);

        PostMetrics metricsAfterChanged = new PostMetrics();
        metricsAfterChanged.setId(1L);
        metricsAfterChanged.setViewed(1L);
        metricsAfterChanged.setPost(postSaved);

        when(repository.save(any(PostMetrics.class))).thenReturn(metricsAfterChanged);

        PostMetrics result = this.service.viewed(metrics);

        assertNotNull(result);
        assertEquals(metricsAfterChanged.getViewed(), result.getViewed());
        assertEquals(metricsAfterChanged.getId(), result.getId());
        assertEquals(postSaved, result.getPost());

        verify(repository, times(1)).save(any(PostMetrics.class));
    }

    private Post configs() {
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
        
        return postSaved;
    }
    
}
