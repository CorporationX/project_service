package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.service.subproject.filter.SubProjectFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectServiceValidator validator;
    private final SubProjectMapper subProjectMapper;
    private final ProjectMapper projectMapper;
    private final MomentService momentService;
    private final List<SubProjectFilter> filters;
    private final UserContext userContext;

    public ProjectDto createSubProject(CreateSubProjectDto subProjectDto) {
        validator.validateSubProjectForCreate(subProjectDto);

        Project parentProject = validator.getProjectAfterValidateId(subProjectDto.getParentProjectId());
        validator.validateParentProjectForCreateSubProject(parentProject, subProjectDto);

        Project subProject = subProjectMapper.toEntity(subProjectDto);
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        projectRepository.save(subProject);

        log.info("Subproject created successfully by user={} with id={}",
                subProject.getOwnerId(), subProject.getId());

        return projectMapper.toDto(subProject);
    }

    public ProjectDto updateSubProject(Long subProjectId, UpdateSubProjectDto updateDto) {
        Project subProject = validator.getProjectAfterValidateId(subProjectId);
        validator.validateOwnerId(subProject);
        validator.validateSubProjectForUpdate(subProject, updateDto);
        if (validator.readyToNewMoment(subProject, updateDto.getStatus())) {
            momentService.addMomentByName(subProject, "All subprojects finished");
        }

        subProjectMapper.updateEntity(updateDto, subProject);
        subProject.setUpdatedAt(LocalDateTime.now());
        Project updatedProject = projectRepository.save(subProject);

        log.info("Subproject with id={} successfully updated", updatedProject.getId());

        return projectMapper.toDto(updatedProject);
    }

    public List<ProjectDto> getFilteredSubProjects(Long projectId, FilterSubProjectDto filterDto) {
        Project project = validator.getProjectAfterValidateId(projectId);
        Long userId = userContext.getUserId();
        Stream<Project> projects = project.getChildren().stream();

        return filters.stream()
                .filter(subProjectFilter -> subProjectFilter.isApplicable(filterDto))
                .reduce(projects,
                        ((projectStream, filter) -> filter.apply(projectStream, filterDto)),
                        ((projectStream, projectStream2) -> projectStream2)
                )
                .filter(filteredProject -> validator.userHasAccessToProject(userId, filteredProject))
                .map(projectMapper::toDto)
                .toList();
    }
}
