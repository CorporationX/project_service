package faang.school.projectservice.dto.intership;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipDto {
    private Long id;
    private Long projectId;
    private TeamMemberDto mentor;
    private List<TeamMember> interns;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private InternshipStatus status;
    private Long createdBy;
    private String name;
    private String description;
}
