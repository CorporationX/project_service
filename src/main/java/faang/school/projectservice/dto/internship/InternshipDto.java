package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InternshipDto {
    private Long id;
    @NotNull
    private Long projectId;
    @NotNull
    private Long mentorId;
    @NotNull
    private List<Long> interns;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private InternshipStatus status;
    private String description;
    private String name;
    private Long schedule;
}
