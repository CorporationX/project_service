package faang.school.projectservice.dto.sub_project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SubProjectViewDto — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 21.07.2025
 */
public record SubProjectViewDto(
    Long id,
    String name,
    String description,
    ProjectVisibility visibility,
    ProjectStatus status,
    Long parentId,
    List<Long> childrenId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}