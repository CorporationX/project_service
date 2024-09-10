package faang.school.projectservice.validator.subproject;

import com.amazonaws.services.kms.model.AlreadyExistsException;
import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ValidatorService {
    private final ProjectRepository projectRepository;

    public void isParentProjectExists(Long parentProjectId) {
        if (!projectRepository.existsById(parentProjectId)) {
            throw new NotFoundException("Parent project with id " + parentProjectId + " does not exist");
        }
    }

    public void isProjectExists(String name) {
        List<String> allProjectsName = projectRepository.findAll().stream()
                .map(Project::getName)
                .toList();
        if (allProjectsName.contains(name)) {
            throw new AlreadyExistsException("Project with name " + name + " already exists");
        }
    }
}
