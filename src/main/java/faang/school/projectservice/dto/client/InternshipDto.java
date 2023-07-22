package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class InternshipDto {
    private Project project;
    private TeamMember mentorId;
    private List<TeamMember> interns;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
