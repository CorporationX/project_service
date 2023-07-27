package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProjectDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Long ownerId;
    private Long parentProjectId;
    private List<Long> childrenIds;
}
