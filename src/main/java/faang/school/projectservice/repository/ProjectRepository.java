package faang.school.projectservice.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public Page<Project> findAll(PageRequest pageRequest) {
        return projectJpaRepository.findAll(pageRequest);
    }

    public Page<Project> findAll(BooleanExpression booleanExpression, PageRequest pageRequest) {
        return projectJpaRepository.findAll(booleanExpression, pageRequest);
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
