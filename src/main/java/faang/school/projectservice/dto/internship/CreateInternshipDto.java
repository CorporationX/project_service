package faang.school.projectservice.dto.internship;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateInternshipDto {

    @Length(max = 128, message = "The name cannot be longer than 128 characters")
    @NotBlank(message = "Name cannot be blank")
    private final String name;

    @Length(max = 500, message = "The description cannot be longer than 500 characters")
    @NotBlank(message = "Description cannot be blank")
    private final String description;

    @NotNull(message = "The internship cannot be outside the project")
    @Positive(message = "Internship cannot be negative")
    private final Long projectId;

    @NotNull(message = "Interns list is required")
    @Size(min = 1, message = "At least one intern is required")
    private final List<Long> internIds;

    @NotNull(message = "Mentor ID cannot be blank")
    private final Long mentorId;

    private final LocalDateTime startDate = LocalDateTime.now();

    @Future(message = "End date must be in the future")
    private final LocalDateTime endDate;
}
