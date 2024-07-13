package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.StageStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.stage.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StageService {
    private final StageRepository stageRepository;
    private final StageValidator stageValidator;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;

    public StageDto create(StageDto stageDto) {
        stageValidator.validateProjectId(stageDto.getStageId());
        stageValidator.validateStageRolesCount(stageDto.getStageRoles());
        stageDto.setStatus(StageStatus.CREATED);

        Stage stage = stageMapper.toEntity(stageDto, projectRepository);
        stageRepository.save(stage);

        return stageMapper.toDto(stage);
    }
}