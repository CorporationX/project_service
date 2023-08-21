package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CoverImageFromAwsDto;
import faang.school.projectservice.dto.project.ProjectCoverImageDto;
import faang.school.projectservice.exception.DataValidException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.FileStorageService;
import faang.school.projectservice.util.ImageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CoverImageProjectService {
    private final ProjectRepository projectRepository;
    private final FileStorageService fileStorageService;
    private final ImageService imageService;

    @Transactional
    public ProjectCoverImageDto upload(MultipartFile file, long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new EntityNotFoundException("Project with id " + projectId + " is not found"));
        byte[] cover = imageService.resizeImage(file);
        String key = fileStorageService.uploadFile(cover, file, projectId);
        project.setCoverImageId(key);
        projectRepository.save(project);
        return new ProjectCoverImageDto(projectId, key);
    }

    @Transactional(readOnly = true)
    public CoverImageFromAwsDto getByProjectId(Long projectId) throws IOException {
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new DataValidException("Project with id " + projectId + " is not found"));

        String coverImageId = fileStorageService.uploadDefaultPicture();
        if (project.getCoverImageId() != null) {
            coverImageId = project.getCoverImageId();
        }
        return fileStorageService.receiveFile(coverImageId);
    }

    @Transactional
    public ProjectCoverImageDto deleteByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new DataValidException("Project with id " + projectId + " is not found"));
        if (project.getCoverImageId() != null) {
            fileStorageService.deleteObject(project.getCoverImageId());
            project.setCoverImageId(null);
            projectRepository.save(project);
        }
        return new ProjectCoverImageDto(projectId, null);
    }
}

