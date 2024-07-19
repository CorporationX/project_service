package faang.school.projectservice.service;

import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectJpaRepository projectJpaRepository;

    public Project findById(Long id) {
        return projectJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Project with id %s not found", id)));
    }
}
