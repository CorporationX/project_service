package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class InternshipCreatedDto {
    private Long projectId;
    private TeamMember mentorId;

    @NotNull(message = "Start date can not be null")
    @Builder.Default
    private LocalDateTime startDate = LocalDateTime.now();

    @NotNull(message = "End date can not be null")
    private LocalDateTime endDate;

    @NotNull(message = "Internship status can not be null")
    private InternshipStatus status;

    @NotBlank(message = "Internship description can not be blank")
    @Size(max = 255, message = "Internship description must not exceed 255 characters")
    private String description;

    @NotBlank(message = "Internship name can not be blank")
    @Size(max = 255, message = "Internship name must not exceed 255 characters")
    private String name;

    @Positive(message = "Internship duration can not be negative")
    @NotNull(message = "Internship duration can not be null")
    private Long createdBy;
}
