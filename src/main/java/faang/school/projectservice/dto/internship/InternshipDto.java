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
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternshipDto {

    private long projectId;
    private long mentorId;
    private List<Long> internsId = new ArrayList<>();
    @NotNull(message = "description cannot be empty")
    private String description;
    @NotNull(message = "name cannot be empty")
    private String name;
    @NotNull(message = "start date cannot be empty")
    private LocalDateTime startDate;
    @NotNull(message = "end date cannot be empty")
    private LocalDateTime endDate;
    @NotNull(message = "status cannot be empty")
    private String status;
    private long scheduleId;
}
