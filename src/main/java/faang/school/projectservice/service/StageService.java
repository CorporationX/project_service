package faang.school.projectservice.service;

import ch.qos.logback.core.joran.conditional.IfAction;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final StageMapper stageMapper;
    private final ProjectValidator projectValidator;
    private final StageValidator stageValidator;
    private final List<StageFilter> stageFilters;
    private final TaskRepository taskRepository;

    @Transactional
    public StageDto createStage(StageDto stageDto) {
        Long projectId = stageDto.getProjectId();
        projectValidator.validateExistProjectById(projectId);
        stageValidator.validateStatusProject(projectId);
        Stage stage = stageMapper.toEntity(stageDto);
        stage.getStageRoles().forEach((role) -> role.setStage(stage));
        return stageMapper.toDto(stageRepository.save(stage));
    }

    public List<StageDto> getAllStageByFilter(StageFilterDto filters) {
        List<Stage> stages = stageRepository.findAll();
        return stageFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(stages.stream(), filters))
                .distinct() //чтобы не дублировало результат при выполнении нескольких фильтров
                .map((stageMapper::toDto))
                .toList();
    }

    @Transactional
    public void deleteStageById(Long stageId) {
        //Удаление делаю именно по объекту, предварительно его получаю через getById, внутри которого выполняется проверка,
        // что такой id есть в бд
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = taskRepository.findAll();
        List<Task> taskListOfStage = tasks.stream()
                .filter((task) -> task.getStage().getStageId().equals(stageId))
                .toList();
        if (!taskListOfStage.isEmpty()) {
            taskRepository.deleteAll(taskListOfStage);
        }
        stageRepository.delete(stage);
    }

    public StageDto getStagesById(Long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }
}