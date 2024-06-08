package faang.school.projectservice.dto.project;


import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDtoRequest {
    @Min(value = 1, message = "Id does not match the required value - must be a number greater than 0")
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Long ownerId;
    private ProjectVisibility visibility;
}
