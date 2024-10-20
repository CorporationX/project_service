package faang.school.projectservice.service.impl;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.FileTooLargeException;
import faang.school.projectservice.exception.MinioUploadException;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.dto.ProjectDto;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.event.ProjectViewEvent;
import faang.school.projectservice.publisher.ProjectViewEventPublisher;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.resource.S3Service;
import faang.school.projectservice.validator.project.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final ProjectMapper projectMapper;
    private final ProjectValidator projectValidator;
    private final ProjectViewEventPublisher projectViewEventPublisher;
    private final UserContext userContext;

    @Setter
    @Value("${cover-image.maxWidth}")
    private int maxWidth;

    @Setter
    @Value("${cover-image.maxHeightHorizontal}")
    private int maxHeightHorizontal;

    @Setter
    @Value("${cover-image.maxHeightSquare}")
    private int maxHeightSquare;

    @Setter
    @Value("${cover-image.maxFileSize}")
    private long maxFileSize;

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.getProjectById(id);
    }

    @Override
    @Transactional
    public ProjectDto uploadCoverImage(Long projectId, MultipartFile coverImage) {
        projectValidator.verifyProjectExists(projectId);
        log.info("Добавление обложки для проекта с ID: {}", projectId);
        Project project = projectRepository.findById(projectId);

        if (coverImage.getSize() > maxFileSize) {
            throw new FileTooLargeException("Размер файла превышает допустимый лимит (" + maxFileSize + " байт)");
        }
        try {
            byte[] imageData = processImage(coverImage);
            String key = generateUniqueKey(coverImage);

            s3Service.uploadCoverImage(key, new ByteArrayInputStream(imageData), coverImage.getContentType(), imageData.length);
            project.setCoverImageId(key);
            projectRepository.save(project);
            return projectMapper.toDto(project);
        } catch (IOException e) {
            log.error("Ошибка при загрузке изображения: ", e);
            throw new RuntimeException("Ошибка загрузки изображения", e);
        } catch (MinioUploadException e) {
            log.error("Ошибка при загрузке файла в Minio: ", e);
            throw new RuntimeException("Ошибка загрузки файла в Minio", e);
        }
    }

    @Override
    public InputStream downloadCoverImage(Long projectId) {
        projectValidator.verifyProjectExists(projectId);
        log.info("Загрузка обложки для проекта с ID: {}", projectId);
        Project project = projectRepository.findById(projectId);

        String coverImageId = project.getCoverImageId();
        if (coverImageId == null) {
            throw new EntityNotFoundException("Проект не имеет обложки");
        }
        return s3Service.downloadCoverImage(coverImageId);
    }

    @Override
    public ProjectDto deleteCoverImage(Long projectId) {
        projectValidator.verifyProjectExists(projectId);
        log.info("Удаление обложки для проекта с ID: {}", projectId);
        Project project = projectRepository.findById(projectId);

        String coverImageId = project.getCoverImageId();
        if (coverImageId != null) {
            s3Service.deleteCoverImage(coverImageId);
            project.setCoverImageId(null);
            projectRepository.save(project);
        }
        return projectMapper.toDto(project);
    }

    @Override
    public ProjectDto getProject(long projectId) {
        Project project = projectRepository.findById(projectId);
        if (project != null) {
            projectViewEventPublisher.publish(new ProjectViewEvent(projectId, userContext.getUserId(), LocalDateTime.now()));
        }
        return projectMapper.toDto(project);
    }

    private byte[] processImage(MultipartFile coverImage) throws IOException {
        if (coverImage.isEmpty()) {
            log.error("Загруженный файл пустой.");
            throw new IOException("Ошибка: загруженный файл пустой.");
        }
        InputStream originalInputStream = coverImage.getInputStream();
        String contentType = coverImage.getContentType();
        BufferedImage image = ImageIO.read(originalInputStream);
        image = resizeImage(image);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = contentType.substring(contentType.indexOf("/") + 1);

        boolean written = ImageIO.write(image, formatName, baos);
        if (!written) {
            throw new IOException("Ошибка: не удалось записать изображение.");
        }
        return baos.toByteArray();
    }

    private BufferedImage resizeImage(BufferedImage image) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();
        int maxHeight = width > height ? maxHeightHorizontal : maxHeightSquare;

        if (width > maxWidth || height > maxHeight) {
            BufferedImage resizedImage = Thumbnails.of(image)
                    .size(maxWidth, maxHeight)
                    .asBufferedImage();
            log.debug("Изображение успешно изменено. Новый размер: {}x{}", resizedImage.getWidth(), resizedImage.getHeight());
            return resizedImage;
        } else {
            log.debug("Размер изображения в пределах допустимого. Возвращаем оригинальное изображение.");
            return image;
        }
    }

    private String generateUniqueKey(MultipartFile coverImage) {
        String originalFilename = coverImage.getOriginalFilename();
        log.debug("Генерация уникального ключа для изображения: {}", originalFilename);
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.')) : "";
        String key = UUID.randomUUID() + extension;
        log.debug("Сгенерированный ключ: {}", key);
        return key;
    }
}