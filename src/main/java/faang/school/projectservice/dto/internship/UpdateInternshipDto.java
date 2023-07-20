package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class UpdateInternshipDto {
    private Long id;
    private Long mentorId;
    private InternshipStatus status;
    private String name;
    private LocalDateTime updatedAt;
    private Long updatedBy;
}
