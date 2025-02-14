package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InternshipCreateDto {
    @Positive
    @NotEmpty
    private Long projectId;

    @Positive
    @NotEmpty
    private Long mentorId;

    @NotEmpty
    private TeamRole role;

    @NotEmpty
    private List<@Positive Long> internsIds;

    @NotEmpty
    private LocalDateTime startDate;

    @NotEmpty
    private LocalDateTime endDate;

    @NotEmpty
    private InternshipStatus status;
}
