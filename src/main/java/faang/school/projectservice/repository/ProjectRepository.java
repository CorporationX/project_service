package faang.school.projectservice.repository;

import faang.school.projectservice.exceptions.NotFoundException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRepository {
    private final ProjectJpaRepository jpaRepository;

    public Project getProjectById(Long projectId) {
        return jpaRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Project not found by id: %s", projectId))
        );
    }

    public List<Project> findAll() {
        return jpaRepository.findAll();
    }

    public List<Project> findAllByIds(List<Long> ids) {
        return jpaRepository.findAllById(ids);
    }

    public boolean existsByOwnerUserIdAndName(Long userId, String name) {
        return jpaRepository.existsByOwnerIdAndName(userId, name);
    }

    public Project save(Project project){
        return jpaRepository.save(project);
    }

    public boolean existsById(Long id){
        return jpaRepository.existsById(id);
    }

    public Project findById(long projectId) {
        return jpaRepository.findById(projectId).orElseThrow(() ->
                new NotFoundException(String.format("Project doesn't exist by id: %s", projectId)));
    }
}
