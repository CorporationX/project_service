package faang.school.projectservice.dto.project;


import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    @NotNull
    private Long id;
    @NotBlank
    @Size(max = 128, message = "Project's name length can't be more than 128 symbols")
    private String name;
    @NotBlank
    @Size(max = 4096, message = "Project's description length can't be more than 4096 symbols")
    private String description;
    private Long parentId;
    private List<Long> childrenId;
    private List<StageDto> stages;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}