package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.subproject.filter.SubProjectFilter;
import faang.school.projectservice.validation.subproject.SubProjectValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static faang.school.projectservice.model.ProjectStatus.COMPLETED;
import static faang.school.projectservice.model.ProjectStatus.CREATED;
import static faang.school.projectservice.model.ProjectVisibility.PRIVATE;
import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;

@Service
@RequiredArgsConstructor
public class SubProjectService {
    private final SubProjectValidation subProjectValidation;
    private final ProjectRepository projectRepository;
    private final List<SubProjectFilter> projectFilters;
    private final ProjectMapper projectMapper;
    public SubProjectService(SubProjectValidation subProjectValidation,
                             ProjectRepository projectRepository,
                             ProjectMapper projectMapper,
                             @Qualifier("subProjectNameFilter") SubProjectFilter subProjectNameFilter,
                             @Qualifier("subProjectStatusFilter") SubProjectFilter subProjectStatusFilter){
        this.subProjectValidation = subProjectValidation;
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        projectFilters = List.of(subProjectNameFilter,subProjectStatusFilter);
    }


    public void createSubProject(CreateSubProjectDto subProjectDto) {
        subProjectValidation.toCreate(subProjectDto);

        Project parentProject = projectRepository.getProjectById(subProjectDto.getParentProjectId());

        if (PRIVATE.equals(parentProject.getVisibility()) && PUBLIC.equals(subProjectDto.getVisibility())) {
            throw new IllegalArgumentException("Нельзя создать публичный подпроект для приватного родительского проекта ");
        } else {
            if (parentProject.getChildren() == null) {
                parentProject.setChildren(List.of(projectCreate(subProjectDto, parentProject)));
            } else {
                List<Project> childrenProject = parentProject.getChildren().stream().collect(Collectors.toList());
                childrenProject.add(projectCreate(subProjectDto, parentProject));
                parentProject.setChildren(childrenProject);
            }
            projectRepository.save(parentProject);
        }
    }

    public Moment updateSubProject(SubProjectDto projectDto) {
        subProjectValidation.checkProjectDto(projectDto);
        Project updateProject = projectRepository.getProjectById(projectDto.getId());
        List<Project> childrenProjects = updateProject.getChildren();

        if (PUBLIC.equals(updateProject.getVisibility()) && PRIVATE.equals(projectDto.getVisibility()) &&
                childrenProjects != null) {
            childrenProjects.forEach(project -> project.setVisibility(projectDto.getVisibility()));
            updateProject.setVisibility(projectDto.getVisibility());
        } else {
            updateProject.setVisibility(projectDto.getVisibility());
        }

        if (!updateProject.getStatus().equals(projectDto.getStatus()) && COMPLETED.equals(projectDto.getStatus())) {
            updateProject.setStatus(updateStatusProject(updateProject));
        } else {
            updateProject.setStatus(projectDto.getStatus());
        }

        updateProject.setName(projectDto.getName());
        updateProject.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(updateProject);

        if (childrenProjects != null) {
            return momentForTheProject(childrenProjects, updateProject);
        }
        return null;
    }

    public List<ProjectDto> getAllProjectFilter(SubProjectDto projectDto, ProjectFilterDto projectFilterDto) {
        subProjectValidation.checkProjectDto(projectDto);
        return projectFilters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto))
                .flatMap(projectFilter -> projectFilter.apply(getChildrenProject(projectDto), projectFilterDto))
                .map(projectMapper::toDto).toList();
    }

    private Stream<Project> getChildrenProject(SubProjectDto projectDto) {
        if (PUBLIC.equals(projectDto.getVisibility())) {
            return projectRepository.getProjectById(projectDto.getId()).
                    getChildren().
                    stream().
                    filter(project -> PUBLIC.equals(project.getVisibility()));
        } else {
            return projectRepository.getProjectById(projectDto.getId()).
                    getChildren().
                    stream();
        }
    }

    private Moment momentForTheProject(List<Project> childrenProjects, Project updateProject) {
        int subprojects = childrenProjects.size();
        List<Project> completeChildrenProject = childrenProjects.stream().filter(project -> COMPLETED.equals(project.getStatus())).toList();
        if (completeChildrenProject.size() == subprojects) {
            return Moment.builder()
                    .name(updateProject.getName())
                    .description(updateProject.getDescription())
                    .projects(completeChildrenProject)
                    .build();
        }
        return null;
    }

    private ProjectStatus updateStatusProject(Project updateProject) {
        List<Project> listChildrenProject = updateProject.getChildren();
        if (listChildrenProject == null) {
            return COMPLETED;
        } else {
            List<Project> listChildrenProjectComplete = listChildrenProject.stream().
                    filter(project -> COMPLETED.equals(project.getStatus())).
                    toList();
            if (listChildrenProjectComplete.size() == listChildrenProject.size()) {
                return COMPLETED;
            } else {
                throw new IllegalArgumentException("Не все дочерние проекты имеют статус Complete," +
                        " перевод данного проекта в статус Complete запрещен");
            }
        }
    }

    private Project projectCreate(CreateSubProjectDto subProjectDto, Project parentProject) {
        Project newSubProject = new Project();
        newSubProject.setVisibility(subProjectDto.getVisibility());

        if (parentProject.getVisibility() == newSubProject.getVisibility()) {
            newSubProject.setParentProject(parentProject);
            newSubProject.setName(subProjectDto.getName());
            newSubProject.setDescription(subProjectDto.getDescription());
            newSubProject.setStatus(CREATED);
            newSubProject.setCreatedAt(LocalDateTime.now());
            newSubProject.setUpdatedAt(LocalDateTime.now());
        } else {
            throw new IllegalArgumentException("Не можем создать подпроект у которого приватность будет отличаться от родительского проекта");
        }
        return newSubProject;
    }
}