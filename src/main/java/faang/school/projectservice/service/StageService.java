package faang.school.projectservice.service;

import faang.school.projectservice.exception.project.ProjectException;
import faang.school.projectservice.exception.stage.StageException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public void createStage(Stage stage, Project project) {
        List<Stage> stageList = new ArrayList<>();
        stageList.add(validStage(stage));
        validProject(project).setStages(stageList);
        stageRepository.save(validStage(stage));
    }

    private Project validProject(Project project) {
        if (project.getStatus().equals(ProjectStatus.CREATED) || project.getStatus().equals(ProjectStatus.IN_PROGRESS)) {
            return  projectRepository.getProjectById(project.getId());
        } else {
            throw new ProjectException("Project unavailable");
        }
    }

    private Stage validStage(Stage stage) {
        if (stage.getStageRoles().isEmpty()) {
            throw new StageException("Participants must have roles");
        }
        if (stage.getStageId() == null) {
            throw new StageException("Invalid ID");
        }
        if (stage.getStageName().isBlank()) {
            throw new StageException("Stage name cannot be empty");
        }
        if (stage.getProject() == null) {
            throw new StageException("The stage must be related to the project");
        }
        if (stage.getTasks() == null) {
            throw new StageException("The stage must have a status");
        }
        return stageRepository.getById(stage.getStageId());
    }
}
