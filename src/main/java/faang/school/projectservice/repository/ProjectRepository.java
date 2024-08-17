package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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

    public List<Project> getAllSubprojectsToProjectById(Long projectId) {
        return projectJpaRepository.getAllSubprojectsForProjectId(projectId);
    }

    public List<Project> findAll() {
        return projectJpaRepository.findAll();
    }

    public List<Project> findAllByIds(Collection<Long> ids) {
        return projectJpaRepository.findAllById(ids);
    }

    public boolean existsByOwnerUserIdAndName(Long userId, String name) {
        return projectJpaRepository.existsByOwnerIdAndName(userId, name);
    }

    public Project save(Project project){
        return projectJpaRepository.save(project);
    }

    public void saveAll(List<Project> projects){
         projectJpaRepository.saveAll(projects);
    }

    public boolean existsById(Long id){
        return projectJpaRepository.existsById(id);
    }

    public List<Project> findAllDistinctByTeamMemberIds(Collection<Long> teamMemberIds) {
        return projectJpaRepository.findAllDistinctByTeamMemberIds(teamMemberIds);
    }
}
