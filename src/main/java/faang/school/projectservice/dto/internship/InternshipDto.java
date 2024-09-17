package faang.school.projectservice.dto.internship;

import jakarta.validation.constraints.NotNull;
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
@NotNull
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
