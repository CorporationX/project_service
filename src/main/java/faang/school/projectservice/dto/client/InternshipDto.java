package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NotNull(message = "There is not internship!")
public class InternshipDto {
    @NotNull(message = "Internship name can not be null!")
    @NotBlank(message = "Internship name can not be blank!")
    private String name;
    private Long id;
    @NotNull(message = "There is not mentor for internship!")
    @Min(1)
    private Long mentorId;
    @NotNull(message = "There is not project for create internship!")
    @Min(1)
    private Long projectId;
    @NotNull(message = "There is not interns for internship!")
    @NotEmpty(message = "There is not interns for internship!")
    private List<Long> internsId;
    @NotNull(message = "Internship is over!")
    private InternshipStatus internshipStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}