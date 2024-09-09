package faang.school.projectservice.service;

import faang.school.projectservice.dto.InitiativeDto;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InitiativeRepository;
import faang.school.projectservice.service.mapper.InitiativeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InitiativeService {

    @Autowired
    private InitiativeRepository initiativeRepository;

    @Autowired
    private InitiativeMapper initiativeMapper;

    public InitiativeDto createInitiative(InitiativeDto initiativeDto) {
        if (initiativeRepository.existsByProjectIdAndStatus(initiativeDto.getProjectId(), "active")) {
            throw new IllegalArgumentException("Project already has an active initiative");
        }
        Initiative initiative = initiativeMapper.toEntity(initiativeDto);
        initiative = initiativeRepository.save(initiative);
        return initiativeMapper.toDto(initiative);
    }

    public InitiativeDto updateInitiative(Long id, InitiativeDto initiativeDto) {
        Optional<Initiative> optionalInitiative = initiativeRepository.findById(id);
        if (!optionalInitiative.isPresent()) {
            throw new IllegalArgumentException("Initiative not found");
        }

        Initiative initiative = optionalInitiative.get();

        if ("completed".equals(initiativeDto.getStatus())) {
            List<Stage> stages = initiative.getStages();
            boolean allStagesCompleted = stages.stream().allMatch(stage -> "completed".equals(stage.getStatus()));
            if (!allStagesCompleted) {
                throw new IllegalStateException("Cannot close initiative until all stages are completed");
            }
        }

        initiative = initiativeMapper.toEntity(initiativeDto);
        initiative.setId(id);
        initiative = initiativeRepository.save(initiative);
        return initiativeMapper.toDto(initiative);
    }

    public List<InitiativeDto> getAllInitiatives(String status, Long curatorId) {
        List<Initiative> initiatives = initiativeRepository.findAll();
        return initiatives.stream()
                .filter(initiative -> (status == null || initiative.getStatus().equals(status)) &&
                        (curatorId == null || initiative.getCuratorId().equals(curatorId)))
                .map(initiativeMapper::toDto)
                .collect(Collectors.toList());
    }

    public InitiativeDto getInitiativeById(Long id) {
        Optional<Initiative> initiative = initiativeRepository.findById(id);
        return initiative.map(initiativeMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Initiative not found"));
    }
}