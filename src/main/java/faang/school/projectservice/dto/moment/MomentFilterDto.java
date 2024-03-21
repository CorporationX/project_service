package faang.school.projectservice.dto.moment;

import faang.school.projectservice.model.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentFilterDto {
    @NotBlank
    @Pattern(regexp = "^\\d{4}/\\d{2}/\\d{2}$")
    private LocalDateTime date;
    @NotEmpty
    private List<Project> projects;
}
