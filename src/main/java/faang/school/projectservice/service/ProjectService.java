package faang.school.projectservice.service;


import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.*;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.dto.resource.S3ObjectDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ProjectAlreadyExistsException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.filter.project.ProjectFilter;
import faang.school.projectservice.util.FileUtils;
import faang.school.projectservice.util.PdfGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectService {

    private final UserServiceClient userServiceClient;
    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final List<ProjectFilter> projectFilters;
    private final ResourceRepository resourceRepository;
    private final S3Service s3service;
    private final ResourceMapper resourceMapper;
    private final PdfGenerator pdfGenerator;

    @Value("${services.s3.bucketName}")
    private String bucketName;


    public Project getProject(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project with ID " + projectId + " not found"));
    }

    public ProjectResponse createProject(CreateProjectRequest createProjectRequest) {
        validUser(createProjectRequest.ownerId());
        Project project = projectMapper.toEntity(createProjectRequest);
        project.setStatus(ProjectStatus.CREATED);

        if (projectRepository.existsByOwnerIdAndName(createProjectRequest.ownerId(), project.getName())) {
            throw new ProjectAlreadyExistsException("Проект с именем " + project.getName() + " уже существует");
        }

        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    public ProjectResponse updateProject(UpdateProjectRequest updateProjectRequest) {
        if (updateProjectRequest.ownerId() != null) {
            validUser(updateProjectRequest.ownerId());
        }

        Project project = getProjectById(updateProjectRequest.id());
        projectMapper.updateProjectFromDto(updateProjectRequest, project);
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    public List<ProjectResponse> filterProjects(Long userId, FilterProjectRequest filterProjectRequest) {
        validUser(userId);

        Stream<Project> projects = projectRepository.findAll().stream();
        for (ProjectFilter projectFilter : projectFilters) {
            projects = projectFilter.filter(projects, filterProjectRequest);
        }

        Stream<Project> filteredProjects = getVisibleProjectsForUser(projects, userId);

        return filteredProjects
                .map(projectMapper::toProjectResponse)
                .collect(Collectors.toList());
    }

    public void deleteProject(@Valid DeleteProjectRequest deleteProjectRequest) {
        validUser(deleteProjectRequest.ownerId());
        Project project = getProjectById(deleteProjectRequest.id());
        if (!project.getOwnerId().equals(deleteProjectRequest.ownerId())) {
            throw new IllegalArgumentException("Пользователь не владелец проекта");
        }
        projectRepository.delete(project);
    }

    public List<ProjectResponse> getAllProjects(Long userId) {
        validUser(userId);

        return getVisibleProjectsForUser(projectRepository.findAll().stream(), userId)
                .map(project -> projectMapper.toProjectResponse(project))
                .toList();
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    private void validUser(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        if (userDto == null || userDto.id() == null) {
            throw new EntityNotFoundException("User not found");
        }
    }

    private Stream<Project> getVisibleProjectsForUser(Stream<Project> projects, long userId) {
        return projects.filter(project ->
                project.getVisibility() == ProjectVisibility.PUBLIC
                        || project.getOwnerId().equals(userId)
                        || isUserInProjectTeams(project, userId) // Пользователь входит в команды проекта
        );
    }

    private boolean isUserInProjectTeams(Project project, long userId) {
        return Optional.ofNullable(project.getTeams())
                .orElse(List.of())
                .stream()
                .flatMap(team ->
                        Optional.ofNullable(team.getTeamMembers())
                                .orElse(List.of())
                                .stream()
                )
                .anyMatch(teamMember -> teamMember.getUserId().equals(userId));
    }

    public ResourceDto createPdfFromProject(Long projectId) {

        Project project = getProject(projectId);
        List<Team> teams = project.getTeams();
        List<Task> tasks = project.getTasks();

        var pdfData = pdfGenerator.generateProjectPresentationNew(project, teams, tasks);
        MultipartFile multipartFile = FileUtils.convertToMultipartFile(pdfData, "project-pdf", ResourceType.PDF.name());

        Resource resource = s3service.uploadFile(multipartFile, "pdf");

        resource = resourceRepository.save(resource);
        return resourceMapper.toEntity(resource);
    }

    public S3ObjectDto downloadPdf(Long resourceId) {
        var resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resources Not found !!!"));

        String fileName = resource.getKey().substring(resource.getKey().lastIndexOf("/") + 1);
        S3Object object = s3service.getObject(resource.getKey());

        return new S3ObjectDto(fileName, object, ResourceType.PDF.name());
    }


}
