package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Alexander Bulgakov
 */

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectJpaRepository projectJpaRepository;

    public Project getProjectById(long id) {
        return projectJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Project by id: %s noy found!", id)));
    }

    public void existsProjectById(long id) {
        if (!projectJpaRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Project does not exists by id: %s", id));
        }
    }
}
