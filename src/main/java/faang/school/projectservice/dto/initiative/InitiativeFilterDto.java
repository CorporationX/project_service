package faang.school.projectservice.dto.initiative;

import faang.school.projectservice.model.initiative.InitiativeStatus;
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

    @Positive(message = "curatorId should be positive")
    private Long curatorId;

    private InitiativeStatus status;
}
