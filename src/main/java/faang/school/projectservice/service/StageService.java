package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.stage.DeleteStageDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.project.ProjectException;
import faang.school.projectservice.exception.stage.StageException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;


@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;
    private final ProjectMapper projectMapper;
    private final TaskRepository taskRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;

    @Transactional
    public StageDto createStage(StageDto stageDto) {
        Stage stage = stageRepository.save(stageMapper.toStage(validStage(stageDto)));
        return stageMapper.toStageDto(stage);
    }

    public void deleteStage(Long stageId1, DeleteStageDto deleteStageDto, Long stageId2) {
        Stage stage = stageRepository.getById(stageId1);
        List<Task> tasks = stage.getTasks();
        if (deleteStageDto.equals(DeleteStageDto.CASCADE)) {
            taskRepository.deleteAll(tasks);
        }
        if (deleteStageDto.equals(DeleteStageDto.CLOSE)) {
            tasks.forEach(task -> task.setStatus(TaskStatus.DONE));
        }
        if (deleteStageDto.equals(DeleteStageDto.MOVE_TO_ANOTHER_STAGE)) {
            stage.setTasks(List.of());
            Stage newStage = stageRepository.getById(stageId2);
            newStage.setTasks(tasks);
            stageRepository.save(newStage);
        }
        stageRepository.delete(stage);
    }

    public List<StageDto> getAllStage(ProjectDto projectDto) {
        if (projectDto.getName().isBlank()) {
            throw new ProjectException("Name cannot be empty");
        }
        if (projectDto.getProjectStatus() == null) {
            throw new ProjectException("The project must have a status");
        }
        projectMapper.toProjectDto(projectRepository.getProjectById(projectDto.getId()));
        return projectDto.getStageList();
    }

    private StageDto validStage(StageDto stage) {
        if (stage.getStageId() == null) {
            throw new StageException("Invalid ID");
        }
        if (stage.getStageName().isBlank()) {
            throw new StageException("Name cannot be empty");
        }

        Project project = projectRepository.getProjectById(stage.getProject().getId());
        boolean projectInProgress = project.getStatus().equals(ProjectStatus.IN_PROGRESS);
        boolean projectCreated = project.getStatus().equals(ProjectStatus.CREATED);

        if (projectInProgress || projectCreated) {
            return stageMapper.toStageDto(stageRepository.getById(stage.getStageId()));
        } else {
            throw new ProjectException(MessageFormat.format("Project with id {0} unavailable", project.getId()));
        }
    }
}
