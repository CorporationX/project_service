package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.StageServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper mapper;
    private final StageServiceValidator validator;
    public void create(StageDto stageDto) {
        validator.validateStageDto(stageDto);
        stageRepository.save(mapper.toStage(stageDto));
    }

    public List<StageDto> getAllStages(Long projectId) {
        validator.validateProjectId(projectId);
        return mapper.toDtoList(projectRepository.getProjectById(projectId).getStages());
    }

    public StageDto getStageById(Long id) {
        return mapper.toDto(stageRepository.getById(id));
    }
}
