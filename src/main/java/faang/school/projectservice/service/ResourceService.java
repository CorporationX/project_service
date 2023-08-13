package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceCreateDto;
import faang.school.projectservice.exception.ProjectStorageCapacityExceededException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceCreateMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private static final int MAX_PROJECT_FILE_SIZE = 2_097_152_000;
    private final TeamMemberRepository teamMemberRepository;
    private final ResourceCreateMapper resourceCreateMapper;
    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final UserContext userContext;
    private final AmazonS3 amazonS3;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public ResourceCreateDto createResource(ResourceCreateDto resourceCreateDto, MultipartFile file) {
        Project project = projectRepository.getProjectById(resourceCreateDto.getProjectId());
        String key = resourceCreateDto.getProjectId() + "_" + project.getName() + "/" + file.getOriginalFilename();

        Resource resource = fillResource(resourceCreateDto, file, key);
        updateProjectStorageCapacity(file, project);

        createBucket(bucketName);

        uploadFile(file, key);

        return resourceCreateMapper.toResourceCreateDto(resourceRepository.save(resource));
    }

    private Resource fillResource(ResourceCreateDto resourceCreateDto, MultipartFile file, String key) {
        TeamMember teamMember = teamMemberRepository.findById(userContext.getUserId());
        Resource resource = resourceCreateMapper.toResource(resourceCreateDto);

        resource.setName(file.getOriginalFilename());
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setAllowedRoles(teamMember.getRoles());
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setCreatedBy(TeamMember.builder().id(userContext.getUserId()).build());
        resource.setProject(Project.builder().id(resourceCreateDto.getProjectId()).build());

        return resource;
    }

    public void uploadFile(MultipartFile file, String key) {
        ByteArrayInputStream content;
        try {
            content = new ByteArrayInputStream(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(bucketName, key, content, metadata);
    }

    private void createBucket(String newDirectory) {
        if (!amazonS3.doesBucketExistV2(newDirectory)) {
            amazonS3.createBucket(newDirectory);
        }
    }

    private void updateProjectStorageCapacity(MultipartFile file, Project project) {
        long newStorageCapacity = project.getStorageSize().intValue() + file.getSize();

        checkStorageCapacity(newStorageCapacity);

        project.setStorageSize(project.getStorageSize().add(BigInteger.valueOf(file.getSize())));
    }

    private static void checkStorageCapacity(long newStorageCapacity) {
        if (newStorageCapacity > MAX_PROJECT_FILE_SIZE) {
            log.error("throw ProjectStorageCapacityExceededException");
            throw new ProjectStorageCapacityExceededException("Project storage capacity exceeded");
        }
    }
}