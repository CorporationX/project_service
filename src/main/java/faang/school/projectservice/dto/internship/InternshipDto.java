package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipDto {
    private Long id;

    @NotBlank(message = "The internship must have a name.")
    private String name;

    private String description;

    @NotNull(message = "The internship must be referred to a specific project.")
    private Long projectId;

    @NotNull(message = "The internship must have a mentor.")
    private Long mentorId;

    @NotEmpty(message = "The internship can only be created with interns.")
    private List<@NotNull(message = "Cannot process null-valued intern id.") Long> internsIds;

    private LocalDateTime startDate;

    @Future(message = "The internship cannot be created in the past.")
    private LocalDateTime endDate;

    @NotNull(message = "The internship must have a status.")
    private InternshipStatus status;
}
