package faang.school.projectservice.dto.sub_project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.time.LocalDateTime;

/**
 * SubProjectCreateDto — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 21.07.2025
 */
public record SubProjectCreateDto(
        @NonNull
        Long parentId,
        @NotBlank
        @Size(max = 255)
        String name,
        @NotBlank
        @Size(max = 512)
        String description,
        @NonNull
        ProjectVisibility visibility,
        LocalDateTime createdAt
) {

}