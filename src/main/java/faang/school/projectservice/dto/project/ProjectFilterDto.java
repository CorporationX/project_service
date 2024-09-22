package faang.school.projectservice.dto.project;

import lombok.Data;

@Data
public class ProjectFilterDto {

    private String projectName;
    private String projectStatus;
    private long teamMemberId;
}
