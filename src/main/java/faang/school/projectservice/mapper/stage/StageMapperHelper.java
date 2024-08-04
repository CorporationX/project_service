package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageMapperHelper {
    private final ProjectService projectService;


    public Project findProjectById(Long projectId){
        return projectService.getProjectById(projectId);
    }



}
