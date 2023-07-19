package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor

public class InternshipDto {

    private Long id;
    private String projectName;
    private Long mentorId;
    private List<Long> internsId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    //private InternshipStatus status;
    //на что менять статус enum
    private String description;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private Schedule schedule;
}
