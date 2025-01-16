package faang.school.projectservice.dto.client.internship;

import faang.school.projectservice.model.InternshipStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InternshipFilterDto {
    private Long projectId;
    private Long mentorId;
    private Long internId;
    private LocalDateTime date;
    private InternshipStatus status;
    private String description;
    private String name;
}
