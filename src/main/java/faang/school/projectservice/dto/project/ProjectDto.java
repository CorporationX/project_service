package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@FieldNameConstants
@Builder
public record ProjectDto(
        Long id,
        String name,
        String description,
        BigInteger storageSize,
        BigInteger maxStorageSize,
        Long parentProjectId,
        List<Long> childrenIds,
        List<Long> tasksIds,
        LocalDateTime createdAt,
        ProjectStatus status,
        List<Long> teamsIds,
        List<Long> stagesIds,
        List<Long> meetsIds
) {
}