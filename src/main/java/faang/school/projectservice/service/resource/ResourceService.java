package faang.school.projectservice.service.resource;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import faang.school.projectservice.dto.image.ProjectImage;
import faang.school.projectservice.dto.resource.MultipartFileResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.s3.requests.S3Request;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private static final String DEFAULT_FOLDER_RESOURCE_DELIMITER = "_";
    
    private final S3Service s3Service;
    @Qualifier("s3ProjectCoverRequest")
    private final S3Request s3CoverRequest;
    
    @SneakyThrows
    public Resource uploadProjectCover(MultipartFile file, Project project, TeamMember teamMember) {
        ProjectImage projectImage = new ProjectImage(file.getInputStream());
        
        MultipartFileResourceDto resourceDto = getMultipartFileResourceDto(file, project, projectImage);
        PutObjectRequest projectCoverPutRequest = putObjectToS3(resourceDto);
        
        return createResource(file, project, teamMember, projectCoverPutRequest, resourceDto);
    }
    
    public void deleteProjectCover(String coverImageId) {
        DeleteObjectRequest deleteRequest = s3CoverRequest.deleteRequest(coverImageId);
        s3Service.deleteFile(deleteRequest);
    }
    
    public PutObjectRequest putObjectToS3(MultipartFileResourceDto resourceDto) {
        PutObjectRequest putRequest = s3CoverRequest.putRequest(resourceDto);
        s3Service.uploadFile(putRequest);
        return putRequest;
    }
    
    private MultipartFileResourceDto getMultipartFileResourceDto(
        MultipartFile file,
        Project project,
        ProjectImage projectImage
    ) {
        return MultipartFileResourceDto.builder()
            .size(projectImage.getResizedImageSize())
            .contentType(file.getContentType())
            .fileName(file.getOriginalFilename())
            .folderName(getDefaultProjectFolderName(project))
            .resourceInputStream(projectImage.getCoverInputStream())
            .build();
    }
    
    private Resource createResource(
        MultipartFile file,
        Project project,
        TeamMember teamMember,
        PutObjectRequest projectCoverPutRequest,
        MultipartFileResourceDto resourceDto
    ) {
        Resource resource = new Resource();
        resource.setKey(projectCoverPutRequest.getKey());
        resource.setSize(BigInteger.valueOf(resourceDto.getSize()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setName(resourceDto.getFileName());
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);
        return resource;
    }
    
    private String getDefaultProjectFolderName(Project project) {
        return String.format("%s%s%s", project.getId(), DEFAULT_FOLDER_RESOURCE_DELIMITER, project.getName());
    }
}
