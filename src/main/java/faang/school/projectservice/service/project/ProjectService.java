package faang.school.projectservice.service.project;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import faang.school.projectservice.dto.resource.MultipartFileResourceDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.dto.image.ProjectImage;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.image.ImageService;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.s3.requests.S3Request;
import faang.school.projectservice.validator.project.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private static String defaultFolderResourceDelimiter = "_";
    
    //TODO: Доделать тесты
    private final S3Service s3Service;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ResourceMapper resourceMapper;
    private final ProjectValidator projectValidator;
    private final ImageService imageService;
    private final List<ProjectFilter> filters;
    @Qualifier("s3ProjectThumbnailRequest")
    private final S3Request s3ThumbnailRequest;
    private final ResourceRepository resourceRepository;
    private final TeamMemberRepository teamMemberRepository;

    public ProjectDto getProjectById(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        return projectMapper.toDto(project);
    }

    public boolean existsById(Long projectId) {
        return projectRepository.existsById(projectId);
    }

    @Transactional
    public ProjectDto create(ProjectDto projectDto) {
        projectValidator.verifyCanBeCreated(projectDto);

        projectDto.setStatus(ProjectStatus.CREATED);
        Project saved = projectRepository.save(projectMapper.toModel(projectDto));

        return projectMapper.toDto(saved);
    }

    @Transactional
    public ProjectDto update(ProjectDto projectDto) {
        projectValidator.verifyCanBeUpdated(projectDto);

        Project saved = projectRepository.save(projectMapper.toModel(projectDto));

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

        return filters.stream()
                .filter(streamFilter -> streamFilter.isApplicable(filter))
                .flatMap(streamFilter -> streamFilter.apply(projects.stream(), filter))
                .distinct()
                .map(projectMapper::toDto)
                .toList();
    }
    
    @Transactional
    public ResourceDto uploadThumbnail(Long projectId, Long teamMemberId, MultipartFile file) {
        Project project = projectRepository.getProjectById(projectId);
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId);
        
        //TODO: Спросить как избавиться от этих проверяемых ошибок? SneakyThrows? Из-за этого метод разросся
        BufferedImage thumbnail = imageService.convertImageToThumbnail(() -> {
            try {
                return new ProjectImage(imageService.convertInputStreamToImage(file.getInputStream()));
            } catch (IOException e) {
                String error = "IO exception in project thumbnail resource";
                log.error(error,e);
                throw new FileException(error);
            }
        });
        Long thumbnailSize = imageService.calculateImageSize(thumbnail);
        projectValidator.verifyStorageSizeNotExceeding(project, thumbnailSize);
        

        MultipartFileResourceDto projectThumbnailResource = MultipartFileResourceDto.builder()
            .size(thumbnailSize)
            .contentType(file.getContentType())
            .fileName(file.getOriginalFilename())
            .folderName(getDefaultProjectFolderName(project))
            .resourceInputStream(imageService.convertBufferedImageToInputStream(thumbnail))
            .build();
            
        PutObjectRequest thumbnailPutRequest = s3ThumbnailRequest.putRequest(projectThumbnailResource);
        s3Service.uploadFile(thumbnailPutRequest);
        
        String key = thumbnailPutRequest.getKey();
        Resource thumbnailProjectResource = createThumbnailProjectResource(key, projectThumbnailResource);
        thumbnailProjectResource.setProject(project);
        thumbnailProjectResource.setCreatedBy(teamMember);
        thumbnailProjectResource.setUpdatedBy(teamMember);
        
        project.addStorageSize(thumbnailSize);
        project.setCoverImageId(key);
        
        Resource savedResource = resourceRepository.save(thumbnailProjectResource);
        
        return resourceMapper.toDto(savedResource);
    }
    
    @Transactional
    public void deleteThumbnail(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        projectValidator.verifyNoThumbnail(project);
        
        DeleteObjectRequest request = s3ThumbnailRequest.deleteRequest(project.getCoverImageId());
        s3Service.deleteFile(request);
        
        project.setCoverImageId(null);
    }
    
    private String getDefaultProjectFolderName(Project project) {
        return String.format("%s%s%s", project.getId(), defaultFolderResourceDelimiter, project.getName());
    }
    
    private Resource createThumbnailProjectResource(String key, MultipartFileResourceDto projectThumbnailResource) {
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(projectThumbnailResource.getSize()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setName(projectThumbnailResource.getFileName());
        resource.setType(ResourceType.getResourceType(projectThumbnailResource.getContentType()));
        return resource;
    }
}
