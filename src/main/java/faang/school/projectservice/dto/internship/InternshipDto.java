package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InternshipDto {

    private Long id;
    @NotNull
    private Long projectId;
    @NotEmpty
    private List<Long> internUserIds;
    @NotNull
    private Long mentorUserId;
    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;
    @NotNull
    private InternshipStatus status;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private TeamRole newTeamRole;
    @NotNull
    private Long creatorUserId;
}
