package faang.school.projectservice.dto.internship;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipDto {
    private Long id;
    @NotNull(message = "Project ID cannot be null")
    private Long projectId;
    @NotNull(message = "Mentor ID cannot be null")
    private Long mentorId;
    @NotEmpty(message = "Interns ID list cannot be empty")
    private List<Long> internsId;
    @NotNull(message = "Start date cannot be null")
    private LocalDateTime startDate;
    @NotNull(message = "End date cannot be null")
    private LocalDateTime endDate;
    @NotEmpty(message = "Status cannot be empty")
    private String status;
    @NotEmpty(message = "Description cannot be empty")
    private String description;
    @NotEmpty(message = "Name cannot be empty")
    private String name;
}
