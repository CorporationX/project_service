package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProjectDto {
    @Positive
    @NotNull
    private Long id;
    private String coverImageId;
}
