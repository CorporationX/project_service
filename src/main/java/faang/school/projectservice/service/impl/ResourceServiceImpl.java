package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
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
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;
    private final ResourceValidator resourceValidator;
    private final ProjectService projectService;

    @Transactional
    @Override
    public ResourceResponseDto addResource(Long userId, Long projectId, MultipartFile file) {
        resourceValidator.validateUserInProject(userId, projectId);
        resourceValidator.validateResourcesOversize(projectId);
        Project project = projectService.getProject(projectId);
        String folder = "project_" + projectId;
        Resource resource = s3Service.uploadFile(file, folder);
        TeamMember teamMember = getTeamMember(userId);
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource = resourceRepository.save(resource);
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
        resourceValidator.validateUserInProject(userId, resource.getProject().getId());
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
