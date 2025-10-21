package faang.school.projectservice.dto.subproject;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;

import java.util.List;

public record SubProjectDto(
        @Min(1)
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("ownerId") Long ownerId,
        @JsonProperty("status") ProjectStatus status,
        @JsonProperty("visibility") ProjectVisibility visibility,
        @JsonProperty("coverImageId") String coverImageId,
        @JsonProperty("presentationFileKey") String presentationFileKey,
        @JsonProperty("galleryFileKeys") List<String> galleryFileKeys
) {
}
