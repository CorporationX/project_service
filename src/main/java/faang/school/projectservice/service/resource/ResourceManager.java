package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.*;
import faang.school.projectservice.service.s3manager.S3Manager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

@Slf4j
@Component
@AllArgsConstructor
public class ResourceManager {
    private final S3Manager s3Manager;
    private final ResourceRepository resourceRepository;

    public ResourceDB createAndSaveProjectResource(ObjectMetadata metadata, Project project, TeamMember teamMember) {
        String path = metadata.getUserMetaDataOf("path");
        String fileName = metadata.getUserMetaDataOf("name");
        String type = metadata.getContentType();
        BigInteger size = BigInteger.valueOf(metadata.getContentLength());
        ResourceDB resourceDB = ResourceDB.builder()
                .key(path)
                .name(generateResourceName(fileName, project.getName()))
                .size(size)
                .type(type)
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .project(project)
                .build();
        return resourceRepository.save(resourceDB);
    }

    private String generateResourceName(String fileName, String entityName) {
        return entityName + " " + fileName;
    }


    public ResourceDB getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found"));
    }

    public ResourceInfo uploadResource(MultipartFile file, String directoryPath, Project project, TeamMember teamMember) {
        ObjectMetadata metadata = s3Manager.uploadFileToS3(file, directoryPath);
        ResourceDB resourceDB = createAndSaveProjectResource(metadata, project, teamMember);
        return new ResourceInfo(file.getResource(), resourceDB);
    }

    public void deleteFileFromProject(ResourceDB resourceDB) {
        s3Manager.deleteFileFromProject(resourceDB.getKey());
    }

    public Resource getResource(String pathKey) {
        return s3Manager.getFileFromProject(pathKey);
    }

}
