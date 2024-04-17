package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.event.ProjectViewEvent;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.validation.project.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final ProjectValidator projectValidator;
    private final List<ProjectFilter> projectFilters;
    private final UserContext userContext;
    private final ProjectViewEvent projectViewEvent;
    private final ResourceService resourceService;

    @Transactional
    public ProjectDto createProject(Long userId, ProjectDto projectDto) {
        projectValidator.validateProjectCreate(projectDto);
        Project project = projectMapper.toEntity(projectDto);
        setUpProjectFields(project, userId);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto updateProject(Long projectId, ProjectDto projectDto) {
        projectValidator.validateProjectUpdate(projectDto);
        Project project = projectMapper.toEntity(projectDto);
        project.setId(projectId);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> findAllProjectsByFilters(Long userId, ProjectFilterDto filters) {
        List<Project> projects = projectRepository.findAll();
        projects.removeIf(project -> isProjectPrivate(project) && !isUserMemberOrOwnerOfProject(project, userId));
        applyFilters(filters, projects);
        return projectMapper.toDto(projects);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> findAllProjects() {
        return projectMapper.toDto(projectRepository.findAll());
    }

    @Transactional
    public ProjectDto findProjectById(Long id) {
        Long userId = userContext.getUserId();
        LocalDateTime timestamp = LocalDateTime.now();
        Long projectId = id;
        projectViewEvent.publishProjectViewEvent(userId, projectId, timestamp);
        log.info("Project viewed: userId={}, projectId={}, timestamp={}", userId, projectId, timestamp);
        return projectMapper.toDto(projectRepository.getProjectById(id));
    }

    private boolean isProjectPrivate(Project project) {
        return project.getVisibility().equals(ProjectVisibility.PRIVATE);
    }

    private void applyFilters(ProjectFilterDto filters, List<Project> projects) {
        projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(projects, filters));
    }

    private boolean isUserMemberOrOwnerOfProject(Project project, Long userId) {
        return projectToMemberIds(project).contains(userId) || userId.equals(project.getOwnerId());
    }

    private List<Long> projectToMemberIds(Project project) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream().map(TeamMember::getId))
                .collect(Collectors.toList());
    }

    private void setUpProjectFields(Project project, Long userId) {
        if (project.getOwnerId() == null) {
            project.setOwnerId(userId);
        }
        project.setStatus(ProjectStatus.CREATED);
    }
    public ProjectDto addACoverToTheProject(Long projectId, MultipartFile file){
        resourceService.addACoverToTheProject(projectId,file);
        ProjectDto projectDto = findProjectById(projectId);
        projectRepository.save(projectMapper.toEntity(projectDto));
        return projectDto;
    }
}