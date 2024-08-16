package faang.school.projectservice.service;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectRepository projectRepository;

    public Project getProject(Long id) {
        Project project = projectRepository.getProjectById(id);
        if(project == null){
            throw new DataValidationException("Project with ID = " + id + " not found");
        }
        return project;
    }
}
