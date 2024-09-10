package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.resource.ResourcePermissionValidator;
import faang.school.projectservice.validator.resource.ResourceSizeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final ResourceSizeValidator sizeValidator;
    private final ResourceMapper resourceMapper;
    private final S3Service s3Service;
    private final UserContext userContext;
    private final TeamMemberRepository teamMemberRepository;
    private final ResourceMediaType resourceMediaType;
    private final ResourcePermissionValidator resourcePermissionValidator;

    @Transactional
    public ResourceDto addResource(Long projectId, MultipartFile file) {
        Project project = projectRepository.getProjectById(projectId);

        sizeValidator.checkNullSizeOfProject(project);
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        sizeValidator.validateSize(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + project.getName();
        String key = s3Service.putIntoBucketFolder(file, folder);

        Resource resource = createResource(file, project, key);
        Long userId = userContext.getUserId();
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        resource.setCreatedBy(teamMember);
        resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);

        return resourceMapper.toDto(resource);
    }

    public Pair<MediaType, InputStream> downloadResource(Long resourceId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        InputStream inputStream = s3Service.downloadResource(resource.getKey());
        MediaType mediaType = resourceMediaType.getMediaType(resource.getName());
        return Pair.of(mediaType, inputStream);
    }

    @Transactional
    public void deleteResource(Long resourceId) {
        Long userId = userContext.getUserId();
        Resource resource = resourcePermissionValidator.getResourceWithPermission(resourceId, userId);

        Project project = resource.getProject();
        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));

        s3Service.deleteFromBucket(resource.getKey());
        resourceRepository.deleteById(resourceId);
    }

    @Transactional
    public ResourceDto updateResource(Long resourceId, MultipartFile file) {
        Long userId = userContext.getUserId();
        Resource resourceFromDb = resourcePermissionValidator.getResourceWithPermission(resourceId, userId);
        Project project = resourceFromDb.getProject();

        BigInteger newStorageSize = project.getStorageSize()
                .add(BigInteger.valueOf(file.getSize()))
                .subtract(resourceFromDb.getSize());
        sizeValidator.validateSize(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + project.getName();
        s3Service.deleteFromBucket(resourceFromDb.getKey());

        String key = s3Service.putIntoBucketFolder(file, folder);
        Resource resource = createResource(file, project, key);

        resourceFromDb.setKey(resource.getKey());
        resourceFromDb.setName(resource.getName());
        resourceFromDb.setSize(resource.getSize());
        resourceFromDb.setUpdatedAt(resource.getUpdatedAt());
        resourceFromDb.setType(resource.getType());
        resourceRepository.save(resourceFromDb);

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);

        return resourceMapper.toDto(resource);
    }

    private Resource createResource(MultipartFile file, Project project, String key) {
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setName(file.getOriginalFilename());
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setProject(project);
        return resource;
    }
}
