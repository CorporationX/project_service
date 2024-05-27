package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.AmazonS3Service;
import faang.school.projectservice.validation.resource.ProjectResourceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class ProjectResourceServiceImpl implements ProjectResourceService {

    private final AmazonS3Service amazonS3Service;
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final ProjectResourceValidator projectResourceValidator;
    private final ResourceMapper resourceMapper;

    @Override
    public ResourceDto saveFile(long projectId, MultipartFile file) {

        Project project = projectRepository.getProjectById(projectId);

        projectResourceValidator.validateMaxStorageSize(project, file.getSize());

        String key = amazonS3Service.uploadFile(file);

        Resource resource = Resource.builder()
                .key(key)
                .name(file.getOriginalFilename())
                .size(BigInteger.valueOf(file.getSize()))
                .type(ResourceType.valueOf(file.getContentType()))
                .project(project)
                .status(ResourceStatus.ACTIVE)
                //...
                .build();

        resource = resourceRepository.save(resource);

        return resourceMapper.toDto(resource);
    }

    @Override
    public InputStream getFile(long projectId) {
        return null;
    }

    @Override
    public String deleteFile(long userId, String key) {
        return "";
    }
}
