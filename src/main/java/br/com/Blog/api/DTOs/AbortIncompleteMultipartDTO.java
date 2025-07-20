package br.com.Blog.api.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbortIncompleteMultipartDTO extends LifeCyclePolicyDTO {
    private int days;
}
