package faang.school.projectservice.service.initiative;

import faang.school.projectservice.dto.filter.initiative.InitiativeFilterDto;
import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.filter.initiative.InitiativeFilter;
import faang.school.projectservice.mapper.initiative.InitiativeMapper;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.repository.InitiativeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InitiativeService {
    private final InitiativeRepository initiativeRepository;
    private final InitiativeMapper initiativeMapper;
    private final List<InitiativeFilter> filters;

    public InitiativeDto createInitiative(InitiativeDto initiativeDto) {
        validateCreateInitiative(initiativeDto);

        Initiative initiative = initiativeMapper.toEntity(initiativeDto);
        Initiative savedInitiative = initiativeRepository.save(initiative);
        return initiativeMapper.toDto(savedInitiative);
    }

    public InitiativeDto updateInitiative(Long id, InitiativeDto initiativeDto) {
        validateUpdateInitiative(id);

        Initiative initiative = initiativeMapper.toEntity(initiativeDto);
        initiative.setId(id);

        Initiative updatedInitiative = initiativeRepository.save(initiative);
        return initiativeMapper.toDto(updatedInitiative);
    }

    public List<InitiativeDto> getAllInitiativesWithFilter(InitiativeFilterDto filterDto) {
        Stream<Initiative> initiativeStream = initiativeRepository.findAll().stream();

        for (InitiativeFilter filter : filters) {
            if (filter.isApplicable(filterDto)) {
                initiativeStream = filter.apply(initiativeStream, filterDto);
            }
        }
        return initiativeStream
                .map(initiativeMapper::toDto)
                .toList();
    }

    public List<InitiativeDto> getAllInitiatives() {
        return initiativeRepository.findAll().stream()
                .map(initiativeMapper::toDto)
                .toList();
    }

    public InitiativeDto getInitiativeById(Long id) {
        return initiativeRepository.findById(id)
                .map(initiativeMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Initiative not found for id: " + id));
    }

    private void validateCreateInitiative(InitiativeDto initiativeDto) {
        if (initiativeDto.getDescription() == null || initiativeDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty for initiative with name: " + initiativeDto.getName());
        }
        if (initiativeDto.getProjectId() == null) {
            throw new IllegalArgumentException("Initiative must be associated with a valid project. Initiative name: " + initiativeDto.getName());
        }

        boolean initiativeExist = initiativeRepository.existsByNameAndProjectId(initiativeDto.getName(), initiativeDto.getProjectId());
        if (initiativeExist) {
            throw new IllegalArgumentException("Initiative with the name '" + initiativeDto.getName() + "' already exists in project ID: " + initiativeDto.getProjectId());
        }

        boolean hasActiveInitiative = initiativeRepository.existsByProjectIdAndStatus(initiativeDto.getProjectId(), InitiativeStatus.OPEN);
        if (hasActiveInitiative) {
            throw new IllegalArgumentException("Project ID " + initiativeDto.getProjectId() + " already has an active initiative");
        }
    }

    private void validateUpdateInitiative(Long id) {
        Optional<Initiative> existingInitiative = initiativeRepository.findById(id);
        if (existingInitiative.isEmpty()) {
            throw new IllegalArgumentException("Initiative not found");
        }
        if (existingInitiative.get().getStatus() == InitiativeStatus.DONE) {
            throw new IllegalArgumentException("Cannot update a completed initiative");
        }
    }
}
