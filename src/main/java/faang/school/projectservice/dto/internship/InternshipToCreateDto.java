package faang.school.projectservice.dto.internship;

import jakarta.validation.constraints.NotBlank;
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
public class InternshipToCreateDto {

    @NotNull(message = "ProjectId should not be null")
    @Positive(message = "ProjectId should be positive")
    private long projectId;

    @NotNull(message = "MentorId should not be null")
    @Positive(message = "MentorId should be positive")
    private long mentorId;

    @NotNull(message = "InternsIds should not be null")
    @NotEmpty(message = "InternsIds should not be empty")
    private List<Long> internsId;

    @NotNull(message = "Description should not be null")
    @NotBlank(message = "Description should not be blank")
    private String description;

    @NotNull(message = "Name should not be null")
    @NotBlank(message = "Name should not be blank")
    private String name;

    @NotNull(message = "Start should not be null")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull(message = "ScheduleId should not be null")
    private Long scheduleId;
}
