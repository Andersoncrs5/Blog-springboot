package br.com.Blog.api.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveToStandardIAAndExpireCurrentVersionsDTO extends LifeCyclePolicyDTO {
    private int days;
    private int expDays;
}
