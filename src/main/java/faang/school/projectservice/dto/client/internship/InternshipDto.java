package faang.school.projectservice.dto.client.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InternshipDto {
    private Long id;
    private Long projectId;
    private Long mentorId;
    private TeamRole role;
    private List<Long> internsId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private InternshipStatus status;
    private String description;
    private String name;
    private Long createdBy;
    private Long updatedBy;
    private Long scheduleId;
}
