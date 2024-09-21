package faang.school.projectservice.dto.internship;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternshipDto {
    private Long id;
    private ProjectDto project;
    private TeamMemberDto mentorId;
    private List<TeamMemberDto> interns;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
