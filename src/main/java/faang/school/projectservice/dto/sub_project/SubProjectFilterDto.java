package faang.school.projectservice.dto.sub_project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.annotation.Nullable;

/**
 * SubProjectFilterDto — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 21.07.2025
 */
public record SubProjectFilterDto(
        @Nullable
        String name,
        @Nullable
        ProjectStatus status
) {
}