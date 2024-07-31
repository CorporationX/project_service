package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MomentMapperHelper {
    private final ProjectRepository projectRepository;

    public List<Project> mapProjects(List<Long> projectIds){
        return projectRepository.findAllByIds(projectIds);
    }
}
