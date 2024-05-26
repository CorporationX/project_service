package faang.school.projectservice.dto.initiative;

import faang.school.projectservice.model.initiative.InitiativeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitiativeDto {

    @Positive(message = "Id should be positive")
    @NotNull(message = "Id should not be null")
    private Long id;

    @NotBlank(message = "Name should not be blank")
    private String name;

    @NotBlank(message = "Description should not be blank")
    private String description;

    @Positive(message = "CuratorId should be positive")
    @NotNull(message = "CuratorId should not be null")
    private Long curatorId;

    @Positive(message = "ProjectId should be positive")
    @NotNull(message = "ProjectId should not be null")
    private Long projectId;

    @NotNull(message = "Status should not be null")
    private InitiativeStatus status;

    private List<Long> stageIds;
}
