package faang.school.projectservice.dto.subproject;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateSubProjectDto(
        @NotBlank(message = "Нельзя создать подпроект с пустым полем name.")
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @Min(1)
        @JsonProperty("parentProjectId") Long parentProjectId,
        @JsonProperty("visibility") ProjectVisibility visibility,
        @JsonProperty("coverImageId") String coverImageId,
        @JsonProperty("teamIds") List<Long> teamIds,
        @JsonProperty("scheduleId") Long scheduleId,
        @JsonProperty("stageIds") List<Long> stageIds,
        @JsonProperty("vacancyIds") List<Long> vacancyIds,
        @JsonProperty("meetIds") List<Long> meetIds,
        @JsonProperty("presentationFileKey") String presentationFileKey,
        @JsonProperty("galleryFileKeys") List<String> galleryFileKeys
) {
}
