package faang.school.projectservice.service.initiative;

import faang.school.projectservice.dtos.initiative.InitiativeDto;
import faang.school.projectservice.mapper.initiative.InitiativeMapper;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.repository.InitiativeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InitiativeService {
    private final InitiativeRepository initiativeRepository;
    private final InitiativeMapper initiativeMapper;

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

    public List<InitiativeDto> getAllInitiativesWithFilter(InitiativeStatus status, Long curatorId) {
        return initiativeRepository.findAll().stream()
                .filter(initiative -> (status == null || initiative.getStatus() == status))
                .filter(initiative -> (curatorId == null || Objects.equals(initiative.getCurator().getId(), curatorId)))
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
                .orElseThrow(() -> new RuntimeException("Initiative not found"));
    }

    private void validateCreateInitiative(InitiativeDto initiativeDto) {
        boolean isEmptyDescription = initiativeDto.getDescription().isEmpty();
        boolean isBlankDescription = initiativeDto.getDescription().isBlank();

        List<Initiative> initiatives = initiativeRepository.findAll();
        boolean initiativeExist = initiatives.stream()
                .anyMatch(existingInitiative -> existingInitiative.getName().equals(initiativeDto.getName()) &&
                        existingInitiative.getProject().getId().equals(initiativeDto.getProjectId())
                );

        if (isBlankDescription || isEmptyDescription) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (initiativeExist) {
            throw new IllegalArgumentException("Initiative with the same name already exists in this project");
        }
        if (initiativeDto.getProjectId() == null) {
            throw new IllegalArgumentException("Initiative must be associated with a valid project");
        }

        boolean hasActiveInitiative = initiativeRepository.findAll().stream()
                .anyMatch(initiative -> initiative.getProject().getId().equals(initiativeDto.getProjectId())
                        && initiative.getStatus() == InitiativeStatus.OPEN);
        if (hasActiveInitiative) {

            throw new IllegalArgumentException("Project already has an active initiative");
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
