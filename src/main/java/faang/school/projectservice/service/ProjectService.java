package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.filter.subProjectFilter.SubProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.project.ProjectFilter;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.SubProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static faang.school.projectservice.model.ProjectStatus.*;
import static faang.school.projectservice.model.ProjectVisibility.PRIVATE;
import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;

@Service
@RequiredArgsConstructor
public class ProjectService {


    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    private final MomentService momentService;
    private final List<SubProjectFilter> filters;

    private final SubProjectValidator validator;
    private final ProjectValidator projectValidator;
    private final List<ProjectFilter> projectFilters;

    public ProjectDto create(ProjectDto projectDto) {
        projectValidator.checkExistProject(projectDto);

        Project project = projectMapper.toEntity(projectDto);
        project.setStatus(ProjectStatus.CREATED);

        return projectMapper.toDto(projectRepository.save(project));

    }

    public ProjectDto update(ProjectDto projectDto) {
        Project project = projectMapper.toEntity(projectDto);

        return projectMapper.toDto(projectRepository.save(project));
    }

    public List<Project> getProjectsByFilter(ProjectFilterDto filterDto) {
        return projectFilters.stream().filter(projectFilter -> projectFilter.isApplicable(filterDto))
                .flatMap(projectFilter -> projectFilter.apply(projectRepository.findAll(), filterDto))
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC))
                .collect(Collectors.toList());
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    @Transactional
    public ProjectDto createSubProject(Long parentId, CreateSubProjectDto subProjectDto) {
        Project parent = projectRepository.getProjectById(parentId);

        validator.validateSubProjectVisibility(parent, subProjectDto);

        Project projectToCreate = projectMapper.toEntity(subProjectDto);

        parent.getChildren().add(projectToCreate);

        projectToCreate.setParentProject(parent);
        projectToCreate.setStatus(CREATED);
        projectRepository.save(projectToCreate);
        return projectMapper.toDto(projectToCreate);
    }

    @Transactional
    public ProjectDto updateSubProject(long projectId, ProjectDto projectDto) {
        Project projectToUpdate = projectRepository.getProjectById(projectId);
        Project parent = projectRepository.getProjectById(projectToUpdate.getParentProject().getId());

        if (!projectToUpdate.getVisibility().equals(projectDto.getVisibility())) {
            checkAndChangeVisibility(parent, projectToUpdate, projectDto);
        }

        ProjectStatus updatedStatus = checkAndChangeStatus(projectToUpdate, projectDto);

        projectToUpdate.setName(projectDto.getName());
        projectToUpdate.setDescription(projectDto.getDescription());

        if (updatedStatus.equals(COMPLETED) && validator.isAllSubProjectsCompleted(parent)) {
            MomentDto momentDto = MomentDto.builder()
                    .title("Проект со всеми подзадачами выполенен")
                    .projectId(projectId)
                    .build();
            momentService.createMoment(momentDto);
        }
        return projectMapper.toDto(projectToUpdate);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getSubProjects(Long projectId, SubProjectFilterDto filterDto) {
        Project project = projectRepository.getProjectById(projectId);

        if (project.getVisibility().equals(PRIVATE)) {
            throw new IllegalArgumentException("Невозможно отобразить привытный проект");
        }
        List<ProjectDto> children = project.getChildren()
                .stream()
                .filter(pr -> pr.getVisibility().equals(PUBLIC))
                .map(projectMapper::toDto)
                .toList();

        applyFilter(children, filterDto);
        return children;
    }

    private void applyFilter(List<ProjectDto> projects, SubProjectFilterDto filterDto) {
        filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(projects, filterDto));
    }

    private void checkAndChangeVisibility(Project parent, Project projectToUpdate, ProjectDto projectDto) {
        validator.validCorrectVisibility(projectDto, parent);

        projectToUpdate.setVisibility(projectDto.getVisibility());
        if (projectDto.getVisibility().equals(PRIVATE) && projectDto.getChildrenIds() != null &&
                !projectToUpdate.getChildren().isEmpty()) {
            projectToUpdate.getChildren().forEach(pr -> pr.setVisibility(projectDto.getVisibility()));
        }
    }

    private ProjectStatus checkAndChangeStatus(Project project, ProjectDto projectDto) {
        ProjectStatus updatedStatus = projectDto.getStatus();

        if (updatedStatus.equals(ON_HOLD) || updatedStatus.equals(CANCELLED)) {
            project.getChildren().forEach(pr -> pr.setStatus(updatedStatus));
            project.setStatus(updatedStatus);
        }

        return updatedStatus;
    }
}