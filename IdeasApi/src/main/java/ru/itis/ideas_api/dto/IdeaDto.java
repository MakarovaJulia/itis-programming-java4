package ru.itis.ideas_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdeaDto {
    private Long id;
    @NotBlank(message = "BLANK_IDEA_NAME")
    private String name;
    @NotBlank(message = "BLANK_IDEA_DESCRIPTION")
    private String description;
    private Integer likesCount;
    private UserDto author;
    @NotNull(message = "USER_ID_EMPTY")
    private Long authorId;
    private List<CommentDto> comments;
}
