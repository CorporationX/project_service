package faang.school.projectservice.service;

import faang.school.projectservice.dto.ImageConfig;
import faang.school.projectservice.exception.ProjectImageCoverException;
import faang.school.projectservice.exception.ProjectNotFound;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class ProjectService {
    public static final String PROJECT_NOT_FOUND = "project not found; id={}.";
    public static final String COVER_PREFIX = "cover_image";
    public static final String ERROR_UPLOAD_PROJECT_IMAGE_COVER = "Error upload project image cover";

    private final AmazonS3Service amazonS3Service;
    private final ProjectRepository projectRepository;
    private final ImageConfig coverImageConfig;
    private final Utils utils;

    public ProjectService(
            AmazonS3Service amazonS3Service,
            ProjectRepository projectRepository,
            @Qualifier("CoverImageConfig") ImageConfig coverImageConfig,
            Utils utils
    ) {
        this.amazonS3Service = amazonS3Service;
        this.projectRepository = projectRepository;
        this.coverImageConfig = coverImageConfig;
        this.utils = utils;
        log.debug("coverImageConfig: {}", coverImageConfig);
    }

    @Transactional
    public void addCoverImage(Long projectId, MultipartFile file) {
        validateProject(projectId);
        String coverImageKey = getImageKey(file);
        updateCoverImageKey(projectId, coverImageKey);
    }

    @Transactional
    public void deleteCoverImage(Long projectId) {
        validateProject(projectId);
        updateCoverImageKey(projectId, null);
    }

    private void validateProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFound(utils.format(PROJECT_NOT_FOUND, projectId));
        }
    }

    private void updateCoverImageKey(Long projectId, String coverImageKey) {
        projectRepository.findCoverImageIdById(projectId)
                .ifPresent(amazonS3Service::deleteImage);
        projectRepository.updateCoverImage(projectId, coverImageKey);
    }

    private String getImageKey(MultipartFile file) {
        try {
            return amazonS3Service.uploadImage(file, COVER_PREFIX, coverImageConfig);
        } catch (IOException e) {
            log.error("error upload image to S3 service.", e);
            throw new ProjectImageCoverException(ERROR_UPLOAD_PROJECT_IMAGE_COVER);
        }
    }
}
