package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubProjectDto {
    private Long id;

    @NotBlank(message = "Name cannot be empty!")
    @Size(min = 1, max = 4096, message = "Name size should be between 1 and 4096!")
    private String name;

    @NotBlank(message = "Description cannot be empty!")
    @Size(min = 1, max = 4096, message = "Description size should be between 1 and 4096!")
    private String description;

    @NotBlank(message = "Subproject can not have parent project!")
    @Min(value = 1L, message = "Project id can not be negative or zero!")
    private Long parentId;
    private List<Long> childrenId;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}
