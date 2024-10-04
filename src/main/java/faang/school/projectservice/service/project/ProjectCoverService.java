package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.image.FileData;
import faang.school.projectservice.dto.project.ProjectCoverDto;
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
        return handleCoverUpload(projectId, imageFile, false);
    }

    @Transactional
    public ProjectCoverDto changeCover(Long projectId, MultipartFile imageFile) {
        return handleCoverUpload(projectId, imageFile, true);
    }

    @Transactional
    public FileData getCover(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        String coverImageId = project.getCoverImageId();

        return coverImageId != null ? s3Service.getFileByKey(coverImageId) : null;
    }

    @Transactional
    public void deleteCover(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        removeCoverFile(project);
        project.setCoverImageId(null);
        projectRepository.save(project);
    }

    private ProjectCoverDto handleCoverUpload(Long projectId, MultipartFile imageFile, boolean removeExistingCover) {
        imageValidator.validateMaximumSize(imageFile.getSize());
        Project project = projectService.getProjectById(projectId);

        if (removeExistingCover) {
            removeCoverFile(project);
        }

        String coverImageId = uploadCoverFile(imageFile, project);

        return new ProjectCoverDto(project.getId(), coverImageId);
    }

    private String uploadCoverFile(MultipartFile imageFile, Project project) {
        byte[] resizedImage = imageService.resizeImage(imageFile);
        String coverImageId = s3Service.uploadFile(
                imageFile.getOriginalFilename(),
                imageFile.getContentType(),
                resizedImage.length,
                new ByteArrayInputStream(resizedImage)
        );

        project.setCoverImageId(coverImageId);
        projectRepository.save(project);
        resourceService.putResource(imageFile, ResourceType.IMAGE, coverImageId);

        return coverImageId;
    }

    private void removeCoverFile(Project project) {
        String coverImageId = project.getCoverImageId();
        if (coverImageId != null) {
            resourceService.markResourceAsDeleted(coverImageId);
            s3Service.removeFileByKey(coverImageId);
        }
    }
}
