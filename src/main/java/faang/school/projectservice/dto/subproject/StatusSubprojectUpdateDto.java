package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusSubprojectUpdateDto {
    private Long id;
    private ProjectStatus status;
}
