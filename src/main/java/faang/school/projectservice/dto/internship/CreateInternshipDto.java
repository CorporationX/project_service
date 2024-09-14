package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.TeamMember;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInternshipDto {

    @Length(max = 128, message = "The name cannot be longer than 128 characters")
    @NotBlank(message = "Name cannot be blank")
    private String internshipName;

    @Length(max = 500, message = "The description cannot be longer than 500 characters")
    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "The internship cannot be outside the project")
    @Positive(message = "Internship cannot be negative")
    private Long projectId;

    @NotNull(message = "Interns list is required")
    @Size(min = 1, message = "At least one intern is required")
    private List<Long> internIds;

    @NotNull(message = "Mentor ID cannot be blank")
    private Long mentorId;

    private final LocalDateTime startDate = LocalDateTime.now();

    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;
}
