package faang.school.projectservice.dto.project;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectCoverImageDto {
    private Long projectId;
    private String fileId;
}

