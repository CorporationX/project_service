package faang.school.projectservice.dto.sub_project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.NonNull;

/**
 * SubProjectUpdateDto — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 21.07.2025
 */
public record SubProjectUpdateDto(
    @NonNull
    ProjectStatus status,
    @NonNull
    ProjectVisibility visibility
) {
}