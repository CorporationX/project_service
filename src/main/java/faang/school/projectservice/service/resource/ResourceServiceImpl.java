package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.s3.S3ServiceImpl;
import faang.school.projectservice.validation.resource.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final S3ServiceImpl s3Service;
    private final ResourceMapper resourceMapper;
    private final ResourceValidator resourceValidator;

    @Value("${project.avatar.fileSizeMax:5}")
    private long projectAvatarFileSizeMax;

    @Override
    public ResourceDto addProjectAvatar(long projectId, MultipartFile file) {
        Project project = projectRepository.getByIdOrThrow(projectId);
        log.debug("Got project by id %d".formatted(projectId));

        BigInteger projectNewStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        resourceValidator.validateFileSize(file, projectAvatarFileSizeMax);
        resourceValidator.validateProjectStorageSize(project, file);
        MultipartFile validatedFile = resourceValidator.validateImageDimensions(file);

        String folder = projectId + project.getName();

        Resource resource = s3Service.uploadFile(validatedFile, folder);
        resource.setProject(project);

        Resource savedResource = resourceRepository.save(resource);
        log.info("Resource {} has been saved", savedResource.getId());

        project.setStorageSize(projectNewStorageSize);
        project.setCoverImageId(String.valueOf(savedResource.getId()));
        projectRepository.save(project);
        log.info("Project {} was updated", projectId);

        return resourceMapper.toResourceDto(savedResource);
    }
}