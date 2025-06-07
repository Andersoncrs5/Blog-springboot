package br.com.Blog.api.unitary;

import br.com.Blog.api.entities.*;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
import br.com.Blog.api.repositories.CommentMetricsRepository;
import br.com.Blog.api.services.CommentMetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentMetricsUnitaryTest {

    @Mock
    private CommentMetricsRepository repository;

    @InjectMocks
    private CommentMetricsService service;
    
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMetric() {
        Comment comment = this.setComment();

        CommentMetrics metrics = new CommentMetrics();
        metrics.setId(1L);
        metrics.setComment(comment);

        when(repository.findByComment(comment)).thenReturn(Optional.of(metrics));

        CommentMetrics result = this.service.get(comment);

        assertNotNull(result);
        assertEquals(metrics.getComment(), result.getComment());
        assertEquals(metrics.getId(), result.getId());

        verify(repository, times(1)).findByComment(comment);
    }

    @Test
    public void testThrowBadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            this.service.get(new Comment());
        });

        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(repository, never()).findByComment(any(Comment.class));
    }

    @Test
    public void testCreateMetric() {
        Comment comment = this.setComment();

        CommentMetrics metrics = new CommentMetrics();
        metrics.setId(1L);
        metrics.setComment(comment);

        when(repository.save(any(CommentMetrics.class))).thenReturn(metrics);

        var result = this.service.create(comment);

        assertNotNull(result);
        assertEquals(metrics.getId(), result.getId());
        assertEquals(metrics.getComment(), result.getComment());

        verify(repository, times(1)).save(any(CommentMetrics.class));
    }

    @Test
    public void testSumView() {
        Comment comment = this.setComment();

        CommentMetrics metricsSave = new CommentMetrics();
        metricsSave.setId(1L);
        metricsSave.setViewsCount(0L);
        metricsSave.setComment(comment);

        CommentMetrics metricsAfterChange = new CommentMetrics();
        metricsAfterChange.setId(1L);
        metricsAfterChange.setViewsCount(1L);
        metricsAfterChange.setComment(comment);

        when(repository.findByComment(comment)).thenReturn(Optional.of(metricsSave));
        when(repository.save(any(CommentMetrics.class))).thenReturn(metricsSave);

        var result = this.service.sumView(comment);

        assertNotNull(result);
        assertEquals(metricsAfterChange.getViewsCount(), result.getViewsCount());
        assertEquals(metricsAfterChange.getComment(), result.getComment());

        verify(repository, times(1)).findByComment(comment);
        verify(repository, times(1)).save(any(CommentMetrics.class));

        InOrder inOrder = inOrder(repository);

        inOrder.verify(repository).findByComment(comment);
        inOrder.verify(repository).save(any(CommentMetrics.class));
    }

    @Test
    public void testReduceFavorite() {
        Comment comment = this.setComment();

        CommentMetrics metricsSave = new CommentMetrics();
        metricsSave.setId(1L);
        metricsSave.setFavorites(1L);
        metricsSave.setComment(comment);

        CommentMetrics metricsAfterChange = new CommentMetrics();
        metricsAfterChange.setId(1L);
        metricsAfterChange.setFavorites(0L);
        metricsAfterChange.setComment(comment);

        when(repository.save(any(CommentMetrics.class))).thenReturn(metricsAfterChange);

        var result = this.service.sumOrReduceFavorite(metricsSave, ActionSumOrReduceComment.REDUCE);

        assertNotNull(result);
        assertEquals(metricsAfterChange.getFavorites(), result.getFavorites());
        assertEquals(metricsAfterChange.getId(), result.getId());
        assertEquals(metricsAfterChange.getComment(), result.getComment());

        verify(repository, times(1)).save(any(CommentMetrics.class));
    }

    @Test
    public void testSumFavorite() {
        Comment comment = this.setComment();

        CommentMetrics metricsSave = new CommentMetrics();
        metricsSave.setId(1L);
        metricsSave.setFavorites(0L);
        metricsSave.setComment(comment);

        CommentMetrics metricsAfterChange = new CommentMetrics();
        metricsAfterChange.setId(1L);
        metricsAfterChange.setFavorites(1L);
        metricsAfterChange.setComment(comment);

        when(repository.save(any(CommentMetrics.class))).thenReturn(metricsAfterChange);

        var result = this.service.sumOrReduceFavorite(metricsSave, ActionSumOrReduceComment.SUM);

        assertNotNull(result);
        assertEquals(metricsAfterChange.getFavorites(), result.getFavorites());
        assertEquals(metricsAfterChange.getId(), result.getId());
        assertEquals(metricsAfterChange.getComment(), result.getComment());

        verify(repository, times(1)).save(any(CommentMetrics.class));
    }

    private Comment setComment() {
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

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("content");
        comment.setName("user");
        comment.setUser(user);
        comment.setPost(postSaved);

        return comment;
    }

}
