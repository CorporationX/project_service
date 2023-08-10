package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.InternshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternshipUpdateDto {

    private Long projectId;
    private Long mentorId;
    private LocalDateTime endDate;
    private InternshipStatus status;
    private String description;
    private String name;
    private LocalDateTime updatedAt;
    private Long updatedBy;
    private Long scheduleId;
}
