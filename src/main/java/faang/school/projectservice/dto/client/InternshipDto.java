package faang.school.projectservice.dto.client;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternshipDto {
    private String name;
    private Long id;
    private Long projectId;
    private Long mentorId;
    private List<Long> interns;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
