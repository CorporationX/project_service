package faang.school.projectservice.dto.initiative;

import faang.school.projectservice.model.initiative.InitiativeStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitiativeFilterDto {

    @NotNull(message = "status should not be null")
    private InitiativeStatus status;

    @Positive(message = "curatorId should be positive")
    @NotNull(message = "curatorId should not be null")
    private Long curatorId;
}
