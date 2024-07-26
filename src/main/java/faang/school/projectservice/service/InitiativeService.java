package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InitiativeDto;
import faang.school.projectservice.dto.client.InitiativeFilterDto;
import faang.school.projectservice.dto.client.InitiativeStatusDto;
import faang.school.projectservice.filter.initiative.InitiativeFilter;
import faang.school.projectservice.mapper.initiative.InitiativeMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.repository.InitiativeRepository;
import faang.school.projectservice.validation.InitiativeValidator;
import jakarta.persistence.EntityNotFoundException;
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

    @Transactional
    public void createInitiative(InitiativeDto initiativeDto) {
        if (!initiativeValidator.checkProjectActiveInitiative(initiativeDto.getProjectId())) {
            if (initiativeValidator.checkCuratorRole(initiativeDto.getCuratorId())) {
                Initiative initiative = initiativeMapper.toEntity(initiativeDto);
                initiativeRepository.save(initiative);
            } else {
                log.info("The curator does not have the required specialization Method: checkCuratorRole");
                throw new RuntimeException("The curator does not have the required specialization");
            }
        } else {
            log.info("Project already have active initiative Method: checkProjectActiveInitiative");
            throw new RuntimeException("Project already have active initiative");
        }
    }

    @Transactional
    public void updateInitiative(Long initiativeId,
                                 InitiativeStatusDto initiativeStatusDto) {
        Initiative initiative = initiativeRepository.findById(initiativeId)
                .orElseThrow(() -> {
                    log.info("Initiative not found Method: updateInitiative");
                    return new EntityNotFoundException("Initiative not found");
                });
        switch (initiativeStatusDto.getStatus()) {
            case DONE -> statusDone(initiative);
            case CLOSED -> {
                initiative.setStatus(InitiativeStatus.CLOSED);
                initiativeRepository.save(initiative);
            }
            case OPEN -> {
                initiative.setStatus(InitiativeStatus.OPEN);
                initiativeRepository.save(initiative);
            }
            case ACCEPTED -> {
                initiative.setStatus(InitiativeStatus.ACCEPTED);
                initiativeRepository.save(initiative);
            }
            case IN_PROGRESS -> {
                initiative.setStatus(InitiativeStatus.IN_PROGRESS);
                initiativeRepository.save(initiative);
            }
        }
    }

    private void statusDone(Initiative initiative) {
        if (initiativeValidator.checkStagesStatusInitiative(initiative)) {
            Moment moment = momentService.createMoment(initiative);
            initiative.getProject().getMoments().add(moment);
            initiative.getSharingProjects()
                    .forEach(project -> project.getMoments().add(moment));
            initiativeRepository.delete(initiative);
        } else {
            log.info("An attempt to change the status while the stage is active Method: checkStagesStatusInitiative");
            throw new RuntimeException("You cannot change the status because not all stages have been completed yet");
        }

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
        return initiativeMapper.toDto(initiativeRepository
                .findById(initiativeId)
                .orElseThrow(() -> {
                    log.info("Initiative not found, Method: findById");
                    return new EntityNotFoundException("Initiative not found");
                }));
    }

}