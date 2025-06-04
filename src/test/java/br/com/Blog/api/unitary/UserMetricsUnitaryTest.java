package br.com.Blog.api.unitary;

import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.UserMetrics;
import br.com.Blog.api.entities.enums.FollowerOrFollowering;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.entities.enums.SumOrReduce;
import br.com.Blog.api.repositories.UserMetricsRepository;
import br.com.Blog.api.services.UserMetricsService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserMetricsUnitaryTest {

    @Mock
    private UserMetricsRepository repository;

    @InjectMocks
    private UserMetricsService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMetric() {
        User user = this.setUser();

        UserMetrics metrics = new UserMetrics();

        metrics.setId(1L);
        metrics.setUser(user);

        when(repository.findByUser(user)).thenReturn(Optional.of(metrics));

        UserMetrics result = this.service.get(user);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(1L, result.getId());

        verify(repository, times(1)).findByUser(user);
    }
    
    @Test
    public void testThrowBadRequest() {
        User user = new User();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            this.service.get(user);
        });

        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(repository, never()).findByUser(any(User.class));
    }

    @Test
    public void testThrowNotFound() {
        User user = this.setUser();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            this.service.get(user);
        });

        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(repository, times(1)).findByUser(user);
    }

    @Test
    public void testCreateUserMetrics() {
        User user = this.setUser();

        UserMetrics metricsCreated = new UserMetrics();

        metricsCreated.setId(1L);
        metricsCreated.setUser(user);

        when(repository.save(any(UserMetrics.class))).thenReturn(metricsCreated);

        UserMetrics result = this.service.create(user);

        assertNotNull(result);
        assertEquals(metricsCreated.getId(), result.getId());
        assertEquals(metricsCreated.getUser(), result.getUser());

        verify(repository, times(1)).save(any(UserMetrics.class));
    }

    @Test
    public void testIncrementMetric() {
        User user = this.setUser();

        UserMetrics metricsSave = new UserMetrics();

        metricsSave.setId(1L);
        metricsSave.setFollowingCount(0L);
        metricsSave.setFollowersCount(0L);
        metricsSave.setUser(user);

        UserMetrics metricsAfterIncrement = new UserMetrics();

        metricsAfterIncrement.setId(1L);
        metricsAfterIncrement.setFollowingCount(1L);
        metricsAfterIncrement.setFollowersCount(0L);
        metricsAfterIncrement.setUser(user);

        when(repository.save(metricsSave)).thenReturn(metricsAfterIncrement);

        UserMetrics result = this.service.incrementMetric(metricsSave, FollowerOrFollowering.FOLLOWERING);

        assertNotNull(result);
        assertEquals(metricsAfterIncrement.getFollowingCount(), result.getFollowingCount());
        assertEquals(metricsAfterIncrement.getFollowersCount(), result.getFollowersCount());
        assertEquals(metricsAfterIncrement.getUser(), result.getUser());
        assertEquals(metricsAfterIncrement.getId(), result.getId());

        verify(repository, times(1)).save(metricsSave);
    }

    @Test
    public void testIncrementMetric_Follower() {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        UserMetrics metrics = new UserMetrics();
        metrics.setId(1L);
        metrics.setFollowingCount(0L);
        metrics.setFollowersCount(0L);
        metrics.setUser(user);

        UserMetrics expected = new UserMetrics();
        expected.setId(1L);
        expected.setFollowingCount(0L);
        expected.setFollowersCount(1L);
        expected.setUser(user);

        when(repository.save(metrics)).thenReturn(expected);

        UserMetrics result = service.incrementMetric(metrics, FollowerOrFollowering.FOLLOWER);

        assertNotNull(result);
        assertEquals(expected.getFollowersCount(), result.getFollowersCount());
        assertEquals(expected.getFollowingCount(), result.getFollowingCount());

        verify(repository, times(1)).save(metrics);
    }

    @Test
    public void testDecrementMetric_Followering() {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        UserMetrics metrics = new UserMetrics();
        metrics.setId(1L);
        metrics.setFollowingCount(2L);
        metrics.setFollowersCount(1L);
        metrics.setUser(user);

        service.decrementMetric(metrics, FollowerOrFollowering.FOLLOWERING);

        assertEquals(1L, metrics.getFollowingCount());
        assertEquals(1L, metrics.getFollowersCount());

        verify(repository, times(1)).save(metrics);
    }

    @Test
    public void testDecrementMetric_Follower() {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        UserMetrics metrics = new UserMetrics();
        metrics.setId(1L);
        metrics.setFollowingCount(1L);
        metrics.setFollowersCount(3L);
        metrics.setUser(user);

        service.decrementMetric(metrics, FollowerOrFollowering.FOLLOWER);

        assertEquals(1L, metrics.getFollowingCount());
        assertEquals(2L, metrics.getFollowersCount());

        verify(repository, times(1)).save(metrics);
    }

    @Test
    public void testSumPostsCount() {
        User user = this.setUser();

        UserMetrics metrics = new UserMetrics();
        metrics.setId(1L);
        metrics.setPostsCount(0L);
        metrics.setUser(user);

        UserMetrics metricsAfterSum = new UserMetrics();
        metricsAfterSum.setId(1L);
        metricsAfterSum.setPostsCount(1L);
        metricsAfterSum.setUser(user);

        when(repository.save(any(UserMetrics.class))).thenReturn(metricsAfterSum);

        UserMetrics result = this.service.sumOrRedPostsCount(metrics, SumOrReduce.SUM);

        assertNotNull(result);
        assertEquals(result.getUser(), metricsAfterSum.getUser());
        assertEquals(result.getId(), metricsAfterSum.getId());
        assertEquals(result.getPostsCount(), metricsAfterSum.getPostsCount());

        verify(repository, times(1)).save(any(UserMetrics.class));
    }

    @Test
    public void testRedPostsCount() {
        User user = this.setUser();

        UserMetrics metrics = new UserMetrics();
        metrics.setId(1L);
        metrics.setPostsCount(1L);
        metrics.setUser(user);

        UserMetrics metricsAfterSum = new UserMetrics();
        metricsAfterSum.setId(1L);
        metricsAfterSum.setPostsCount(0L);
        metricsAfterSum.setUser(user);

        when(repository.save(any(UserMetrics.class))).thenReturn(metricsAfterSum);

        UserMetrics result = this.service.sumOrRedPostsCount(metrics, SumOrReduce.REDUCE);

        assertNotNull(result);
        assertEquals(result.getUser(), metricsAfterSum.getUser());
        assertEquals(result.getId(), metricsAfterSum.getId());
        assertEquals(result.getPostsCount(), metricsAfterSum.getPostsCount());

        verify(repository, times(1)).save(any(UserMetrics.class));
    }

    @Test
    public void testSumCommentsCount() {
        User user = this.setUser();

        UserMetrics metrics = new UserMetrics();
        metrics.setId(1L);
        metrics.setCommentsCount(0L);
        metrics.setUser(user);

        UserMetrics metricsAfterSum = new UserMetrics();
        metricsAfterSum.setId(1L);
        metricsAfterSum.setCommentsCount(1L);
        metricsAfterSum.setUser(user);

        when(repository.save(any(UserMetrics.class))).thenReturn(metricsAfterSum);

        UserMetrics result = this.service.sumOrRedCommentsCount(metrics, SumOrReduce.SUM);

        assertNotNull(result);
        assertEquals(result.getUser(), metricsAfterSum.getUser());
        assertEquals(result.getId(), metricsAfterSum.getId());
        assertEquals(result.getCommentsCount(), metricsAfterSum.getCommentsCount());

        verify(repository, times(1)).save(any(UserMetrics.class));
    }

    @Test
    public void testReqCommentsCount() {
        User user = this.setUser();

        UserMetrics metrics = new UserMetrics();
        metrics.setId(1L);
        metrics.setCommentsCount(1L);
        metrics.setUser(user);

        UserMetrics metricsAfterSum = new UserMetrics();
        metricsAfterSum.setId(1L);
        metricsAfterSum.setCommentsCount(0L);
        metricsAfterSum.setUser(user);

        when(repository.save(any(UserMetrics.class))).thenReturn(metricsAfterSum);

        UserMetrics result = this.service.sumOrRedCommentsCount(metrics, SumOrReduce.REDUCE);

        assertNotNull(result);
        assertEquals(result.getUser(), metricsAfterSum.getUser());
        assertEquals(result.getId(), metricsAfterSum.getId());
        assertEquals(result.getCommentsCount(), metricsAfterSum.getCommentsCount());

        verify(repository, times(1)).save(any(UserMetrics.class));
    }

    @Test
    public void testSumSavedPostsCountFavorite() {
        User user = this.setUser();

        UserMetrics metrics = new UserMetrics();
        metrics.setId(1L);
        metrics.setSavedPostsCount(0L);
        metrics.setUser(user);

        UserMetrics metricsAfterSum = new UserMetrics();
        metricsAfterSum.setId(1L);
        metricsAfterSum.setSavedPostsCount(1L);
        metricsAfterSum.setUser(user);

        when(repository.save(any(UserMetrics.class))).thenReturn(metricsAfterSum);

        UserMetrics result = this.service.sumOrRedSavedPostsCountFavorite(metrics, SumOrReduce.SUM);

        assertNotNull(result);
        assertEquals(result.getUser(), metricsAfterSum.getUser());
        assertEquals(result.getId(), metricsAfterSum.getId());
        assertEquals(result.getSavedPostsCount(), metricsAfterSum.getSavedPostsCount());

        verify(repository, times(1)).save(any(UserMetrics.class));
    }

    @Test
    public void testReqSavedPostsCountFavorite() {
        User user = this.setUser();

        UserMetrics metrics = new UserMetrics();
        metrics.setId(1L);
        metrics.setSavedPostsCount(1L);
        metrics.setUser(user);

        UserMetrics metricsAfterSum = new UserMetrics();
        metricsAfterSum.setId(1L);
        metricsAfterSum.setSavedPostsCount(0L);
        metricsAfterSum.setUser(user);

        when(repository.save(any(UserMetrics.class))).thenReturn(metricsAfterSum);

        UserMetrics result = this.service.sumOrRedSavedPostsCountFavorite(metrics, SumOrReduce.REDUCE);

        assertNotNull(result);
        assertEquals(result.getUser(), metricsAfterSum.getUser());
        assertEquals(result.getId(), metricsAfterSum.getId());
        assertEquals(result.getSavedPostsCount(), metricsAfterSum.getSavedPostsCount());

        verify(repository, times(1)).save(any(UserMetrics.class));
    }

    @Test
    public void testRedSavedCommentsCount() {
        User user = this.setUser();

        UserMetrics metrics = new UserMetrics();
        metrics.setId(1L);
        metrics.setSavedCommentsCount(1L);
        metrics.setUser(user);

        UserMetrics metricsAfterSum = new UserMetrics();
        metricsAfterSum.setId(1L);
        metricsAfterSum.setSavedCommentsCount(0L);
        metricsAfterSum.setUser(user);

        when(repository.save(any(UserMetrics.class))).thenReturn(metricsAfterSum);

        UserMetrics result = this.service.sumOrRedSavedCommentsCount(metrics, SumOrReduce.REDUCE);

        assertNotNull(result);
        assertEquals(result.getUser(), metricsAfterSum.getUser());
        assertEquals(result.getId(), metricsAfterSum.getId());
        assertEquals(result.getSavedCommentsCount(), metricsAfterSum.getSavedCommentsCount());

        verify(repository, times(1)).save(any(UserMetrics.class));
    }

    @Test
    public void testSumSavedCommentsCount() {
        User user = this.setUser();

        UserMetrics metrics = new UserMetrics();
        metrics.setId(1L);
        metrics.setSavedCommentsCount(0L);
        metrics.setUser(user);

        UserMetrics metricsAfterSum = new UserMetrics();
        metricsAfterSum.setId(1L);
        metricsAfterSum.setSavedCommentsCount(1L);
        metricsAfterSum.setUser(user);

        when(repository.save(any(UserMetrics.class))).thenReturn(metricsAfterSum);

        UserMetrics result = this.service.sumOrRedSavedCommentsCount(metrics, SumOrReduce.SUM);

        assertNotNull(result);
        assertEquals(result.getUser(), metricsAfterSum.getUser());
        assertEquals(result.getId(), metricsAfterSum.getId());
        assertEquals(result.getSavedCommentsCount(), metricsAfterSum.getSavedCommentsCount());

        verify(repository, times(1)).save(any(UserMetrics.class));
    }

    @Test
    public void sumDislikeGivenCount() {
        User user = this.setUser();

        UserMetrics metrics = new UserMetrics();
        metrics.setId(1L);
        metrics.setDeslikesGivenCount(0L);
        metrics.setDeslikesGivenCountCreateByDay(0L);
        metrics.setLikesGivenCount(0L);
        metrics.setLikesGivenCountCreateByDay(0L);
        metrics.setUser(user);

        UserMetrics metricsAfterSum = new UserMetrics();
        metricsAfterSum.setId(1L);
        metricsAfterSum.setDeslikesGivenCount(1L);
        metricsAfterSum.setDeslikesGivenCountCreateByDay(1L);
        metricsAfterSum.setLikesGivenCount(0L);
        metricsAfterSum.setLikesGivenCountCreateByDay(0L);
        metricsAfterSum.setUser(user);

        when(repository.save(any(UserMetrics.class))).thenReturn(metricsAfterSum);

        UserMetrics result = this.service.sumOrRedLikesOrDislikeGivenCount(metrics, SumOrReduce.SUM, LikeOrUnLike.UNLIKE);

        assertNotNull(result);
        assertEquals(result.getDeslikesGivenCount(), metricsAfterSum.getDeslikesGivenCount());
        assertEquals(result.getDeslikesGivenCountCreateByDay(), metricsAfterSum.getDeslikesGivenCountCreateByDay());
        assertEquals(result.getLikesGivenCount(), metricsAfterSum.getLikesGivenCount());
        assertEquals(result.getLikesGivenCountCreateByDay(), metricsAfterSum.getLikesGivenCountCreateByDay());
        assertEquals(result.getId(), metricsAfterSum.getId());
        assertEquals(result.getUser(), metricsAfterSum.getUser());

        verify(repository, times(1)).save(any(UserMetrics.class));
    }

    @Test
    public void sumLikeGivenCount() {
        User user = this.setUser();

        UserMetrics metrics = new UserMetrics();
        metrics.setId(1L);
        metrics.setLikesGivenCount(0L);
        metrics.setLikesGivenCountCreateByDay(0L);
        metrics.setDeslikesGivenCount(0L);
        metrics.setDeslikesGivenCountCreateByDay(0L);
        metrics.setUser(user);

        UserMetrics metricsAfterSum = new UserMetrics();
        metricsAfterSum.setId(1L);
        metricsAfterSum.setLikesGivenCount(1L);
        metricsAfterSum.setLikesGivenCountCreateByDay(1L);
        metricsAfterSum.setDeslikesGivenCount(0L);
        metricsAfterSum.setDeslikesGivenCountCreateByDay(0L);
        metricsAfterSum.setUser(user);

        when(repository.save(any(UserMetrics.class))).thenReturn(metricsAfterSum);

        UserMetrics result = service.sumOrRedLikesOrDislikeGivenCount(metrics, SumOrReduce.SUM, LikeOrUnLike.LIKE);

        assertNotNull(result);
        assertEquals(1L, result.getLikesGivenCount());
        assertEquals(1L, result.getLikesGivenCountCreateByDay());
        assertEquals(0L, result.getDeslikesGivenCount());
        assertEquals(0L, result.getDeslikesGivenCountCreateByDay());

        verify(repository, times(1)).save(any(UserMetrics.class));
    }

    @Test
    public void reduceLikeGivenCount() {
        User user = this.setUser();

        UserMetrics metrics = new UserMetrics();
        metrics.setId(1L);
        metrics.setLikesGivenCount(1L);
        metrics.setLikesGivenCountCreateByDay(1L);
        metrics.setDeslikesGivenCount(0L);
        metrics.setDeslikesGivenCountCreateByDay(0L);
        metrics.setUser(user);

        UserMetrics metricsAfterReduce = new UserMetrics();
        metricsAfterReduce.setId(1L);
        metricsAfterReduce.setLikesGivenCount(0L);
        metricsAfterReduce.setLikesGivenCountCreateByDay(0L);
        metricsAfterReduce.setDeslikesGivenCount(0L);
        metricsAfterReduce.setDeslikesGivenCountCreateByDay(0L);
        metricsAfterReduce.setUser(user);

        when(repository.save(any(UserMetrics.class))).thenReturn(metricsAfterReduce);

        UserMetrics result = service.sumOrRedLikesOrDislikeGivenCount(metrics, SumOrReduce.REDUCE, LikeOrUnLike.LIKE);

        assertNotNull(result);
        assertEquals(0L, result.getLikesGivenCount());
        assertEquals(0L, result.getLikesGivenCountCreateByDay());
        assertEquals(0L, result.getDeslikesGivenCount());
        assertEquals(0L, result.getDeslikesGivenCountCreateByDay());

        verify(repository, times(1)).save(any(UserMetrics.class));
    }

    @Test
    public void reduceDislikeGivenCount() {
        User user = this.setUser();

        UserMetrics metrics = new UserMetrics();
        metrics.setId(1L);
        metrics.setDeslikesGivenCount(1L);
        metrics.setDeslikesGivenCountCreateByDay(1L);
        metrics.setLikesGivenCount(0L);
        metrics.setLikesGivenCountCreateByDay(0L);
        metrics.setUser(user);

        UserMetrics metricsAfterReduce = new UserMetrics();
        metricsAfterReduce.setId(1L);
        metricsAfterReduce.setDeslikesGivenCount(0L);
        metricsAfterReduce.setDeslikesGivenCountCreateByDay(0L);
        metricsAfterReduce.setLikesGivenCount(0L);
        metricsAfterReduce.setLikesGivenCountCreateByDay(0L);
        metricsAfterReduce.setUser(user);

        when(repository.save(any(UserMetrics.class))).thenReturn(metricsAfterReduce);

        UserMetrics result = service.sumOrRedLikesOrDislikeGivenCount(metrics, SumOrReduce.REDUCE, LikeOrUnLike.UNLIKE);

        assertNotNull(result);
        assertEquals(0L, result.getDeslikesGivenCount());
        assertEquals(0L, result.getDeslikesGivenCountCreateByDay());
        assertEquals(0L, result.getLikesGivenCount());
        assertEquals(0L, result.getLikesGivenCountCreateByDay());

        verify(repository, times(1)).save(any(UserMetrics.class));
    }


    private User setUser() {
        User user = new User();

        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        return user;
    }

}
