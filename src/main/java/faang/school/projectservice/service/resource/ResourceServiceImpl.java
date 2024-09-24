package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.resource.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceValidator validator;
    private final TeamMemberRepository teamMemberRepository;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;


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
        ResourceDto dto = resourceMapper.toDto(resource);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public InputStreamResource getFile(long projectId, long userId, long resourceId) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);

        Resource resource = getResource(resourceId);

        return s3Service.getFile(resource.getKey());
    }

    /*
    Удалять файл может только создатель файла или менеджер проекта. При удалении нужно удалять файл из Minio,
    в Resource обнулять key и size, изменять status на DELETED и обновлять storageSize у Project.
     */
    @Override
    @Transactional
    public void deleteResource(long projectId, long userId, long resourceId) {
        TeamMember member = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        Resource resource = getResource(resourceId);

        validator.validateResourceOwner(resource, member);

        s3Service.deleteFile(resource.getKey());

        resource.setStatus(ResourceStatus.DELETED);

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
