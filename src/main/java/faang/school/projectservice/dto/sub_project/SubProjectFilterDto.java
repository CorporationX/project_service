package faang.school.projectservice.dto.sub_project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.annotation.Nullable;

/**
 * Класс с параметрами фильтрации подпроектов
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