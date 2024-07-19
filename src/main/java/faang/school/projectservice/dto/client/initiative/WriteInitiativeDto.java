package faang.school.projectservice.dto.client.initiative;

import faang.school.projectservice.dto.client.state.WriteStageDto;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import lombok.Value;

import java.util.List;

@Value
public class WriteInitiativeDto {

    Long projectId;
    String name;
    String description;
    List<WriteStageDto> writeStageDtos;
    InitiativeStatus status;
    Long curatorId;
}
