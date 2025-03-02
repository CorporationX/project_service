package faang.school.projectservice.service;

import faang.school.projectservice.dto.event.ProjectViewEvent;
import faang.school.projectservice.dto.project.ProjectCreateRequestDto;
import faang.school.projectservice.dto.project.ProjectCreateResponseDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateRequestDto;
import faang.school.projectservice.dto.project.ProjectUpdateResponseDto;
import faang.school.projectservice.dto.project.gallery.AddImageResponseDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.validator.project.ProjectGalleryValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final TeamMemberService teamMemberService;
    private final S3Service s3Service;
    private final ProjectGalleryValidator projectGalleryValidator;
    private final List<ProjectFilter> projectFilters;
    private final KafkaTemplate<String, ProjectViewEvent> projectViewEventKafkaTemplate;

    @Value("${spring.kafka.producer.project_view.topic}")
    private String projectViewEventTopic;

    public ProjectCreateResponseDto createProject(ProjectCreateRequestDto projectCreateRequestDto) {
        Long ownerId = projectCreateRequestDto.getOwnerId();
        String projectName = projectCreateRequestDto.getName();
        if (projectRepository.existsByOwnerIdAndName(ownerId, projectName)) {
            throw new DataValidationException("User " + ownerId + " already has a project with name " + projectName);
        }

        Project project = projectMapper.toProject(projectCreateRequestDto);

        project.setStatus(ProjectStatus.CREATED);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toCreateResponseDto(savedProject);
    }

    public ProjectUpdateResponseDto updateProject(ProjectUpdateRequestDto projectUpdateRequestDto) {
        Project project = projectRepository.findById(projectUpdateRequestDto.getId())
                .orElseThrow(NoSuchElementException::new);
        projectMapper.update(project, projectUpdateRequestDto);

        Project savedProject = projectRepository.save(project);
        return projectMapper.toUpdateResponseDto(savedProject);
    }

    public List<ProjectResponseDto> getAllVisibleProjects(Long userId, ProjectFilterDto filters) {
        Stream<Project> projectStream = projectRepository.findAll()
                .stream();

        if (filters != null && CollectionUtils.isNotEmpty(projectFilters)) {
            for (ProjectFilter projectFilter : projectFilters) {
                if (projectFilter.isApplicable(filters)) {
                    projectStream = projectFilter.apply(projectStream, filters);
                }
            }
        }

        projectStream = projectStream.filter(project -> project.getVisibility() != ProjectVisibility.PRIVATE ||
                project.getOwnerId().equals(userId) ||
                projectRepository.isUserMemberOfProject(project.getId(), userId));

        return projectStream.map(projectMapper::toResponseDto)
                .toList();
    }

    public List<ProjectResponseDto> getAllProjects() {
        Stream<Project> projectStream = projectRepository.findAll()
                .stream();
        return projectStream.map(projectMapper::toResponseDto)
                .toList();
    }

    public ProjectResponseDto getProjectDtoById(Long id, Long userId) {
        Project project = getProjectById(id);
        if (!project.getOwnerId().equals(userId)) {
            ProjectViewEvent projectViewEvent = new ProjectViewEvent(project.getId(), userId, LocalDateTime.now());
            projectViewEventKafkaTemplate.send(projectViewEventTopic, projectViewEvent);
        }
        return projectMapper.toResponseDto(project);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Project with id " + id + " not found"));
    }

    public void deleteProjectById(Long id) {
        projectRepository.deleteById(id);
    }


    public AddImageResponseDto addImageInProjectGallery(Long projectId,
                                                        Long creatorId,
                                                        MultipartFile file) {
        Project project = getProjectById(projectId);
        projectGalleryValidator.validateAddingImage(project, creatorId, file);

        String folder = project.getName() + project.getId();

        Resource resource = s3Service.uploadFile(file, folder);
        TeamMember creatorMember = teamMemberService.getTeamMemberByUserAndProjectIds(creatorId, projectId);
        resource.setCreatedBy(creatorMember);
        resource.setUpdatedBy(creatorMember);
        resource.setProject(project);

        List<String> galleryFileKeys = project.getGalleryFileKeys();
        if (galleryFileKeys == null) {
            galleryFileKeys = new ArrayList<>();
            galleryFileKeys.add(resource.getKey());
            project.setGalleryFileKeys(galleryFileKeys);
        } else {
            galleryFileKeys.add(resource.getKey());
        }

        resourceRepository.save(resource);

        return resourceMapper.toAddDto(resource);
    }

    public void deleteImageFromProjectGallery(Long resourceId, Long userId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NoSuchElementException("Resource not found"));

        projectGalleryValidator.validateDeletingImage(resource.getProject(), userId);

        s3Service.deleteFile(resource.getKey());
        resourceRepository.delete(resource);
    }

    public List<String> getImagesFromProjectGallery(Long projectId, Long userId) {
        Project project = getProjectById(projectId);
        projectGalleryValidator.validateGettingGallery(project, userId);

        List<String> galleryFileKeys = project.getGalleryFileKeys();
        List<String> imageUrls = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(galleryFileKeys)) {
            for (String galleryFileKey : galleryFileKeys) {
                imageUrls.add(s3Service.getFileUrl(galleryFileKey));
            }
        }
        return imageUrls;
    }
}
