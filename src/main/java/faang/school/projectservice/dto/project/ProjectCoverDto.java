package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectCoverDto {
    @Positive
    @NotNull
    private Long id;
    private String coverImageId;
}
