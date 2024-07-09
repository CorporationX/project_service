package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import lombok.Data;

import java.util.List;

@Data
public class InternshipDto {

    private long id;
    private Project project;
    private TeamMember mentorId;
    private List<TeamMember> interns;
    private InternshipStatus status;
}
