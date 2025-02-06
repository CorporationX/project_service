package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.ResourceService;
import faang.school.projectservice.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final static String FOLDER_PREFIX = "project_";

    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;
    private final ResourceValidator resourceValidator;
    private final ProjectValidator projectValidator;
    private final ProjectService projectService;

    @Transactional
    @Override
    public ResourceResponseDto addResource(Long userId, Long projectId, MultipartFile file) {
        projectValidator.validateUserInProject(userId, projectId);
        resourceValidator.validateResourcesOversize(projectId);
        TeamMember teamMember = getTeamMember(userId);
        Project project = projectService.getProject(projectId);
        String folder = FOLDER_PREFIX + projectId;
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        Resource resource = Resource.builder()
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .status(ResourceStatus.ACTIVE)
                .type(ResourceType.getResourceType(file.getContentType()))
                .name(file.getOriginalFilename())
                .project(project)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .build();
        resource = resourceRepository.save(resource);
        s3Service.uploadFile(file, key);
        return resourceMapper.toResourceResponseDto(resource);
    }

    @Override
    public InputStream downloadResource(Long userId, Long resourceId) {
        Resource resource = getResourceById(resourceId);
        Long projectId = resource.getProject().getId();
        resourceValidator.validateUserCanDownloadFromProject(userId, projectId);
        return s3Service.downloadFile(resource.getKey());
    }

    @Transactional
    @Override
    public void deleteResource(Long userId, Long resourceId) {
        Resource resource = getResourceById(resourceId);
        projectValidator.validateUserInProject(userId, resource.getProject().getId());
        resourceRepository.deleteById(resourceId);
        String key = resource.getKey();
        s3Service.deleteFile(key);
    }

    private Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId).orElseThrow();
    }

    private TeamMember getTeamMember(Long userId) {
        return TeamMember.builder()
                .id(userId)
                .build();
    }

}
