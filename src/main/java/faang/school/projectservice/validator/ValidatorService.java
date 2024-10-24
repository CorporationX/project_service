package faang.school.projectservice.validator;

import com.amazonaws.services.kms.model.AlreadyExistsException;
import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.enums.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ValidatorService {
    private final ProjectRepository projectRepository;

    public void isProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException("Project with id " + projectId + " does not exist");
        }
    }

    public void isProjectExistsByName(String name) {
        List<String> allProjectsName = projectRepository.findAll().stream()
                .map(Project::getName)
                .toList();
        if (allProjectsName.contains(name)) {
            throw new AlreadyExistsException("Project with name " + name + " already exists");
        }
    }

    public void isVisibilityRight(ProjectVisibility parentProjectVisibility, ProjectVisibility subProjectVisibility) {
        if (parentProjectVisibility.equals(ProjectVisibility.PUBLIC) &&
                subProjectVisibility.equals(ProjectVisibility.PRIVATE)) {
            throw new IllegalArgumentException("Can't create SubProject, because ParentProject visibility is public");
        }
    }
}
