package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.meet.MeetStatus;
import faang.school.projectservice.validator.meet.CreateValidation;
import faang.school.projectservice.validator.meet.UpdateValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MeetDto {

    @NotNull(message = "Meet id can't be empty", groups = {UpdateValidation.class})
    private Long id;

    @NotBlank(message = "Meet title can't be empty", groups = {CreateValidation.class, UpdateValidation.class})
    @Size(min = 10, max = 256)
    private String title;

    @NotBlank(message = "Meet status can't be empty", groups = {CreateValidation.class, UpdateValidation.class})
    @Size(min = 10, max = 4096)
    private String description;

    @NotNull(message = "Meet status can't be empty", groups = {UpdateValidation.class})
    private MeetStatus status;

    private Long createdBy;

    @NotBlank(message = "Team id for meet can't be empty", groups = {CreateValidation.class})
    private Long teamId;

    @NotBlank(message = "Project id for meet can't be empty", groups = {CreateValidation.class})
    private Long projectId;

    @NotNull(message = "Start time can't be empty", groups = {CreateValidation.class, UpdateValidation.class})
    private LocalDateTime startDate;

    @NotNull(message = "End time can't be empty", groups = {CreateValidation.class, UpdateValidation.class})
    private LocalDateTime endDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
