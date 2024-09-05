package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.ProjectService;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final ResourceSizeValidator sizeValidator;
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
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
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setProject(project);

        Long userId = userContext.getUserId();
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        resource.setCreatedBy(teamMember);

        resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        ProjectUpdateDto projectUpdateDto = projectMapper.toUpdateDto(project, newStorageSize);
        projectService.updateProject(projectId, projectUpdateDto);
        return resourceMapper.toDto(resource);
    }

    public Pair<MediaType, InputStream> downloadResource(Long resourceId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        InputStream inputStream = s3Service.downloadResource(resource.getKey());
        MediaType mediaType = resourceMediaType.getMediaType(resource.getName());
        return Pair.of(mediaType, inputStream);
    }

    @Transactional
    public void deleteResource(Long resourceId, Long userId) {
        Resource resource = resourcePermissionValidator.getResourceWithPermission(resourceId, userId);
        s3Service.deleteFromBucket(resource.getKey());
        resourceRepository.deleteById(resourceId);
    }

    @Transactional
    public ResourceDto updateResource(Long resourceId, Long userId, MultipartFile file) {
        Resource resourceFromDb = resourcePermissionValidator.getResourceWithPermission(resourceId, userId);
        Project project = resourceFromDb.getProject();

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        sizeValidator.validateSize(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + project.getName();
        s3Service.deleteFromBucket(resourceFromDb.getKey());
        Resource resource = s3Service.uploadFile(file, folder);
        resourceFromDb.setKey(resource.getKey());
        resourceFromDb.setName(resource.getName());
        resourceFromDb.setSize(resource.getSize());
        resourceFromDb.setUpdatedAt(resource.getUpdatedAt());
        resourceFromDb.setType(resource.getType());
        resourceRepository.save(resourceFromDb);

        project.setStorageSize(newStorageSize);
        ProjectUpdateDto projectUpdateDto = projectMapper.toUpdateDto(project, newStorageSize);
        projectService.updateProject(project.getId(), projectUpdateDto);
        return resourceMapper.toDto(resource);
    }
}
