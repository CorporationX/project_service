package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filters.FilterProjects;
import faang.school.projectservice.validations.annotations.CanBeParentProject;
import faang.school.projectservice.validations.annotations.ChildCompleted;
import faang.school.projectservice.validations.annotations.ProjectExist;
import faang.school.projectservice.validations.annotations.ShouldBePublic;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import faang.school.projectservice.validations.validator.ProjectValidator;
import faang.school.projectservice.validations.validator.SubProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final SubProjectValidator subProjectValidator;
    private final ProjectValidator projectValidator;
    private final List<FilterProjects> projectFilters;


    @ProjectExist
    @CanBeParentProject
    @ShouldBePublic
    public Project createSubProject(Long parentId, Project subProject) {
        Optional<Project> optParentProject = projectRepository.findById(parentId);
        Project parentProject = optParentProject.get();
        subProject.setParentProject(parentProject);
        subProject.setVisibility(parentProject.getVisibility());
        subProject.setStatus(ProjectStatus.CREATED);
        subProject.setCreatedAt(LocalDateTime.now());
        return projectRepository.save(subProject);
    }
    @ProjectExist
    @ChildCompleted
    public Project updateSubProject(Long id, ProjectStatus status, ProjectVisibility visibility) {
        Optional<Project> optSubProject = projectRepository.findById(id);
        Project subProject = optSubProject.get();
        if (visibility != null) {
            subProject.setVisibility(visibility);
            updateChildVisibility(subProject, visibility);
        }
        if (status != null) {
            subProject.setStatus(status);
            subProject.setUpdatedAt(LocalDateTime.now());
        }

        return projectRepository.save(subProject);
    }

    private void updateChildVisibility(Project project, ProjectVisibility visibility) {
        List<Project> subProjects = project.getChildren();
        if (subProjects != null) {
            subProjects.forEach(subP -> subP.setVisibility(visibility));
        }
    }
    @ProjectExist
    @ShouldBePublic
    public List<Project> getSubProjects(Long id, FilterSubProjectDto filters, Integer limitList) {
        Optional<Project> parentProject = projectRepository.findById(id);
        projectValidator.doesProjectExist(parentProject);
        subProjectValidator.shouldBePublic(parentProject.get());

        Stream<Project> subProjects = parentProject.get().getChildren().stream();
        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(subProjects,
                        (stream, filter) -> filter.apply(stream, filters),
                        (stream1, stream2) -> stream1)
                .filter(project -> !Objects.equals(project.getVisibility(), ProjectVisibility.PRIVATE))
                .limit(limitList)
                .toList();
    }
}
