package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CreateInternshipDto {
    private Long projectId;
    private Long mentorId;
    @NotNull
    @NotEmpty
    private List<Long> internIds;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @NotBlank
    private String name;
    private TeamRole internshipRole;
    private Long createdBy;
}

