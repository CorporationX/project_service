package faang.school.projectservice.model.dto;

import faang.school.projectservice.model.enums.InternshipStatus;
import faang.school.projectservice.model.enums.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Project ID must not be null")
    private Long projectId;

    @NotEmpty(message = "Intern user IDs must not be empty")
    private List<Long> internUserIds;

    @NotNull(message = "Mentor user ID must not be null")
    private Long mentorUserId;

    @NotNull(message = "Start date must not be null")
    private LocalDateTime startDate;

    @NotNull(message = "End date must not be null")
    private LocalDateTime endDate;

    @NotNull(message = "Status must not be null")
    private InternshipStatus status;

    @NotBlank(message = "Name must not be blank")
    @Size(max = 255, message = "Name must be less than or equal to 255 characters")
    private String name;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 255, message = "Description must be less than or equal to 255 characters")
    private String description;

    private TeamRole newTeamRole;

    @NotNull(message = "Creator user ID must not be null")
    private Long creatorUserId;
}