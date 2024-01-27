package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.jpa.ProjectJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Alexander Bulgakov
 */

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectJpaRepository projectJpaRepository;

    public void existsProjectById(long id) {
        if (!projectJpaRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Project does not exists by id: %s", id));
        }
    }
}
