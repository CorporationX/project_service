package faang.school.projectservice.dto.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SubProjectDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @Size(max = 4096, message = "SubProject name length can't be more than 128 symbols")
    private String name;
    @Size(max = 4096, message = "Project's description length can't be more than 4096 symbols")
    private String description;
    @Min(value = 1, message = "Owner id cant be less then 1")
    private long ownerId;
    @Min(value = 1, message = "Parent project id cant be less then 1")
    private Long parentProjectId;
    private List<Long> childrenIds;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private List<Long> stagesId;
}