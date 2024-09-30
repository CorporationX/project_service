package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSubProjectDto(Long id,
                                  @NotBlank String name,
                                  String description,
                                  @NotNull Long parentId) {
}