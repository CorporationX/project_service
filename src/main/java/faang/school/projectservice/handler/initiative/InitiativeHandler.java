package faang.school.projectservice.handler.initiative;

import faang.school.projectservice.dto.client.initiative.WriteInitiativeDto;
import faang.school.projectservice.model.initiative.Initiative;

public interface InitiativeHandler {

    boolean isApplicable(WriteInitiativeDto writeInitiativeDto, Initiative initiative);

    void handle(Initiative initiative);
}
