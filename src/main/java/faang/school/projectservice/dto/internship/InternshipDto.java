package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
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

    @NotNull(message = "Project ID cannot be null")
    private Long projectId;

    @NotEmpty(message = "Intern user IDs list cannot be empty")
    private List<Long> internUserIds;

    @NotNull(message = "Mentor user ID cannot be null")
    private Long mentorUserId;

    @NotNull(message = "Start date cannot be null")
    private LocalDateTime startDate;

    @NotNull(message = "End date cannot be null")
    private LocalDateTime endDate;

    @NotNull(message = "Status cannot be null")
    private InternshipStatus status;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    private TeamRole newTeamRole;

    @NotNull(message = "Creator user ID cannot be null")
    private Long creatorUserId;
}