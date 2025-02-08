package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

/**
 * DTO for {@link faang.school.projectservice.model.Project}
 */
@Builder
public record CreateProjectRequest(@NotBlank(message = "Название проекта не может быть пустым")
                                   String name,
                                   @NotBlank(message = "Описание проекта не может быть пустым")
                                   String description,
                                   @NotNull(message = "Владелец проекта обязателен")
                                   @Positive(message = "Id пользователя должно быть положительным")
                                   Long ownerId,
                                   ProjectVisibility visibility,
                                   List<Long> teamIds) {
}