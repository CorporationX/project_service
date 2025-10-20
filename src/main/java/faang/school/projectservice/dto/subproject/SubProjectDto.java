package faang.school.projectservice.dto.subproject;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.List;

public record SubProjectDto(
        @Min(1)
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("ownerId") Long ownerId,
        @JsonProperty("parentProjectId") Long parentProjectId,
        @JsonProperty("childrenIds") List<Long> childrenIds,
        @JsonProperty("tasksIds") List<Long> taskIds,
        @JsonProperty("resourceIds") List<String> resourceIds,
        @JsonProperty("createdAt") LocalDateTime createdAt,
        @JsonProperty("updatedAt") LocalDateTime updatedAt,
        @JsonProperty("status") ProjectStatus status,
        @JsonProperty("visibility") ProjectVisibility visibility,
        @JsonProperty("coverImageId") String coverImageId,
        @JsonProperty("teamIds") List<Long> teamIds,
        @JsonProperty("scheduleId") Long scheduleId,
        @JsonProperty("stageIds") List<Long> stages,
        @JsonProperty("vacancieIds") List<Long> vacancieIds,
        @JsonProperty("momentIds") List<Long> momentIds,
        @JsonProperty("meetIds") List<Long> meetIds,
        @JsonProperty("presentationFileKey") String presentationFileKey,
        @JsonProperty("presentationGeneratedAt") LocalDateTime presentationGeneratedAt,
        @JsonProperty("galleryFileKeys") List<String> galleryFileKeys
) {
}
