package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectDto {
    @Positive
    @NotNull
    private Long id;
    private String coverImageId;
}
