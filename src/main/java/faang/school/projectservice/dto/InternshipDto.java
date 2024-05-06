package faang.school.projectservice.dto;

import faang.school.projectservice.model.InternshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipDto {

    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private Long mentorId;
    private List<Long> internIds;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long createdBy;
    private InternshipStatus status;
    private List<Long> candidateIds;
}
