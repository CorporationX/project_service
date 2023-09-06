package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    @Min(0)
    private Long id;
    @NotBlank(message = "The name is not valid")
    @Size(min = 3, max = 50, message = "The name must be between 3 and 50 characters")
    private String name;
    @NotBlank(message = "The description is not valid")
    @Size(min = 3, max = 100, message = "The description must be between 3 and 100 characters")
    private String description;
    @Min(0)
    @NotEmpty(message = "The ownerId is not valid")
    private Long ownerId;
    @NotBlank(message = "The status is not valid")
    private String status;
    @NotBlank(message = "The visibility is not valid")
    private String visibility;
}
