package faang.school.projectservice.service.resource;

import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.service.s3.AmazonS3Service;
import faang.school.projectservice.validator.resource.ResourceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final AmazonS3Service amazonS3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final TeamMemberJpaRepository teamMemberRepository;
    private final ResourceValidator resourceValidator;

    @Transactional
    public ResourceDto saveFile(Long userId, Long projectId, MultipartFile file) {
        TeamMember teamMember = getTeamMember(userId, projectId);
        Project project = teamMember.getTeam().getProject();

        resourceValidator.validateMaxFreeStorageSize(project, file.getSize());

        String path = "project/" + projectId + "/";
        String key = amazonS3Service.uploadFile(path, file);

        Resource resource = buildResource(file, teamMember, key);
        resource = resourceRepository.save(resource);

        return resourceMapper.toDto(resource);
    }

    @Transactional(readOnly = true)
    public S3ObjectInputStream getFile(Long userId, Long projectId, Long resourceId) {
        getTeamMember(userId, projectId);

        return amazonS3Service.downloadFile(getResource(resourceId).getKey());
    }

    @Transactional
    public void deleteFile(Long userId, Long projectId, Long resourceId) {
        Resource resource = getResource(resourceId);
        TeamMember teamMember = getTeamMember(userId, projectId);

        resourceValidator.validateDeletePermission(teamMember, resource);

        amazonS3Service.deleteFile(resource.getKey());

        resource.setKey(null);
        resource.setSize(null);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(teamMember);

        Project project = resource.getProject();
        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));

        resourceRepository.save(resource);
    }

    private Resource getResource(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Not found resource with this id: " + resourceId));
    }

    private TeamMember getTeamMember(Long userId, Long projectId) {
        return teamMemberRepository.findOptionalByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new NotFoundException("Not found teamMember with this userId: " + userId + " and projectId: " + projectId));
    }

    private Resource buildResource(MultipartFile file, TeamMember teamMember, String key) {
        return Resource.builder()
                .key(key)
                .name(file.getName())
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(teamMember.getRoles())
                .type(ResourceType.valueOf(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .createdAt(LocalDateTime.now())
                .project(teamMember.getTeam().getProject())
                .build();
    }
}
