package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
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
    private Long mentorId;
    private List<Long> internIds;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private InternshipStatus status;
    private String description;
    private String name;
    private Long createdBy;
    private Long updatedBy;
    private Long scheduleId;
}
