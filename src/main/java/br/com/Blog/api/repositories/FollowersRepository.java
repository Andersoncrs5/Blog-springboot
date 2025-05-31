package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.Followers;
import br.com.Blog.api.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowersRepository extends JpaRepository<Followers, Long> {

    Page<Followers> findAllByFollowed(User user, Pageable pageable);

    List<Followers> findAllByFollowed(User user);

    Boolean existsByFollowerAndFollowed(User user, User followed);

    Followers findByFollowerAndFollowed(User user, User followed);

    @Query("SELECT f1.followed FROM Followers f1 JOIN Followers f2 ON f1.followed = f2.followed WHERE f1.follower = :user1 AND f2.follower = :user2")
    Page<User> findMutualFollowed(@Param("user1") User user1, @Param("user2") User user2, Pageable pageable);

    Page<Followers> findAllByFollower(User user, Pageable pageable);
}

