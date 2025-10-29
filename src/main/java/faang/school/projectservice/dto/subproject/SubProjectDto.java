package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public record SubProjectDto(
        Long id,
        String name,
        String description,
        BigInteger storageSize,
        BigInteger maxStorageSize,
        Long ownerId,
        Long parentProjectId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        ProjectStatus status,
        ProjectVisibility visibility,
        String coverImageId,
        String presentationFileKey,
        LocalDateTime presentationGeneratedAt,
        List<String> galleryFileKeys
) {
}
