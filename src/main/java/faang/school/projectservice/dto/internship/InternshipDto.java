package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotNull(message = "id cannot be empty")
    @Positive(message = "id should be positive number")
    private Long id;
    @NotEmpty(message = "Team should have at least one intern")
    private List<TeamMember> interns;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @NotNull(message = "status cannot be empty")
    private InternshipStatus status;
}
