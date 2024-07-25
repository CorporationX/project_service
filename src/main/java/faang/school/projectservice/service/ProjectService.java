package faang.school.projectservice.service;

import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectJpaRepository projectJpaRepository;

    public Project findById(Long id) {
        return projectJpaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("InitiativeService.update: event with id {} not found", id);
                    return  new EntityNotFoundException(String.format("Project with id %s not found", id));
                });
    }
}
