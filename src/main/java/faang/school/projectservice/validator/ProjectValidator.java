package faang.school.projectservice.validator;

import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public void verifyProjectExists(Long projectId) {
        if (projectRepository.findById(projectId) == null) {
            log.info("Проект ID: {} не найден в БД", projectId);
            throw new EntityNotFoundException("Проект ID " + projectId + " не найден в БД");
        }
    }
}
