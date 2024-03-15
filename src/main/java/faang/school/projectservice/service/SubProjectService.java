package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.CreateSubProjectDto;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.model.ProjectStatus.COMPLETED;
import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;

@Service
@RequiredArgsConstructor
public class SubProjectService {
    private final ProjectRepository projectRepository;
    private final MomentMapper momentMapper;
    private final List<ProjectFilter> projectFilters;
    private final ProjectMapper projectMapper;


    public void createSubProject(CreateSubProjectDto subProjectDto) {
        Project parentProject = projectRepository.getProjectById(subProjectDto.getParentProjectId());

        parentProject.getChildren().add(projectCreate(subProjectDto, parentProject));
        projectRepository.save(parentProject);
    }

    public void updateProject(ProjectDto projectDto) {
        Project updateProject = projectRepository.getProjectById(projectDto.getId());
        //checkCancelledChildrenProject(updateProject);
        updateProject.setUpdatedAt(LocalDateTime.now());
        if (!updateProject.getVisibility().equals(projectDto.getVisibility())) {
            updateProject.getChildren().forEach(project -> project.setVisibility(projectDto.getVisibility()));
            updateProject.setVisibility(projectDto.getVisibility());
        }
        if (!updateProject.getStatus().equals(projectDto.getStatus())) {
            updateProject.setStatus(UpdateStatusProject(updateProject, projectDto));
        }
    }

    public List<ProjectDto> getAllProjectFilter(ProjectDto projectDto, ProjectFilterDto projectFilterDto) {
        return projectFilters.stream().
                filter(projectFilter -> projectFilter.isApplicable(projectFilterDto)).
                flatMap(projectFilter -> projectFilter.apply(getChildrenProject(projectDto), projectFilterDto)).
                map(projectMapper::toDto).toList();
    }

    private Stream<Project> getChildrenProject(ProjectDto projectDto) {
        if (projectDto.getVisibility().equals(PUBLIC)) {
            return projectRepository.getProjectById(projectDto.getId()).
                    getChildren().
                    stream().
                    filter(project -> project.getVisibility().equals(PUBLIC));
        } else {
            return projectRepository.getProjectById(projectDto.getId()).
                    getChildren().
                    stream();
        }
    }

    /*private void checkCancelledChildrenProject(Project updateProject) {
        List<Project> cancelledChildrenProject = updateProject.getChildren().stream().filter(project -> project.getStatus().equals(COMPLETED)).toList();
        if(!cancelledChildrenProject.isEmpty()){
            cancelledChildrenProject.forEach(project -> {
                Moment = new Moment(project.getName());
                updateProject.setMoments(moment);
            });
        }
    }*/

    private ProjectStatus UpdateStatusProject(Project updateProject, ProjectDto projectDto) {
        if (projectDto.getStatus().equals(COMPLETED)) {
            List<Project> listChildrenProject = updateProject.getChildren().stream().
                    filter(project -> !project.getStatus().equals(COMPLETED)).
                    toList();
            if (listChildrenProject.isEmpty()) {
                return COMPLETED;
            } else {
                throw new IllegalArgumentException("Не все дочерние проекты имеют статус Complete," +
                        " перевод данного проекта в статус Complete запрещен");
            }
        }
        return projectDto.getStatus();
    }

    private Project projectCreate(CreateSubProjectDto subProjectDto, Project parentProject) {
        Project newSubProject = new Project();
        newSubProject.setVisibility(subProjectDto.getVisibility());

        if (parentProject.getVisibility() == newSubProject.getVisibility()) {
            newSubProject.setParentProject(parentProject);
            newSubProject.setName(subProjectDto.getName());
            newSubProject.setDescription(subProjectDto.getDescription());
            newSubProject.setStatus(subProjectDto.getStatus());
            newSubProject.setCreatedAt(LocalDateTime.now());
            newSubProject.setUpdatedAt(LocalDateTime.now());
        } else {
            throw new IllegalArgumentException("Не можем создать подпроект у которого" +
                    " приватность будет отличаться от родительского проекта");
        }
        return newSubProject;
    }
}