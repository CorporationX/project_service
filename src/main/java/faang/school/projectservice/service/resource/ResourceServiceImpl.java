package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.resource.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceValidator validator;
    private final TeamMemberRepository teamMemberRepository;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;
    private final ProjectRepository projectRepository;


    @Override
    @Transactional
    public ResourceDto saveResource(long projectId, MultipartFile file, long userId) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        Project project = teamMember.getTeam().getProject();

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));

        validator.validateStorage(project, newStorageSize);

        String folderName = project.getId() + project.getName();
        String key = s3Service.uploadFile(file, folderName);

        Resource resource = createResource(file, key, teamMember);
        resource = resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);

        return resourceMapper.toDto(resource);
    }

    @Override
    @Transactional(readOnly = true)
    public InputStreamResource getFile(long projectId, long userId, long resourceId) {
        teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        Resource resource = getResource(resourceId);

        return s3Service.getFile(resource.getKey());
    }

    @Override
    @Transactional
    public void deleteResource(long projectId, long userId, long resourceId) {
        TeamMember member = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        Resource resource = getResource(resourceId);

        if (resource.getStatus() == ResourceStatus.DELETED) {
            log.info("Resource with id {} is deleted already", resourceId);
            return;
        }

        validator.validateResourceOwner(resource, member);
        Project project = member.getTeam().getProject();

        s3Service.deleteFile(resource.getKey());

        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));
        projectRepository.save(project);

        resource.setStatus(ResourceStatus.DELETED);
        resource.setKey(null);
        resource.setSize(BigInteger.ZERO);
        resource.setUpdatedBy(member);

        resourceRepository.save(resource);
    }

    private Resource getResource(long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource with id %d does not exist".formatted(resourceId)));
    }


    private static Resource createResource(MultipartFile file, String key, TeamMember author) {
        return Resource.builder()
                .name(file.getName())
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .createdBy(author)
                .updatedBy(author)
                .updatedAt(LocalDateTime.now())
                .project(author.getTeam().getProject())
                .allowedRoles(new ArrayList<>(author.getRoles()))
                .build();
    }
}
