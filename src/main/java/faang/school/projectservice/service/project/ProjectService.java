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
    public ResourceDto uploadCover(Long projectId, Long teamMemberId, MultipartFile file) {
        Project project = projectRepository.getProjectById(projectId);
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId);
        
        //TODO: Спросить как избавиться от этих проверяемых ошибок? SneakyThrows? Из-за этого метод разросся
        BufferedImage cover = imageService.convertImageToCover(() -> {
            try {
                return new ProjectImage(imageService.convertInputStreamToImage(file.getInputStream()));
            } catch (IOException e) {
                String error = "IO exception in project cover resource";
                log.error(error,e);
                throw new FileException(error);
            }
        });
        Long coverSize = imageService.calculateImageSize(cover);
        projectValidator.verifyStorageSizeNotExceeding(project, coverSize);
        

        MultipartFileResourceDto resourceDto = MultipartFileResourceDto.builder()
            .size(coverSize)
            .contentType(file.getContentType())
            .fileName(file.getOriginalFilename())
            .folderName(getDefaultProjectFolderName(project))
            .resourceInputStream(imageService.convertBufferedImageToInputStream(cover))
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
    
    private String getDefaultProjectFolderName(Project project) {
        return String.format("%s%s%s", project.getId(), defaultFolderResourceDelimiter, project.getName());
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
}
