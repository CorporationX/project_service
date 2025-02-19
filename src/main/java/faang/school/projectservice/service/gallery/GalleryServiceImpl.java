package faang.school.projectservice.service.gallery;

import com.amazonaws.util.IOUtils;
import faang.school.projectservice.dto.gallery.GalleryResponseDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.properties.GalleryProperties;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class GalleryServiceImpl implements GalleryService {

    private final S3Service s3Client;
    private final ProjectRepository projectRepository;
    private final GalleryProperties galleryProperties;

    @Override
    @Transactional
    public GalleryResponseDto uploadFiles(long projectId, List<MultipartFile> files) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        s3Client.validateAndCheckFileSizes(files, galleryProperties.getMaxFileSizeGalleryInBytes());

        int totalFiles = getTotalFilesByProjectId(projectId);
        int maxImages = galleryProperties.getMaxImages();
        if (totalFiles + files.size() > maxImages) {
            throw new DataValidationException(String.format("Gallery is full, you can add only %d files", (maxImages - totalFiles)));
        }

        List<String> keys = s3Client.uploadFiles(files, projectId);
        updateProjectGalleryFileKeys(project, keys);
        projectRepository.save(project);
        return new GalleryResponseDto(projectId, keys);
    }

    @Override
    @Transactional
    public void deleteFiles(List<String> keys) {
        projectRepository.deleteGalleryByKeys(keys);
        keys.forEach(s3Client::deleteFile);
    }

    @Override
    @Transactional
    public List<String> downloadImagesAsBase64(long projectId) {

        Project project = getProjectById(projectId);

        List<String> keys = project.getGalleryFileKeys();

        return keys.stream()
                .map(this::convertToBase64)
                .toList();
    }

    private Project getProjectById(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
    }

    private String convertToBase64(String key) {
        log.info("Processing key: " + key);
        InputStream inputStream = s3Client.downloadFile(key);
        try {
            byte[] imageBytes = IOUtils.toByteArray(inputStream);
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("Failed to encode image to Base64: " + e.getMessage());
            throw new RuntimeException("Failed to encode image to Base64", e);
        }
    }


    private void updateProjectGalleryFileKeys(Project project, List<String> keys) {
        List<String> currentGalleryFileKeys = project.getGalleryFileKeys();
        currentGalleryFileKeys.addAll(keys);
        project.setGalleryFileKeys(currentGalleryFileKeys);
    }


    private int getTotalFilesByProjectId(long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        return project.get().getGalleryFileKeys().size();
    }
}
