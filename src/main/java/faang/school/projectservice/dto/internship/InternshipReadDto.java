package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InternshipReadDto {
    private Long id;
    private Long projectId;
    private Long mentorId;
    private TeamRole role;
    private List<Long> internsIds;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private InternshipStatus status;
}
