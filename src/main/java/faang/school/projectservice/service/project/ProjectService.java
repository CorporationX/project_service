package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.filter.ProjectFilterDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.service.project.updater.ProjectUpdater;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.subproject.filter.SubProjectFilter;
import faang.school.projectservice.validator.project.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;
    private final List<ProjectUpdater> projectUpdaters;
    private final UserContext userContext;
    private final ProjectValidator validator;
    private final SubProjectMapper subProjectMapper;
    private final MomentService momentService;
    private final List<SubProjectFilter> subProjectFilters;
    private final S3Service s3Service;

    public ProjectDto add(ProjectDto projectDto) {
        if (projectDto.getOwnerId() == null) {
            projectDto.setOwnerId(userContext.getUserId());
        }
        validator.existsByOwnerUserIdAndName(projectDto);
        projectDto.setStatus(ProjectStatus.CREATED);
        log.info("Save project to database {}", projectDto);
        log.debug("Mapping dto to entity {}", projectDto);
        Project project = projectMapper.toEntity(projectDto);
        log.debug("Saving project to database {}", project);
        project = projectRepository.save(project);
        log.info("Project saved to database successfully {}", project);
        return projectMapper.toDto(project);
    }

    public ProjectDto update(ProjectDto projectDto) {
        try {
            Project project = projectRepository.getProjectById(projectDto.getId());
            projectUpdaters.stream().filter(filter -> filter.isApplicable(projectDto))
                    .forEach(filter -> filter.apply(project, projectDto));
            return projectMapper.toDto(projectRepository.save(project));
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public List<ProjectDto> getProjectsWithFilters(ProjectFilterDto filters) {
        long userId = userContext.getUserId();
        List<Project> publicProjects = projectRepository.findAll().stream()
                .filter(project -> project.getVisibility() == ProjectVisibility.PUBLIC).toList();
        List<Project> privateProjects = projectRepository.findAll().stream()
                .filter(project -> project.getVisibility() == ProjectVisibility.PRIVATE)
                .filter(project -> project.getTeams().stream()
                        .anyMatch(team -> team.getTeamMembers().stream()
                                .anyMatch(member -> member.getId().equals(userId)))).toList();

        List<Project> resultProjects = Stream.concat(publicProjects.stream(), privateProjects.stream()).toList();
        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(resultProjects.stream(), filters))
                .distinct()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toDto)
                .toList();
    }

    public ProjectDto getProjectById(Long projectId) {
        long userId = userContext.getUserId();
        Project project;
        try {
            project = projectRepository.getProjectById(projectId);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            boolean hasAccess = project.getTeams().stream().anyMatch(team -> team.getTeamMembers().stream()
                    .anyMatch(teamMember -> teamMember.getId().equals(userId)));
            if (!hasAccess) {
                throw new RuntimeException("User " + userId + " cannot get private project");
            }
        }
        return projectMapper.toDto(project);
    }

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

        return subProjectFilters.stream()
                .filter(subProjectFilter -> subProjectFilter.isApplicable(filterDto))
                .reduce(projects,
                        ((projectStream, filter) -> filter.apply(projectStream, filterDto)),
                        ((projectStream, projectStream2) -> projectStream2)
                )
                .filter(filteredProject -> validator.userHasAccessToProject(userId, filteredProject))
                .map(projectMapper::toDto)
                .toList();
    }

    public String addCover(Long projectId, MultipartFile coverImage) {
        Project project = validator.getProjectAfterValidateId(projectId);
        validator.validateOwnerId(project);

        String folder = project.getId() + project.getName();
        String key = s3Service.uploadFile(coverImage, folder);
        project.setCoverImageId(key);

        return key;
    }
}
