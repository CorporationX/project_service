package faang.school.projectservice.service.project;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import faang.school.projectservice.dto.image.ProjectImage;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.resource.MultipartFileResourceDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.s3.requests.S3Request;
import faang.school.projectservice.validator.project.ProjectValidator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private static final String ALL_SUBPROJECTS_DONE_MOMENT_NAME = "All subprojects completed";
    private static final String DEFAULT_FOLDER_RESOURCE_DELIMITER = "_";
    
    //TODO: Доделать тесты (RickHammerson/Radmir)
    private final S3Service s3Service;
    private final ProjectRepository projectRepository;
    private final MomentService momentService;
    private final ProjectValidator validator;
    private final ProjectMapper projectMapper;
    private final ResourceMapper resourceMapper;
    private final ProjectValidator projectValidator;
    private final List<ProjectFilter> filters;
    @Qualifier("s3ProjectCoverRequest")
    private final S3Request s3CoverRequest;
    private final ResourceRepository resourceRepository;
    private final TeamMemberRepository teamMemberRepository;

    public boolean existsById(Long projectId) {
        return projectRepository.existsById(projectId);
    }

    @Transactional
    public ProjectDto create(ProjectDto projectDto) {
        projectValidator.verifyCanBeCreated(projectDto);

        projectDto.setStatus(ProjectStatus.CREATED);
        Project projectToBeCreated = projectMapper.toModel(projectDto);
        fillProject(projectToBeCreated, projectDto);

        Project saved = projectRepository.save(projectToBeCreated);
        return projectMapper.toDto(saved);
    }

    @Transactional
    public ProjectDto update(ProjectDto projectDto) {
        projectValidator.verifyCanBeUpdated(projectDto);

        Project projectToBeUpdated = projectMapper.toModel(projectDto);
        manageFinishedProject(projectToBeUpdated);
        manageVisibilityChange(projectToBeUpdated);

        Project saved = projectRepository.save(projectToBeUpdated);
        fillProject(saved, projectDto);
        return projectMapper.toDto(saved);
    }

    public ProjectDto getById(Long id) {
        Project project = projectRepository.getProjectById(id);

        return projectMapper.toDto(project);
    }

    public List<ProjectDto> getAll() {
        List<Project> projects = projectRepository.findAll();

        return projectMapper.toDto(projects);
    }

    public List<ProjectDto> search(ProjectFilterDto filter) {
        List<Project> projects = projectRepository.findAll();

        return filterProjects(filter, projects);
    }
    
    @SneakyThrows
    @Transactional
    public ResourceDto uploadCover(Long projectId, Long teamMemberId, MultipartFile file){
        Project project = projectRepository.getProjectById(projectId);
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId);
        
        ProjectImage projectImage = new ProjectImage(file.getInputStream());
        Long coverSize = projectImage.calculateCoverSize();
        projectValidator.verifyStorageSizeNotExceeding(project, coverSize);
        
        MultipartFileResourceDto resourceDto = MultipartFileResourceDto.builder()
            .size(coverSize)
            .contentType(file.getContentType())
            .fileName(file.getOriginalFilename())
            .folderName(getDefaultProjectFolderName(project))
            .resourceInputStream(projectImage.getCoverInputStream())
            .build();
        
        PutObjectRequest projectCoverPutRequest = s3CoverRequest.putRequest(resourceDto);
        s3Service.uploadFile(projectCoverPutRequest);
        
        String key = projectCoverPutRequest.getKey();
        Resource projectCoverResource = createCoverProjectResource(key, resourceDto);
        projectCoverResource.setProject(project);
        projectCoverResource.setCreatedBy(teamMember);
        projectCoverResource.setUpdatedBy(teamMember);
        
        project.addStorageSize(coverSize);
        project.setCoverImageId(key);
        
        Resource savedResource = resourceRepository.save(projectCoverResource);
        
        return resourceMapper.toDto(savedResource);
    }
    
    @Transactional
    public void deleteCover(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        projectValidator.verifyNoCover(project);
        
        DeleteObjectRequest request = s3CoverRequest.deleteRequest(project.getCoverImageId());
        s3Service.deleteFile(request);
        
        project.setCoverImageId(null);
    }

    private List<ProjectDto> filterProjects(ProjectFilterDto filter, List<Project> projects) {
        return filters.stream()
                .filter(streamFilter -> streamFilter.isApplicable(filter))
                .flatMap(streamFilter -> streamFilter.apply(projects.stream(), filter))
                .distinct()
                .map(projectMapper::toDto)
                .toList();
    }
    
    private String getDefaultProjectFolderName(Project project) {
        return String.format("%s%s%s", project.getId(), DEFAULT_FOLDER_RESOURCE_DELIMITER, project.getName());
    }
    
    private Resource createCoverProjectResource(String key, MultipartFileResourceDto multipartFileResourceDto) {
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(multipartFileResourceDto.getSize()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setName(multipartFileResourceDto.getFileName());
        resource.setType(ResourceType.getResourceType(multipartFileResourceDto.getContentType()));
        return resource;
    }

    public List<ProjectDto> searchSubprojects(Long parentProjectId, ProjectFilterDto filter) {
        List<Project> projects = projectRepository.getProjectById(parentProjectId).getChildren();

        return filterProjects(filter, projects);
    }

    private void manageVisibilityChange(Project projectToBeUpdated) {
        if (!projectToBeUpdated.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            return;
        }

        projectToBeUpdated.getChildren().forEach(subproject -> {
            subproject.setVisibility(ProjectVisibility.PRIVATE);

            manageVisibilityChange(subproject);
        });
    }

    private void manageFinishedProject(Project projectToBeUpdated) {
        if (!projectToBeUpdated.isStatusFinished()) {
            return;
        }

        validator.verifySubprojectStatus(projectToBeUpdated);

        Project parent = projectToBeUpdated.getParentProject();
        if (parent == null) {
            return;
        }

        long finishedSubprojectsCount = parent.getChildren().stream().filter(Project::isStatusFinished).count();

        if (finishedSubprojectsCount != parent.getChildren().size()) {
            return;
        }

        List<Long> projectMembers = parent.getTeams().stream().flatMap(team -> team.getTeamMembers().stream()).map(TeamMember::getId).toList();

        MomentDto allSubprojectsDoneMoment = MomentDto.builder().name(ALL_SUBPROJECTS_DONE_MOMENT_NAME).projects(List.of(parent.getId())).userIds(projectMembers).build();
        momentService.createMoment(allSubprojectsDoneMoment);
    }

    private void fillProject(Project saved, ProjectDto projectDto) {
        Long parentProjectId = projectDto.getParentProjectId();
        if (parentProjectId != null) {
            saved.setParentProject(projectRepository.getProjectById(parentProjectId));
        }

        List<@NotNull Long> children = projectDto.getChildren();
        if (children != null && !children.isEmpty()) {
            saved.setChildren(projectRepository.findAllByIds(children));
        }
    }
}
