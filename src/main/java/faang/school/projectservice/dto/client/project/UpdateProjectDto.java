package faang.school.projectservice.dto.client.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;

import java.math.BigInteger;
import java.util.List;

public record UpdateProjectDto(
        @NotNull
        String name,
        @NotNull
        String description,
        @NotNull
        BigInteger storageSize,
        @NotNull
        BigInteger maxStorageSize,
        @NotNull
        Long ownerId,
        @NotNull
        Project parentProject,
        @NotNull
        ProjectVisibility visibility,
        @NotNull
        ProjectStatus status,
        @NotNull
        String coverImageId,
        @NotNull
        List<String> galleryFileKeys
) {
}
