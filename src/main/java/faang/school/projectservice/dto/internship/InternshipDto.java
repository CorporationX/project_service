package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipDto {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private InternshipStatus status;
}
