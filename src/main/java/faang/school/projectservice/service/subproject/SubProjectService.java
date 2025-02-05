package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.exeption.EntityNotFoundException;
import faang.school.projectservice.exeption.ProjectNotClosableException;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filters.subproject.SubProjectFilter;
import faang.school.projectservice.service.moment.MomentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubProjectService {
    private final ProjectRepository projectRepository;
    private final MomentService momentService;
    private final List<SubProjectFilter> subProjectFilters;

    @Transactional
    public void createSubProject(Long parentProjectId, Project subProject) {
        Project project = getProject(parentProjectId);

        if (Objects.equals(project.getVisibility(), ProjectVisibility.PUBLIC)) {
            subProject.setVisibility(ProjectVisibility.PUBLIC);

        } else {
            subProject.setVisibility(ProjectVisibility.PRIVATE);
        }

        subProject.setParentProject(project);
        subProject.setCreatedAt(LocalDateTime.now());
        subProject.setUpdatedAt(LocalDateTime.now());
        subProject.setStatus(ProjectStatus.CREATED);
        subProject.setStages(project.getStages());
        subProject.setVacancies(project.getVacancies());

        projectRepository.save(subProject);
    }

    @Transactional
    public void updateSubProject(Long projectId, Project subProject) {
        Project existingSubProject = getProject(projectId);

        validateSubProjectStatus(projectId, subProject, existingSubProject);

        validateSubProjectVisibility(existingSubProject);

        mapSubProjectToUpdate(subProject, existingSubProject);

        projectRepository.save(existingSubProject);
    }

    @Transactional(readOnly = true)
    public List<Project> getSubProjectsByProjectId(Long parentProjectId, SubProjectFilterDto filters) {

        List<Project> subProjects = projectRepository
                .findByParentId(parentProjectId);

        log.info("SubProjects with parent id {} before filtering", parentProjectId);
        return filterGoals(subProjects, filters);
    }

    private List<Project> filterGoals(List<Project> subProjects, SubProjectFilterDto filters) {
        Stream<Project> streamSubProjects = subProjects.stream();

        return subProjectFilters.stream()
                .filter(filter -> filter
                        .isApplicable(filters))
                .reduce(streamSubProjects, (currentStream, filter) -> filter
                        .apply(currentStream, filters), (s1, s2) -> s1)
                .collect(Collectors.toList());
    }


    private void mapSubProjectToUpdate(Project subProject, Project existingProject) {
        existingProject.setDescription(subProject.getDescription());
        existingProject.setStatus(subProject.getStatus());
        existingProject.setUpdatedAt(LocalDateTime.now());
        existingProject.setVisibility(subProject.getVisibility());
    }

    private Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    private void validateSubProjectStatus(Long projectId, Project subProject, Project existingSubProject) {
        if (!Objects.equals(subProject.getStatus(), ProjectStatus.COMPLETED)) {
            existingSubProject.setStatus(subProject.getStatus());
        } else {
            List<Project> completedSubProjects = subProject.getChildren().stream()
                    .filter(subProj -> Objects.equals(subProj.getStatus(), ProjectStatus.COMPLETED))
                    .collect(Collectors.toList());

            if (completedSubProjects.size() == existingSubProject.getChildren().size()) {
                Moment moment = momentService.create();
                momentService.getAllByProjectId(projectId).add(moment);
            } else {
                throw new ProjectNotClosableException("Cannot close the project because not all subprojects are completed");
            }
        }
    }

    private void validateSubProjectVisibility(Project existingSubProject) {
        if (Objects.equals(existingSubProject.getVisibility(), ProjectVisibility.PRIVATE)) {
            List<Project> children = existingSubProject.getChildren();
            children.forEach(proj -> proj.setVisibility(ProjectVisibility.PRIVATE));
            projectRepository.saveAll(children);
        }
    }
}