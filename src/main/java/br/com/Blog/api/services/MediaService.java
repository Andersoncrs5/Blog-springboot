package br.com.Blog.api.services;

import br.com.Blog.api.DTOs.UpdateMediaDTO;
import br.com.Blog.api.entities.Media;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.repositories.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository repository;

    @Transactional(readOnly = true)
    public Media get(Long id) {
        if (id <= 0) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required"); }

        return this.repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found"));
    }

    @Transactional
    public void delete(Media media) {
        this.repository.delete(media);
    }

    @Transactional
    public Media update(Media media, UpdateMediaDTO mediaToUpdate) {
        if (media.getId() <= 0) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Media id is required");}

        media.setOrder(mediaToUpdate.order());

        return this.repository.save(media);
    }

    @Transactional
    public Media create(Media media, Post post) {
        if (post.getId() <= 0) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post is required"); }

        List<Media> medias = this.repository.findAllByPost(post);

        if (medias.size() >= 10){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Media limit is 10 per post");
        }

        media.setPost(post);
        media.setId(null);

        return this.repository.save(media);
    }

    @Transactional(readOnly = true)
    public List<Media> getAllMidiasByPost(Post post) {
        if (post.getId() <= 0) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post is required"); }
        return this.repository.findAllByPost(post);
    }

}
