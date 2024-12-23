package faang.school.projectservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(name = "Project")
@AllArgsConstructor
public class ProjectDto {
    @Schema(name = "Id")
    private Long id;
    @Schema(name = "Name")
    @NotBlank
    private String name;
    @Schema(name = "Description")
    @NotBlank
    private String description;
    @NotNull
    @Schema(name = "Owner Id")
    private Long ownerId;
    @Schema(name = "Status")
    private ProjectStatus status;
    @Schema(name = "Visibility")
    private ProjectVisibility visibility;
    @JsonProperty("parentProjectId")
    private Long parentProjectId;
    private List<Long> childrenIds;
}