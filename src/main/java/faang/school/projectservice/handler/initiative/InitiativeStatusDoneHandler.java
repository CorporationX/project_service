package faang.school.projectservice.handler.initiative;

import faang.school.projectservice.dto.client.initiative.WriteInitiativeDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.InitiativeHandler;
import faang.school.projectservice.valitator.initiative.InitiativeStatusDoneValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InitiativeStatusDoneHandler implements InitiativeHandler {

    private final InitiativeStatusDoneValidator initiaiveStatusDoneValidator;
    private final MomentRepository momentRepository;

    public boolean isApplicable(WriteInitiativeDto writeInitiativeDto, Initiative initiative) {
        return writeInitiativeDto.getStatus() != null
                && writeInitiativeDto.getStatus().equals(InitiativeStatus.DONE)
                && !initiative.getStatus().equals(InitiativeStatus.DONE);
    }

    @Transactional
    @Override
    public void handle(Initiative initiative) {
        initiaiveStatusDoneValidator.checkCompletedStages(initiative);
        momentRepository.save(this.createMomentForDoneInitiative(initiative));
    }

    private Moment createMomentForDoneInitiative(Initiative initiative) {
        Moment moment = new Moment();
        moment.setName("Выполненная инициатива");
        moment.setDate(LocalDateTime.now());
        moment.addTeamMember(initiative.getCurator());
        initiative.getSharingProjects().forEach(moment::addProject);
        return moment;
    }
}
