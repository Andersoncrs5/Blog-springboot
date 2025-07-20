package br.com.Blog.api.utils.mappers;

import br.com.Blog.api.DTOs.MediaDTO;
import br.com.Blog.api.entities.Media;
import com.github.dozermapper.core.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaMapper {
    private final Mapper mapper;

    public MediaDTO toDTO(Media media) {
        return mapper.map(media, MediaDTO.class);
    }

    public Media toMedia(MediaDTO mediaDTO) {
        return mapper.map(mediaDTO, Media.class);
    }

}