package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InternshipDto {
    private Long id;
    private Long projectId;
    private Long mentorId;
    private List<InternshipUserInformationDto> interns;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private InternshipStatus status;
    private String description;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private Long scheduleId;
}
