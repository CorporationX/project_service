package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.dto.stage.ForwardStageDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper mapper;
    private final StageValidator stageValidator;

    @Transactional
    public StageDto createStage(StageDto stageDto) {
        Project project = projectRepository.getProjectById(stageDto.getProjectId());
        stageValidator.validateStageDto(stageDto, project);
        Stage stage = mapper.toEntity(stageDto);
        stage.getStageRoles().forEach(i -> i.setStage(stage));
        return mapper.toDto(stageRepository.save(stage));
    }

    @Transactional(readOnly = true)
    public List<StageDto> findStageByFilter(StageFilterDto filter) {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .filter(f -> f.getStatus() == filter.getProjectStatus())
                .flatMap(p -> p.getStages().stream())
                .skip((long) filter.getSize() * filter.getPage())
                .limit(filter.getSize())
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public StageDto findStageById(Long stageId) {
        return mapper.toDto(stageRepository.getById(stageId));
    }

    @Transactional
    public void removeStageById(Long stageId) {
        stageRepository.delete(stageRepository.getById(stageId));
    }

    @Transactional(readOnly = true)
    public List<StageDto> findAll() {
        return stageRepository.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional
    public void closeStageById(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = stage.getTasks();
        //Не нашел статус "CLOSED", наиболее близкие к нему статусы "DONE"/"CANCELLED"
        tasks.forEach(i -> i.setStatus(TaskStatus.CANCELLED));
        stage.setTasks(tasks);
        stageRepository.save(stage);
    }

    @Transactional
    public void forwardStage(ForwardStageDto forwardStageDto) {
        if (forwardStageDto.getForwardId() == null) {
            throw new DataValidationException("Specify the stage ID for the forward");
        }
        Stage currentStage = stageRepository.getById(forwardStageDto.getId());
        Stage forwardStage = stageRepository.getById(forwardStageDto.getForwardId());
        List<Task> tasks = currentStage.getTasks().stream().filter(
                i -> i.getStatus() != TaskStatus.CANCELLED && i.getStatus() != TaskStatus.DONE
        ).peek(i -> i.setStage(forwardStage)
        ).toList();
        if (!tasks.isEmpty()) {
            forwardStage.getTasks().addAll(tasks);
            //Возможно удалятся таски каскадно
            stageRepository.delete(currentStage);
            stageRepository.save(forwardStage);
        } else {
            throw new DataValidationException("There is nothing to forward");
        }
    }
}
