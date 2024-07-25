package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.initiative.ReadInitiativeDto;
import faang.school.projectservice.dto.client.initiative.WriteInitiativeDto;
import faang.school.projectservice.filter.initiative.InitiativeFieldFilter;
import faang.school.projectservice.filter.initiative.InitiativeFilterDto;
import faang.school.projectservice.handler.initiative.InitiativeHandler;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.mapper.InitiativeToReadInitiativeDtoMapper;
import faang.school.projectservice.mapper.WriteInitiativeDtoToInitiativeMapper;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.repository.InitiativeRepository;
import faang.school.projectservice.valitator.initiative.WriteInitiativeDtoValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InitiativeService {

    private final InitiativeRepository initiativeRepository;
    private final InitiativeToReadInitiativeDtoMapper initiativeToReadInitiativeDtoMapper;
    private final WriteInitiativeDtoToInitiativeMapper writeInitiativeDtoToInitiativeMapper;
    private final List<InitiativeHandler> initiativeHandlers;
    private final List<InitiativeFieldFilter> initiativeFieldFilters;
    private final WriteInitiativeDtoValidator writeInitiativeDtoValidator;
    private final StageJpaRepository stageJpaRepository;

    @Transactional
    public ReadInitiativeDto create(WriteInitiativeDto writeInitiativeDto) {
        writeInitiativeDtoValidator.validate(writeInitiativeDto);
        Initiative initiative = writeInitiativeDtoToInitiativeMapper.map(writeInitiativeDto);
        stageJpaRepository.saveAll(initiative.getStages());
        initiativeRepository.save(initiative);
        return initiativeToReadInitiativeDtoMapper.map(initiative);
    }

    @Transactional
    public ReadInitiativeDto update(Long id, WriteInitiativeDto writeInitiativeDto) {
        return initiativeRepository.findById(id)
                .map(entity -> {
                    initiativeHandlers.stream()
                            .filter(handler -> handler.isApplicable(writeInitiativeDto, entity))
                            .forEach(handler -> handler.handle(entity));
                    return entity;
                })
                .map(entity -> writeInitiativeDtoToInitiativeMapper.map(writeInitiativeDto, entity))
                .map(initiativeRepository::saveAndFlush)
                .map(initiativeToReadInitiativeDtoMapper::map)
                .orElseThrow(() -> {
                    log.error("InitiativeService.update: event with id {} not found", id);
                    return new EntityNotFoundException(String.format("Initiative with id %s not found", id));
                });
    }

    public List<ReadInitiativeDto> findAllByFilter(InitiativeFilterDto initiativeFilterDto) {
        return initiativeRepository.findAll().stream()
                .filter(initiativeFieldFilters.stream()
                        .filter(fieldFilter -> fieldFilter.isApplicable(initiativeFilterDto))
                        .map(fieldFilter -> fieldFilter.apply(initiativeFilterDto))
                        .reduce(initiative -> true, Predicate::and))
                .map(initiativeToReadInitiativeDtoMapper::map)
                .toList();
    }

    public List<ReadInitiativeDto> findAll() {
        return initiativeRepository.findAll().stream()
                .map(initiativeToReadInitiativeDtoMapper::map)
                .toList();
    }

    public ReadInitiativeDto findById(Long initiativeId) {
        return initiativeRepository.findById(initiativeId)
                .map(initiativeToReadInitiativeDtoMapper::map)
                .orElseThrow(() -> {
                    log.error("InitiativeService.findById: event with id {} not found", initiativeId);
                    return new EntityNotFoundException(String.format("Initiative with id %s not found", initiativeId));
                });
    }
}
