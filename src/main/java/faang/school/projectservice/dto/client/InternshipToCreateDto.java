package faang.school.projectservice.dto.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.annotation.DurationOfInternship;
import faang.school.projectservice.annotation.FutureAfterStart;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@FutureAfterStart
@DurationOfInternship
public class InternshipToCreateDto {
    @Positive(message = "projectId cannot be negative")
    @NotNull(message = "projectId cannot be null")
    private Long projectId;
    @Positive(message = "mentorId cannot be negative")
    @NotNull(message = "mentorId cannot be null")
    private Long mentorId;
    @NotEmpty(message = "list interns for creation cannot be null")
    private List<@Positive(message = "internId cannot be negative")
    @NotNull(message = "internId cannot be null") Long> internsId;

    @NotNull(message = "startDate cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @NotNull(message = "endDate cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    @NotBlank(message = "description cannot be null")
    private String description;
    @NotBlank(message = "name cannot be null")
    private String name;
}
