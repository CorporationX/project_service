package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.validation.ValidationConstants;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@With
public record CreateInternshipDto(
        @NotNull(message = "Project id should be present")
        Long projectId,

        @NotNull(message = "Mentor id should be present")
        Long mentorId,

        @NotNull(message = "Role should be present")
        TeamRole role,

        @NotEmpty(message = "Interns ids list cant be empty")
        List<Long> internsIds,

        @NotNull(message = "Start date must not be null")
        @Future(message = "Start date must be in future")
        LocalDateTime startDate,

        @NotNull(message = "End date must not be null")
        @Future(message = "End date must be in future")
        LocalDateTime endDate,

        @NotBlank(message = "Description must not be blank")
        @Size(max = ValidationConstants.DESCRIPTION_MAX_LENGTH, message = ValidationConstants.DESCRIPTION_SIZE_MESSAGE)
        String description,

        @NotBlank(message = "Name must not be blank")
        @Size(max = ValidationConstants.NAME_MAX_LENGTH, message = ValidationConstants.NAME_SIZE_MESSAGE)
        String name
) {
    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateAfterStartDate() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return endDate.isAfter(startDate);
    }
}