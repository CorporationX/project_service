package faang.school.projectservice.service.stage.impl;

import faang.school.projectservice.dto.stage.NewStageDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stagerole.NewStageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.StageDeleteMode;
import faang.school.projectservice.model.StageStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.pattern.strategy.stage.StrategyForDeletingStage;
import faang.school.projectservice.service.stage.StageService;
import faang.school.projectservice.validator.project.ProjectValidator;
import faang.school.projectservice.validator.stage.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StageServiceImpl implements StageService {
    private final Map<StageDeleteMode, StrategyForDeletingStage> stageDeleteModeStrategyForDeletingStage;
    private final StageRolesRepository stageRolesRepository;
    private final StageJpaRepository stageRepository;
    private final ProjectValidator projectValidator;
    private final StageRolesMapper stageRolesMapper;
    private final StageValidator stageValidator;
    private final StageMapper stageMapper;

    @Override
    @Transactional
    public StageDto createStage(final NewStageDto dto) {
        Stage stageEntity = stageMapper.toEntity(dto);

        stageEntity = stageValidator.validateStageBeforeCreation(stageEntity, dto);
        stageEntity = stageRepository.save(stageEntity);

        return stageMapper.toDto(stageEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StageDto> getAllStages(Long projectId, StageStatus statusFilter) {
        projectValidator.validateProjectExistence(projectId);
        List<Stage> stageEntities = stageRepository.findAllByProjectIdAndStageStatus(projectId, statusFilter);

        return stageMapper.toDtoList(stageEntities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StageDto> getAllStages(Long projectId) {
        projectValidator.validateProjectExistence(projectId);
        List<Stage> stageEntities = stageRepository.findAllByProjectId(projectId);

        return stageMapper.toDtoList(stageEntities);
    }

    @Override
    @Transactional
    public void deleteStage(long stageId, Long stageToMigrateId, StageDeleteMode stageDeleteMode) {
        var strategy = stageDeleteModeStrategyForDeletingStage.get(stageDeleteMode);
        strategy.manageTasksOfStage(stageId, stageToMigrateId);

        stageRepository.deleteById(stageId);
    }

    @Override
    @Transactional
    public StageDto updateStage(Long stageId, List<NewStageRolesDto> newStageRolesDtoList) {
        Stage stageEntity = stageValidator.validateStageExistence(stageId);
        List<StageRoles> stageRolesEntities = stageRolesMapper.toEntityList(newStageRolesDtoList);

        stageRolesEntities.forEach(s -> s.setStage(stageEntity));
        stageRolesRepository.saveAll(stageRolesEntities);
        Stage updatedStage = getStage(stageId);

        return stageMapper.toDto(updatedStage);
    }

    @Override
    @Transactional(readOnly = true)
    public StageDto getStageById(Long stageId) {
        Stage stageEntity = stageValidator.validateStageExistence(stageId);

        return stageMapper.toDto(stageEntity);
    }


    public Stage getStage(long id) {
        var optional = stageRepository.findById(id);
        return optional.orElseThrow(() -> {
            var message = String.format("a stage with %d does not exist", id);

            return new DataValidationException(message);
        });
    }
}
