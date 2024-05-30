package faang.school.projectservice.dto.internship;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class InternshipToCreateDto {

    private long projectId;
    private long mentorId;
    @NotEmpty(message = "Interns ID list cannot be empty")
    private List<Long> internsId;
    @NotNull(message = "Description cannot be null")
    private String description;
    @NotNull(message = "Name cannot be null")
    private String name;
    @NotNull(message = "Start date cannot be null")
    private LocalDateTime startDate;
    @NotNull(message = "End date cannot be null")
    private LocalDateTime endDate;
    private Long scheduleId;
}
