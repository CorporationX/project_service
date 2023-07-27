package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.Project;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProjectDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Long ownerId;
    private Long parentProjectId;
    private List<Long> childrenIds;
}
