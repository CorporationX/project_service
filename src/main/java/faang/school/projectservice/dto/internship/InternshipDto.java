package faang.school.projectservice.dto.internship;

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

    private long projectId;
    private long mentorId;
    private List<Long> internsId;
    private String description;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private long scheduleId;
    private LocalDateTime createdAt;
    private long createdBy;
    private LocalDateTime updatedAt;
    private long updatedBy;

}
