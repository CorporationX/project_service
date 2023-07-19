package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;

    private final StageMapper stageMapper;

    public StageDto createStage(StageDto stageDto) {
        validationStageDto(stageDto);
        Stage save = stageRepository.save(stageMapper.toEntity(stageDto));
        return stageMapper.toDto(save);
    }

    private void validationStageDto(StageDto stageDto) {
        if (stageDto.getProject().getStatus().equals(ProjectStatus.COMPLETED) ||
                stageDto.getProject().getStatus().equals(ProjectStatus.CANCELLED)) {
            throw new DataValidationException("You cannot create a stage in a closed or canceled project");
        }

    }
}
