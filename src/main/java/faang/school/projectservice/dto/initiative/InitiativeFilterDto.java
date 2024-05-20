package faang.school.projectservice.dto.initiative;

import faang.school.projectservice.model.initiative.InitiativeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitiativeFilterDto {
    private InitiativeStatus status;
    private Long curatorId;
}
