package faang.school.projectservice.repository;

import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.NotFoundException;
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

    public List<Project> findAll() {
        return projectJpaRepository.findAll();
    }

    public List<Project> findAllByIds(Collection<Long> ids) {
        List<Project> projects = projectJpaRepository.findAllById(ids);
        if (ids.size() != projects.size()) {
            throw new NotFoundException(ErrorMessage.SOME_OF_PROJECTS_NOT_EXIST);
        }
        return projects;
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

    public List<Project> findAllDistinctByTeamMemberIds(Collection<Long> teamMemberIds) {
        return projectJpaRepository.findAllDistinctByTeamMemberIds(teamMemberIds);
    }
}
