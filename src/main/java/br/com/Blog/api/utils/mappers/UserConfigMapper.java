package br.com.Blog.api.utils.mappers;

import br.com.Blog.api.DTOs.UserConfigDTO;
import br.com.Blog.api.entities.UserConfig;
import com.github.dozermapper.core.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserConfigMapper {

    private final Mapper mapper;

    public UserConfigDTO toDTO(UserConfig config) {
        return mapper.map(config, UserConfigDTO.class);
    }

    public UserConfig toUserConfig(UserConfigDTO dto) {
        return mapper.map(dto, UserConfig.class);
    }

}
