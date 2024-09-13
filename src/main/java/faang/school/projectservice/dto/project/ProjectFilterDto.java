package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.TeamMember;
import lombok.Data;

@Data
public class ProjectFilterDto {
    private String projectName;
    private String projectStatus;
    private TeamMember teamMember;
}
