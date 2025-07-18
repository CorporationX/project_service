package faang.school.projectservice.dto.client.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

import java.math.BigInteger;
import java.util.List;

public record UpdateProjectDto(
        String name,
        String description,
        BigInteger storageSize,
        BigInteger maxStorageSize,
        Long ownerId,
        Project parentProject,
        ProjectVisibility visibility,
        ProjectStatus status,
        String coverImageId,
        List<String> galleryFileKeys
) {
}
