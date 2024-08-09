package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectCoverDto {
    private Long id;
    @NotBlank(message = "Name should not be empty.")
    private String name;
    private String coverImageId;
}
