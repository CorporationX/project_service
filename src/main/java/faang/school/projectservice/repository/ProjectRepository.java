package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

    public List<Project> findByName(String name) {
        List<Project> projects = findAll();
        return projects.stream()
                .filter(project -> project.getName().equals(name))
                .toList();
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

    public Project findProjectByNameAndOwnerId(String name, Long ownerId) {
        List<Project> projects = findByName(name);
        for (Project project : projects) {
            if (project.getOwnerId().equals(ownerId)) {
                return project;
            }
        }
        return null;
    }

    public Project findById(long id) {
        return projectJpaRepository.getById(id);
    }
}
