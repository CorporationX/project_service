package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InternshipDto {
    private String name;
    private Long id;
    private Long mentorId;
    private Long projectId;
    private List<Long> internsId;
    private TaskStatus taskStatus;
    private InternshipStatus internshipStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
