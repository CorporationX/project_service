package faang.school.projectservice.service.ProjectCover;

import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProjectCoverService {

    private final CoverProperties coverProperties;
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;


    @Transactional
    public void addCover(Long projectId, Long userId, MultipartFile file) {
        isAllowed(projectId, userId);
        checkSize(file);

        Project project = projectRepository.findById(projectId).orElseThrow();
        String key = String.valueOf(projectId);
        byte[] image;
        try {
            image = processCoverImage(file);
            s3Service.uploadFile(file.getContentType(),image, key);
            project.setCoverImageId(key);
            projectRepository.save(project);
            log.info("added cover image with key{} for projectId: {}", key, projectId);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream downloadCover(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        String key = project.getCoverImageId();
        return s3Service.downloadFile(key);
    }

    public void updateCover(Long projectId, Long userId, MultipartFile file) {
        isAllowed(projectId, userId);
        checkSize(file);

        Project project = projectRepository.findById(projectId).orElseThrow();
        String key = project.getCoverImageId();

        byte[] image;
        try {
            image = processCoverImage(file);
            s3Service.uploadFile(file.getContentType(),image, key);
            projectRepository.save(project);
            log.info("updated cover image with key{} for projectId: {}", key, projectId);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteCover(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        String key = project.getCoverImageId();
        isAllowed(project.getOwnerId(), userId);
        s3Service.deleteFile(String.valueOf(key));
        log.info("deleted cover image with key: {}", key);
    }

    private void isAllowed(Long ownerId, Long userId) {
        if (!ownerId.equals(userId)) {
            String message = "You are not allowed to change this cover image";
            log.info(message);
            throw new ForbiddenException(message);
        }
    }

    private void checkSize(MultipartFile file) {
        if (file.getSize() > coverProperties.getMaxSizeBytes()) {
            String message = String.format("File size should be less than %d MB",
                    coverProperties.getMaxSizeBytes() / 1024 / 1024);
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    public byte[] processCoverImage(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));

        if (originalImage == null) {
            throw new RuntimeException("Невозможно прочитать изображение");
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        CoverType coverType = determineCoverType(originalWidth, originalHeight);

        int maxWidth = getMaxWidthForType(coverType);
        int maxHeight = getMaxHeightForType(coverType);

        if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
            return file.getBytes();
        }

        return compressImage(originalImage, maxWidth, maxHeight);
    }

    private byte[] compressImage(BufferedImage image, int maxWidth, int maxHeight)
            throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(image)
                .size(maxWidth, maxHeight)
                .outputFormat("JPEG")
                .outputQuality(coverProperties.getOutputQuality())
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    private CoverType determineCoverType(int width, int height) {
        double ratio = (double) width / height;

        if (ratio >= 1.5) {
            return CoverType.HORIZONTAL;
        } else if (ratio <= 0.7) {
            return CoverType.VERTICAL;
        } else {
            return CoverType.SQUARE;
        }
    }

    private int getMaxWidthForType(CoverType coverType) {
        return switch (coverType) {
            case HORIZONTAL -> coverProperties.getMaxWidthHorizontal();
            case SQUARE -> coverProperties.getMaxSquare();
            case VERTICAL -> coverProperties.getMaxWidthVertical();
        };
    }

    private int getMaxHeightForType(CoverType coverType) {
        return switch (coverType) {
            case HORIZONTAL -> coverProperties.getMaxHeightHorizontal();
            case SQUARE -> coverProperties.getMaxSquare();
            case VERTICAL -> coverProperties.getMaxHeightVertical();
        };
    }
}