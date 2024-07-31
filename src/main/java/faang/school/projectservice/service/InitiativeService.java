package faang.school.projectservice.service;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.dto.initiative.InitiativeStatusDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.initiative.InitiativeFilter;
import faang.school.projectservice.mapper.initiative.InitiativeMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InitiativeRepository;
import faang.school.projectservice.validation.InitiativeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitiativeService {
    private final InitiativeRepository initiativeRepository;
    private final List<InitiativeFilter> initiativeFilters;
    private final InitiativeMapper initiativeMapper;
    private final MomentService momentService;
    private final InitiativeValidator initiativeValidator;
    private final StageService stageService;


    @Transactional
    public Initiative createInitiativeEntity(InitiativeDto initiativeDto) {
        initiativeValidator.projectHasNotActiveInitiative(initiativeDto.getProjectId());
        initiativeValidator.curatorRoleValid(initiativeDto.getCuratorId());
        Initiative initiative = initiativeMapper.toEntity(initiativeDto);
        List<Stage> stages = initiativeDto.
                getStageDtoList().
                stream().
                map(stageService::createStageEntity).
                toList();
        initiative.setStages(stages);
        return initiativeRepository.save(initiative);
    }

    @Transactional
    public InitiativeDto createInitiative(InitiativeDto initiativeDto) {
        return initiativeMapper.toDto(createInitiativeEntity(initiativeDto));
    }

    @Transactional
    public InitiativeDto updateInitiative(Long initiativeId,
                                          InitiativeStatusDto initiativeStatusDto){
        return initiativeMapper.toDto(updateInitiativeEntity(initiativeId, initiativeStatusDto));
    } // не забыть протестировать

    @Transactional
    public Initiative updateInitiativeEntity(Long initiativeId,
                                       InitiativeStatusDto initiativeStatusDto) {
        Initiative initiative = getInitiative(initiativeId);
        switch (initiativeStatusDto.getStatus()) {
            case DONE -> {return finalizeInitiative(initiative);}
            case CLOSED -> {return changeInitiativeStatus(initiative, InitiativeStatus.CLOSED);}
            case OPEN -> {return changeInitiativeStatus(initiative, InitiativeStatus.OPEN);}
            case ACCEPTED -> {return changeInitiativeStatus(initiative, InitiativeStatus.ACCEPTED);}
            case IN_PROGRESS -> {return changeInitiativeStatus(initiative, InitiativeStatus.IN_PROGRESS);}
        }
        return initiative;
    }

    @Transactional(readOnly = true)
    public List<InitiativeDto> findAllInitiativesWithFilters(InitiativeFilterDto initiativeFilterDto) {
        List<Initiative> initiatives = initiativeRepository.findAll();
        return initiativeFilters.stream()
                .filter(filter -> filter.isApplicable(initiativeFilterDto))
                .reduce(initiatives.stream(),
                        (stream, filter) -> filter.apply(stream, initiativeFilterDto), Stream::concat)
                .map(initiativeMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InitiativeDto> findAllInitiatives() {
        return initiativeMapper.toDtos(initiativeRepository.findAll());
    }

    @Transactional(readOnly = true)
    public InitiativeDto findById(Long initiativeId) {
        return initiativeMapper.toDto(getInitiative(initiativeId));
    }

    private Initiative getInitiative(Long initiativeId) {
        return initiativeRepository.findById(initiativeId)
                .orElseThrow(() -> {
                    log.info("Initiative not found Method: updateInitiative");
                    return new EntityNotFoundException("Initiative not found");
                });
    }

    private Initiative changeInitiativeStatus(Initiative initiative, InitiativeStatus closed) {
        initiative.setStatus(closed);
        return initiativeRepository.save(initiative);
    }

    private Initiative finalizeInitiative(Initiative initiative) {
        initiativeValidator.checkAllTasksDone(initiative);
        Moment moment = momentService.createMoment(initiativeToMomentDto(initiative));
        initiative.getProject().getMoments().add(moment);
        initiative.getSharingProjects()
                .forEach(project -> project.getMoments().add(moment));
        return initiative;
    }

    private MomentDto initiativeToMomentDto(Initiative initiative) {
        List<Long> projectsIds = initiative.getSharingProjects()
                .stream()
                .map(Project::getId)
                .toList();
        return MomentDto.builder()
                .name(initiative.getName())
                .description(initiative.getDescription())
                .projectIds(projectsIds)
                .userIds(List.of(initiative.getCurator().getId()))
                .build();
    }

}
