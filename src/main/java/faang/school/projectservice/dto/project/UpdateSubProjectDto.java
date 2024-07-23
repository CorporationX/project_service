package faang.school.projectservice.dto.project;


import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSubProjectDto {
    private ProjectStatus status;
    private ProjectVisibility visibility;
}
