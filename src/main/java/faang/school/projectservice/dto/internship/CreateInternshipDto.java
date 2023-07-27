package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CreateInternshipDto {
    @NotNull
    private Long projectId;
    @NotNull
    private Long mentorId;
    @NotEmpty
    @NotNull
    private List<Long> internIds;
    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;
    @NotBlank
    private String name;
    @NotNull
    private TeamRole internshipRole;
    private Long createdBy;
}
