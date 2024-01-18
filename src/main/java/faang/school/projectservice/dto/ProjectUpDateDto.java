package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectUpDateDto {
    private String description;
    private ProjectStatus status;
}
