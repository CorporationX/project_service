package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InternshipCreateDto {
    @NotNull
    @NotBlank
    private String name;
    @Min(1)
    private long projectId;
    @Min(1)
    private long mentorId;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    @NotEmpty
    private List<Long> internsId;
    @NotNull
    @FutureOrPresent
    private LocalDateTime startDate;
    @NotNull
    @FutureOrPresent
    private LocalDateTime endDate;
    @NotNull
    private InternshipStatus status;
    @NotNull
    private TeamRole role;
}
