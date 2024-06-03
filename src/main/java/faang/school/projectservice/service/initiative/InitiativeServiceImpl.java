package faang.school.projectservice.service.initiative;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.dto.initiative.InitiativeFilterDto;
import faang.school.projectservice.mapper.InitiativeMapper;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.repository.InitiativeRepository;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.validation.initiative.InitiativeValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InitiativeServiceImpl implements InitiativeService {
    private final InitiativeMapper mapper;
    private final InitiativeValidator validator;
    private final InitiativeRepository initiativeRepository;
    private final InitiativeFilterService filterService;
    private final MomentService momentService;

    @Override
    public InitiativeDto create(InitiativeDto initiative) {
        validator.validate(initiative);
        validator.validateCurator(initiative);

        Initiative entity = mapper.toEntity(initiative);
        Initiative saved = initiativeRepository.save(entity);

        return mapper.toDto(saved);
    }

    @Override
    public InitiativeDto update(InitiativeDto initiative) {
        validator.validate(initiative);
        validator.validateCurator(initiative);

        Initiative entity = mapper.toEntity(initiative);

        if (initiative.getStatus() == InitiativeStatus.DONE) {
            validator.validateClosedInitiative(initiative);
            momentService.createFromInitiative(entity);
        }

        Initiative saved = initiativeRepository.save(entity);

        return mapper.toDto(saved);
    }

    @Override
    public List<InitiativeDto> getAllByFilter(InitiativeFilterDto filter) {
        Stream<Initiative> initiatives = initiativeRepository.findAll().stream();
        return filterService.applyAll(initiatives, filter)
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<InitiativeDto> getAll() {
        return initiativeRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public InitiativeDto getById(long id) {
        Initiative initiative = initiativeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("can't find initiative with id:" + id));
        return mapper.toDto(initiative);
    }
}
