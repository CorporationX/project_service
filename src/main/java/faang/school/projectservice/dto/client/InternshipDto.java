package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InternshipDto {
    private Internship internship;
    private List<TeamMember> interns;
    private TaskStatus taskStatus;
}
