package faang.school.projectservice.dto.project;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectCoverDto {
    private Long projectId;
    private Long coverId;
}
