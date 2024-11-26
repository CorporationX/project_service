package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class InternshipFilterDto {
    @NotNull(message = "Internship status can not be null")
    private InternshipStatus internshipStatus;

    private TeamRole teamRole;

    @NotNull(message = "Created by can not be null")
    private Long createdBy;

    @NotBlank(message = "Description should not be blank")
    @Size(max = 255, message = "Description should not exceed 255 characters")
    private String description;
}
