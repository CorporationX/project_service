package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.CreateStageDto;
import faang.school.projectservice.dto.client.ProjectIdDto;
import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.dto.client.StageIdDto;
import faang.school.projectservice.dto.client.UpdateStageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageServiceImpl implements StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;

    @Override
    public StageDto createStage(CreateStageDto stageDto) {
        log.info("Create stage for project id={}", stageDto.projectId());
        validateCreateStageDto(stageDto);
        Project project = projectRepository.findById(stageDto.projectId())
                .orElseThrow(() -> {
                    log.debug("Project with id={} is not found", stageDto.projectId());
                    return new EntityNotFoundException("Project with id "
                            + stageDto.projectId() + " is not found");
                });

        Stage stage = stageMapper.toStage(stageDto);
        stage.setProject(project);

        buildStageRoles(stageDto, stage);

        Stage savedStage = stageRepository.save(stage);
        log.info("Stage '{}' was created for id={}", stage.getStageName(), stageDto.projectId());
        return stageMapper.toStageDto(savedStage);
    }

    @Override
    public List<StageDto> getAllStagesOfProject(ProjectIdDto projectIdDto) {
        log.info("Getting all stages of project id={}", projectIdDto.projectId());
        List<Stage> stages = stageRepository.findAllByProjectId(projectIdDto.projectId());
        List<StageDto> result = new ArrayList<>();
        for (Stage stage : stages) {
            result.add(stageMapper.toStageDto(stage));
        }
        return result;
    }

    @Override
    public void deleteById(StageIdDto stageIdDto) {
        log.info("Delete stage id={}", stageIdDto.stageId());
        Stage stage = stageRepository.findById(stageIdDto.stageId())
                .orElseThrow(() -> {
                    log.debug("Stage with id={} is not found", stageIdDto.stageId());
                    return new EntityNotFoundException("Этап с id " + stageIdDto.stageId() + " не найден");
                });
        stageRepository.delete(stage);
        log.info("Stage '{}' was deleted ", stage.getStageName());
    }

    @Override
    public StageDto updateStage(UpdateStageDto stageDto) {
        Stage stage = stageRepository.findById(stageDto.stageId())
                .orElseThrow(() -> new EntityNotFoundException("Stage with id "
                        + stageDto.stageId() + " is not found"));

        Stage updatedStage = stageRepository.save(stage);
        return stageMapper.toStageDto(updatedStage);
    }

    @Override
    public StageDto getById(StageIdDto stageIdDto) {
        log.info("Getting Stage id={}", stageIdDto.stageId());
        Stage stage = stageRepository.findById(stageIdDto.stageId())
                .orElseThrow(() -> {
                    log.debug("Stage with id={} is not found", stageIdDto.stageId());
                    return new EntityNotFoundException("Stage with id "
                            + stageIdDto.stageId() + " is not found");
                });
        return stageMapper.toStageDto(stage);
    }

    private void validateCreateStageDto(CreateStageDto stageDto) {
        log.info("Validation CreateStageDto for project id={}", stageDto.projectId());
        if (stageDto.stageName() == null || stageDto.stageName().isBlank()) {
            log.error("Validation error: stageName is empty for project id={}", stageDto.projectId());
            throw new DataValidationException("Name should not be empty!");
        }
        if (stageDto.requiredRoles() == null || stageDto.requiredRoles().isEmpty()) {
            log.error("Validation error:requiredRoles is empty for project id={}", stageDto.projectId());
            throw new DataValidationException("Name should not be empty!");
        }
        log.info("Validation CreateStageDto done for project id={}", stageDto.projectId());
    }

    private void buildStageRoles(CreateStageDto stageDto, Stage stage) {
        log.info("Creation StageRoles for stage '{}' of project id={}", stage.getStageName(), stageDto.projectId());
        List<StageRoles> stageRoles = new ArrayList<>();
        stageDto.requiredRoles().forEach(roleDto -> {
            StageRoles sr = new StageRoles();
            sr.setTeamRole(roleDto.role());
            sr.setCount(roleDto.count());
            sr.setStage(stage);
            stageRoles.add(sr);
            log.info("Added role '{}'with amounts {} for stage '{}'", roleDto.role(), roleDto.count(),
                    stage.getStageName());
        });
        stage.setStageRoles(stageRoles);
        log.info("All StageRoles were created for the stage '{}'", stage.getStageName());
    }
}
