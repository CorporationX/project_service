package faang.school.projectservice.dto.client;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipDto {

    private Long id;
    private Long projectId;
    private Long mentorId;
    private List<Long> internIds;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private String description;
    private String name;

}
