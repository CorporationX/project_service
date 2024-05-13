package faang.school.projectservice.dto.initiative;

import faang.school.projectservice.model.initiative.InitiativeStatus;
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
    private Long id;
    private String name;
    private String description;
    private Long curatorId;
    private Long projectId;
    private InitiativeStatus status;
    private List<Long> stageIds;
}
