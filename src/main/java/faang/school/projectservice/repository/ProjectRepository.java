package faang.school.projectservice.repository;

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
    private final ProjectJpaRepository projectJpaRepository;

    public Optional<Project> findById(Long projectId) {
        return projectJpaRepository.findById(projectId);
    }

    public Project getProjectById(Long projectId) {
        return projectJpaRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Project not found by id: %s", projectId))
        );
    }

    public List<Project> findAll() {
        return projectJpaRepository.findAll();
    }

    public List<Project> findAllByIds(List<Long> ids) {
        return projectJpaRepository.findAllById(ids);
    }

    public boolean existsByOwnerUserIdAndName(Long userId, String name) {
        return projectJpaRepository.existsByOwnerIdAndName(userId, name);
    }

    public Project save(Project project){
        return projectJpaRepository.save(project);
    }

    public boolean existsById(Long id){
        return projectJpaRepository.existsById(id);
    }
}
