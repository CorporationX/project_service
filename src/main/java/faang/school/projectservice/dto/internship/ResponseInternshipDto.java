package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ResponseInternshipDto {
    private Long id;
    private Long projectId;
    private Long mentorId;
    private List<Long> internIds;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private InternshipStatus status;
    private TeamRole internshipRole;
    private String description;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private Long scheduleId;
}
