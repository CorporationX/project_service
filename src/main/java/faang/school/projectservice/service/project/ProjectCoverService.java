package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.image.FileData;
import faang.school.projectservice.dto.project.ProjectCoverDto;
import faang.school.projectservice.exception.EmptyCoverException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.image.ImageService;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.image.ImageValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectCoverService {

    private final ImageService imageService;
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

        if (coverImageId != null) {
            return s3Service.getFileById(coverImageId);
        } else {
            throw new EmptyCoverException("Cover image is not set for the project with id: " + projectId);
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
            s3Service.removeFileById(coverImageId);
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

        resourceService.putResource(fileKey, imageFile, ResourceType.IMAGE);
        project.setCoverImageId(fileKey);
        projectRepository.save(project);

        byte[] resizedImage = imageService.resizeImage(imageFile);
        s3Service.uploadFile(
                fileKey,
                imageFile.getOriginalFilename(),
                imageFile.getContentType(),
                resizedImage.length,
                new ByteArrayInputStream(resizedImage)
        );

        return fileKey;
    }
}
