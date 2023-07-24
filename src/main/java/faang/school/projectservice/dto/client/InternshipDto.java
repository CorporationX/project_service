package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class InternshipDto {
    private Project projectId;
    private TeamMember mentorId;
    private List<TeamMember> interns;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
