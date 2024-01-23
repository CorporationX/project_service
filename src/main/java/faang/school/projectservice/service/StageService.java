package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final ProjectValidator projectValidator;
    private final StageValidator stageValidator;

    public StageDto save(StageDto stage) {
        Long projectId = stage.getProjectId();
        projectValidator.validateExistProjectById(projectId);
        stageValidator.validateStatusProject(projectId);
        Stage savedStage = stageRepository.save(stageMapper.toEntity(stage));
        return stageMapper.toDto(savedStage);
    }

//    public List<StageDto> getAllStages(Long projectId) {
//        stageRepository.findAll()
//    }

    public StageDto getStagesById(Long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }
}