package faang.school.projectservice.service.project;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.dto.image.FileData;
import faang.school.projectservice.dto.project.ProjectCoverDto;
import faang.school.projectservice.exception.S3Exception;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.image.ImageValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectCoverService {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final ImageValidator imageValidator;
    private final ResourceService resourceService;
    private final S3Service s3Service;

    @Transactional
    public ProjectCoverDto uploadCover(Long projectId, MultipartFile imageFile) {
        return setCover(projectId, imageFile);
    }

    @Transactional
    public ProjectCoverDto changeCover(Long projectId, MultipartFile imageFile) {
        removeCover(projectId);

        return setCover(projectId, imageFile);
    }

    public FileData getCover(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        String coverImageId = project.getCoverImageId();
        Resource resource = resourceService.getResourceByKey(coverImageId);
        S3Object s3Object = s3Service.getObject(resource);

        try {
            return new FileData(
                    s3Object.getObjectContent().readAllBytes(),
                    s3Object.getObjectMetadata().getContentType()
            );
        } catch (IOException e) {
            throw new S3Exception("Error reading file content from S3 for projectId: " + projectId, e);
        } catch (AmazonServiceException e) {
            throw new S3Exception("Amazon S3 service error while fetching file for projectId: " + projectId, e);
        }
    }

    @Transactional
    public void removeCover(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        String coverImageId = project.getCoverImageId();

        if (coverImageId != null) {
            project.setCoverImageId(null);
            projectRepository.save(project);

            resourceService.markResourceAsDeleted(coverImageId);
            s3Service.deleteObject(coverImageId, project.getName());
        }
    }

    private ProjectCoverDto setCover(Long projectId, MultipartFile imageFile) {
        imageValidator.validateMaximumSize(imageFile.getSize());
        Project project = projectService.getProjectById(projectId);
        String coverImageId = uploadCoverFile(imageFile, project);

        return new ProjectCoverDto(project.getId(), coverImageId);
    }

    private String uploadCoverFile(MultipartFile imageFile, Project project) {
        String fileKey = UUID.randomUUID().toString();

        Resource resource = resourceService.saveResource(imageFile, fileKey, ResourceType.IMAGE);
        project.setCoverImageId(fileKey);
        projectRepository.save(project);
        s3Service.saveObject(imageFile, resource);

        return fileKey;
    }
}
