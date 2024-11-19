package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProjectRepository {
    private final ProjectJpaRepository projectJpaRepository;

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

    public Project save(Project project) {
        return projectJpaRepository.save(project);
    }

    public boolean existsById(Long id) {
        return projectJpaRepository.existsById(id);
    }

    public List<Long> findExistingByIds(List<Long> ids){
        return projectJpaRepository.findExistingIds(ids);
    }

    public List<Project> getSubProjectsByParentId(Long Id) {
            return projectJpaRepository.findAllSubProjectsByParentId(Id);
    }

    public void saveAll(List<Project> projects) {
        projectJpaRepository.saveAll(projects);
    }
}
