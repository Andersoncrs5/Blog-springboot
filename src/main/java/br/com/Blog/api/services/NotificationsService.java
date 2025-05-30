package br.com.Blog.api.services;

import br.com.Blog.api.entities.Followers;
import br.com.Blog.api.entities.Notification;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.StatusNotification;
import br.com.Blog.api.repositories.FollowersRepository;
import br.com.Blog.api.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationsService {

    private final NotificationRepository repository;
    private final FollowersRepository followersRepository;

    @Async
    @Transactional(readOnly = true)
    public Notification get(Long notiId) {
        if (notiId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID is required");
        }

        Notification notification = this.repository.findById(notiId).orElse(null);

        if (notification == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }

        return notification;
    }

    @Async
    @Transactional
    public Notification create(User user, Notification notification) {
        notification.setUser(user);
        return this.repository.save(notification);
    }

    @Async
    @Transactional
    public void notifyFollowersAboutPostCreated(Post post) {
        User actor = post.getUser();

        List<Followers> followers = this.followersRepository.findAllByFollowed(actor);

        for (Followers f : followers) {
            Notification notification = new Notification();
            notification.setUser(f.getFollower());
            notification.setTitle("New post from " + actor.getName());
            notification.setMessage("Check out the new post: " + post.getTitle());
            notification.setStatus(StatusNotification.POST_CREATED);
            notification.setPostId(post.getId());

            this.repository.save(notification);
        }
    }

    @Async
    @Transactional(readOnly = true)
    public Page<Notification> getAllOfUser(User user, Pageable pageable, Specification<Notification> spec) {
        return this.repository.findAllByUser(user, pageable, spec);
    }

    @Async
    @Transactional
    public void markHowRead(Notification notification) {
        notification.setRead(true);

        this.repository.save(notification);
    }

}
