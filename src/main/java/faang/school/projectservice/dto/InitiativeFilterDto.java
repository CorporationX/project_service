package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.initiative.InitiativeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class InitiativeFilterDto {
    private InitiativeStatus statusPattern;
    private Long curatorPattern;
}