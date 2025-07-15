package br.com.Blog.api.DTOs;

import br.com.Blog.api.entities.enums.FontTypeEnum;
import br.com.Blog.api.entities.enums.LayoutPreferenceEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserConfigDTO(
        @NotBlank
        String ThemeName,

        @NotBlank
        String PrimaryColor,

        @NotBlank
        String SecondaryColor,

        @NotBlank
        String AccentColor,

        @NotNull
        FontTypeEnum FontType,

        @NotNull
        Integer FontSize,

        @NotNull
        Double LineHeight,

        @NotNull
        Double LetterSpacing,

        @NotBlank
        String BorderColor,

        @NotNull
        Integer BorderSize,

        @NotNull
        Integer BorderRadius,

        @NotNull
        LayoutPreferenceEnum LayoutPreference,

        @NotNull
        Boolean ShowProfilePictureInComments ,

        @NotNull
        Boolean EnableAnimations ,

        @NotNull
        Boolean NotificationsEnabled ,

        @NotBlank
        String TimeZone
) {
}
