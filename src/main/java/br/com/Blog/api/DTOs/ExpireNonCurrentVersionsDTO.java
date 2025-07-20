package br.com.Blog.api.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpireNonCurrentVersionsDTO extends LifeCyclePolicyDTO {
    private int noncurrentDays;
}
