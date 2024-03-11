package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternshipDto {
    private Long id;
    private Long projectId;
    private Long mentorId;
    private List<Long> internsId;
    private Long createdBy;
    private String description;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
