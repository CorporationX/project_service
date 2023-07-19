package faang.school.projectservice.dto.moment;

import faang.school.projectservice.dto.project.ProjectDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class MomentDto {
    @NotNull
    private Long id;
    @NotBlank
    @Size(max = 128)
    private String name;
    @Size(max = 4096)
    private String description;
    @NotNull
    private LocalDateTime createdAt;
    @NotNull
    @Size(min = 1)
    private List<ProjectDto> projects;
}
