package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StageValidator {
    private final ProjectService projectService;

    public List<Stage> getStages(Long projectId){
        List<Stage> stages = projectService.getProjectById(projectId).getStages();
        if (stages.isEmpty()){
            log.info("Method getValidationStageIsEmpty return empty List<Stage>");
            throw new RuntimeException("List is empty");
        } else {
            return stages;
        }
    }

    public void validationProjectById(Long projectId){
        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            log.info("Method getValidationProjectById return Null");
            throw new EntityNotFoundException("Project not found");
        }
    }
}
