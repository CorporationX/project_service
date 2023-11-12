package faang.school.projectservice.service;

import faang.school.projectservice.dto.file.FileUploadResult;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exception.CoverImageException;
import faang.school.projectservice.exception.DataAlreadyExistingException;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.PrivateAccessException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filters.ProjectFilter;
import faang.school.projectservice.util.MultipartFileHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final SubProjectMapper subProjectMapper;
    private final List<ProjectFilter> filters;
    private final MultipartFileHandler multipartFileHandler;
    private final AmazonS3Service amazonS3Service;

    public ProjectDto create(ProjectDto projectDto) {
        validateNameAndDescription(projectDto);
        projectDto.setName(processTitle(projectDto.getName()));
        long ownerId = projectDto.getOwnerId();
        String projectName = projectDto.getName();

        if (projectRepository.existsByOwnerUserIdAndName(ownerId, projectName)) {
            throw new DataAlreadyExistingException(String
                    .format("User with id: %d already exist project %s", ownerId, projectName));
        }

        Project project = projectMapper.toModel(projectDto);
        LocalDateTime now = LocalDateTime.now();
        project.setCreatedAt(now);
        project.setUpdatedAt(now);
        project.setStatus(ProjectStatus.CREATED);

        if (projectDto.getVisibility() == null) {
            project.setVisibility(ProjectVisibility.PUBLIC);
        }
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public ProjectDto update(ProjectDto projectDto) {
        long projectId = projectDto.getId();
        if (projectRepository.getProjectById(projectId) == null) {
            throw new DataNotFoundException("This Project doesn't exist");
        }
        Project projectToUpdate = projectRepository.getProjectById(projectId);
        if (projectDto.getDescription() != null) {
            projectToUpdate.setDescription(projectDto.getDescription());
            projectToUpdate.setUpdatedAt(LocalDateTime.now());
        }
        if (projectDto.getStatus() != null) {
            projectToUpdate.setStatus(projectDto.getStatus());
            projectToUpdate.setUpdatedAt(LocalDateTime.now());
        }
        projectRepository.save(projectToUpdate);
        return projectMapper.toDto(projectToUpdate);
    }

    @Transactional
    public String addCoverImage(long projectId, MultipartFile file) {
        Project project = projectRepository.getProjectById(projectId);
        byte[] processedImage = multipartFileHandler.processCoverImage(file);

        String folder = String.valueOf(project.getId());
        FileUploadResult uploadResult = amazonS3Service.uploadFile(processedImage, file, folder);

        String key = uploadResult.getFileKey();

        project.setCoverImageId(key);
        return multipartFileHandler.generateCoverImageUrl(key);
    }

    public String getCoverImageBy(long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        String key = project.getCoverImageId();

        if (key == null || key.isBlank()) {
            throw new CoverImageException("There is no cover image in project with ID: " + projectId);
        }

        return multipartFileHandler.generateCoverImageUrl(key);
    }

    @Transactional
    public void deleteCoverImageBy(long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        String key = project.getCoverImageId();

        if (key == null || key.isBlank()) {
            throw new CoverImageException("Cover image in project with ID: " + projectId + ", already deleted");
        }

        amazonS3Service.deleteFile(key);
        projectRepository.deleteCoverImage(projectId);
    }

    public List<ProjectDto> getProjectsWithFilter(ProjectFilterDto projectFilterDto, long userId) {
        Stream<Project> projects = getAvailableProjectsForCurrentUser(userId).stream();

        List<ProjectFilter> listApplicableFilters = filters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto)).toList();
        for (ProjectFilter listApplicableFilter : listApplicableFilters) {
            projects = listApplicableFilter.apply(projects, projectFilterDto);
        }
        List<Project> listResult = projects.toList();
        return listResult.stream().map(projectMapper::toDto).toList();
    }

    public List<ProjectDto> getAllProjects(long userId) {
        return getAvailableProjectsForCurrentUser(userId).stream()
                .map(projectMapper::toDto)
                .toList();
    }


    public ProjectDto getProjectById(long projectId, long userId) {
        Project projectById = projectRepository.getProjectById(projectId);
        boolean isUserInPrivateProjectTeam = projectById.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers().stream()
                        .anyMatch(teamMember -> teamMember.getUserId() == userId));

        if (!isUserInPrivateProjectTeam) {
            throw new PrivateAccessException("This project is private");
        }
        return projectMapper.toDto(projectById);
    }

    private List<Project> getAvailableProjectsForCurrentUser(long userId) {
        List<Project> projects = projectRepository.findAll();
        List<Project> availableProjects = new ArrayList<>(projects.stream()
                .filter(project -> project.getVisibility() == ProjectVisibility.PUBLIC)
                .toList());
        List<Project> privateProjects = projects.stream()
                .filter(project -> project.getVisibility() == ProjectVisibility.PRIVATE)
                .toList();

        for (Project privateProject : privateProjects) {
            boolean isUserInPrivateProjectTeam = privateProject.getTeams().stream()
                    .anyMatch(team -> team.getTeamMembers().stream()
                            .anyMatch(teamMember -> teamMember.getUserId() == userId));
            if (isUserInPrivateProjectTeam) {
                availableProjects.add(privateProject);
            }
        }

        return availableProjects;
    }

    private String processTitle(String title) {
        title = title.replaceAll("[^A-Za-zА-Яа-я0-9+-/#]", " ");
        title = title.replaceAll("[\\s]+", " ");
        return title.trim().toLowerCase();
    }

    @Transactional
    public SubProjectDto createSubProject(SubProjectDto subProjectDto) {
        long parentProjectId = subProjectDto.getParentProjectId();
        String name = subProjectDto.getName();

        validateSubProjectExistence(subProjectDto);
        checkSubProjectNotPublicOnPrivateProject(parentProjectId, subProjectDto.getVisibility(), name);
        log.info("Subproject with name '{}' has successfully passed all required validations.", name);

        Project parentProject = projectRepository.getProjectById(parentProjectId);
        Project subProject = subProjectMapper.toEntity(subProjectDto);

        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);

        return subProjectMapper.toDto(projectRepository.save(subProject));
    }

    @Transactional
    public LocalDateTime updateSubProject(UpdateSubProjectDto updateSubProjectDto) {
        long subprojectId = updateSubProjectDto.getId();

        Project projectToUpdate = projectRepository.getProjectById(subprojectId);

        if (updateSubProjectDto.getStatus() == ProjectStatus.COMPLETED) {
            log.info("Initiating closure of subproject with ID: {}", subprojectId);
            return closeSubProject(updateSubProjectDto, projectToUpdate);
        }
        checkUpdatedVisibility(updateSubProjectDto, projectToUpdate);
        setAllNeededFields(updateSubProjectDto, projectToUpdate);

        projectRepository.save(projectToUpdate);

        return projectToUpdate.getUpdatedAt();
    }

    public List<SubProjectDto> getProjectChildrenWithFilter(ProjectFilterDto projectFilterDto, long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        Stream<Project> subProjectsStream = project.getChildren().stream();

        List<ProjectFilter> applicableFilters = filters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto))
                .toList();

        for (ProjectFilter filter : applicableFilters) {
            subProjectsStream = filter.apply(subProjectsStream, projectFilterDto);
        }

        return subProjectsStream.map(subProjectMapper::toDto)
                .toList();
    }

    private LocalDateTime closeSubProject(UpdateSubProjectDto updateSubProjectDto, Project projectToUpdate) {
        projectToUpdate.getChildren()
                .forEach(this::checkProjectStatusNotCompletedOrCancelled);

        setAllNeededFields(updateSubProjectDto, projectToUpdate);
        projectRepository.save(projectToUpdate);

        return projectToUpdate.getUpdatedAt();
    }

    private void checkUpdatedVisibility(UpdateSubProjectDto updateSubProjectDto, Project projectToUpdate) {
        ProjectVisibility newVisibility = updateSubProjectDto.getVisibility();
        String projectName = projectToUpdate.getName();
        Long parentProjectId = updateSubProjectDto.getParentProjectId();

        if (newVisibility == ProjectVisibility.PUBLIC) {
            if (parentProjectId != null) {
                checkSubProjectNotPublicOnPrivateProject(parentProjectId, newVisibility, projectName);
            } else {
                checkSubProjectNotPublicOnPrivateProject(projectToUpdate.getParentProject().getId(), newVisibility, projectName);
            }
        }
        if (newVisibility == ProjectVisibility.PRIVATE) {
            makeAllSubprojectPrivate(projectToUpdate);
        }
    }

    private void makeAllSubprojectPrivate(Project project) {
        long projectId = project.getId();
        List<Project> children = project.getChildren();

        log.info("Attempting to set all subprojects to private for Project ID: {}", projectId);

        if (children != null && !children.isEmpty()) {
            log.info("Project with ID: {} has {} subprojects. Attempting to set them to private.", projectId, children.size());
            children.forEach(this::makeAllSubprojectPrivate);
        }
        project.setVisibility(ProjectVisibility.PRIVATE);
        projectRepository.save(project);
    }

    private void setAllNeededFields(UpdateSubProjectDto updateSubProjectDto, Project projectToUpdate) {
        Long updatedParentProjectId = updateSubProjectDto.getParentProjectId();
        String updatedName = updateSubProjectDto.getName();
        String updatedDescriptions = updateSubProjectDto.getDescription();
        ProjectStatus updatedStatus =  updateSubProjectDto.getStatus();
        ProjectVisibility updatedVisibility = updateSubProjectDto.getVisibility();

        if (updatedParentProjectId != null) {
            Project newParentProject = projectRepository.getProjectById(updatedParentProjectId);
            projectToUpdate.setParentProject(newParentProject);
        }
        if (updatedName != null) {
            projectToUpdate.setName(updatedName);
        }
        if (updatedDescriptions != null) {
            projectToUpdate.setDescription(updatedDescriptions);
        }
        if (updatedStatus != null) {
            projectToUpdate.setStatus(updatedStatus);
        }
        if (updatedVisibility != null) {
            projectToUpdate.setVisibility(updatedVisibility);
        }
    }

    private void checkSubProjectNotPublicOnPrivateProject(long parentProjectId, ProjectVisibility subProjectVisibility, String projectName) {
        Project parentProject = projectRepository.getProjectById(parentProjectId);

        boolean isParentProjectPrivate = parentProject.getVisibility().equals(ProjectVisibility.PRIVATE);
        boolean isSubProjectPublic = subProjectVisibility.equals(ProjectVisibility.PUBLIC);

        if (isParentProjectPrivate && isSubProjectPublic) {
            throw new DataValidationException(String.format("Public SubProject: %s, cant be with a private parent Project with id: %d", projectName, parentProjectId));
        }
        log.info("Subproject with the name '{}' successfully passed validation for a non-public subproject on a private core project.", projectName);
    }

    private void validateSubProjectExistence(SubProjectDto subProjectDto) {
        long ownerId = subProjectDto.getOwnerId();
        String subprojectName = subProjectDto.getName();

        if (projectRepository.existsByOwnerUserIdAndName(ownerId, subprojectName)) {
            throw new DataAlreadyExistingException(String.format("SubProject %s is already exist", subProjectDto.getName()));
        }
        log.info("Subproject with a name '{}' passed existence validation.", subprojectName);
    }

    private void checkProjectStatusNotCompletedOrCancelled(Project project) {
        ProjectStatus projectStatus = project.getStatus();

        if (projectStatus != ProjectStatus.COMPLETED && projectStatus != ProjectStatus.CANCELLED) {
            throw new DataValidationException("Can't close project if subProject status are not complete or cancelled");
        }
    }

    private void validateNameAndDescription(ProjectDto projectDto) {
        String name = projectDto.getName();
        String description = projectDto.getDescription();
        if (name == null || name.isBlank()) {
            throw new DataValidationException("Project can't be created with empty name");
        }
        if (description == null || description.isBlank()) {
            throw new DataValidationException("Project can't be created with empty description");
        }
    }

    public Project getProjectByIdFromRepo(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public void saveProject(Project project) {
        projectRepository.save(project);
    }

    public boolean isProjectExist(Long projectId) {
        return projectRepository.existsById(projectId);
    }
}
