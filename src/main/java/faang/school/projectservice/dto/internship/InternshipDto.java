package faang.school.projectservice.dto.internship;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InternshipDto {
    private Long id;
    private ProjectDto project;
    private TeamMemberDto mentorId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String name;
    private InternshipStatus status;
    private List<TeamMemberDto> interns;
}
