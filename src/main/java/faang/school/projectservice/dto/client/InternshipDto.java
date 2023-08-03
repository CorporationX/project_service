package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.InternshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternshipDto {
    private String name;
    private Long id;
    private Long mentorId;
    private Long projectId;
    private List<Long> internsId;
    private InternshipStatus internshipStatus = InternshipStatus.IN_PROGRESS;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
