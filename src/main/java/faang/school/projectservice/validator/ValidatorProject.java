package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class ValidatorProject {
    private final ProjectMapper mapper;
    private final ProjectRepository projectRepository;
    private final ProjectJpaRepository projectJpaRepository;

    public Project getEntity(ProjectDto projectDto) {
        return mapper.toEntity(projectDto);
    }

    public void validationName(ProjectDto projectDto) {
        Project project = getEntity(projectDto);

        if (project.getName() == null || project.getName().isBlank()) {
            throw new NoSuchElementException("Need project name");
        }
    }

    public void validationDescription(ProjectDto projectDto) {
        Project project = getEntity(projectDto);

        if (project.getDescription() == null || project.getDescription().isBlank()) {
            throw new NoSuchElementException("Need project description");
        }
    }

    private List<Project> findByName(String name) {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .filter(project -> {
                    System.out.println(project.getName());
                    System.out.println(name);
                    return project.getName().equals(name);
                })
                .toList();
    }

    public Project findById(long id) {
        return projectJpaRepository.getById(id);
    }

    private Project findProjectByNameAndOwnerId(String name, Long ownerId) {
        List<Project> projects = findByName(name);
        System.out.println(projects);
        for (Project project : projects) {
            if (project.getOwnerId().equals(ownerId)) {
                return project;
            }
        }
        return null;
    }

    public void validationDuplicateProjectNames(ProjectDto projectDto) {
        Project project = getEntity(projectDto);
        Project existingProject = findProjectByNameAndOwnerId(projectDto.getName(),
                project.getOwnerId());

        if (existingProject != null && existingProject.getId().equals(project.getId())) {
            throw new NoSuchElementException("This user already has a project with this name");
        }
    }

    public void validationCreateProject(ProjectDto projectDto) {
        validationName(projectDto);
        validationDescription(projectDto);
        validationDuplicateProjectNames(projectDto);
        validationDuplicateProjectNames(projectDto);
    }
}
