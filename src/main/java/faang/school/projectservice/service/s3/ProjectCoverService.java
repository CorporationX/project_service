package faang.school.projectservice.service.s3;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.project.ProjectViewDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.project.ProjectUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class ProjectCoverService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private final ProjectRepository repository;
    private final UserContext userContext;
    private final ProjectMapper mapper;
    private final S3Client s3Client;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public ProjectViewDto linkCover(Long id, MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new DataValidationException("Размер файла не должен превышать 5 Мб");
        }
        Project project = repository.getByIdOrThrow(id);
        if (!ProjectUtil.isAvailable(project, userContext.getUserId())) {
            throw new ForbiddenException("У пользователя нет доступа к указанному проекту");
        }
        try {
            BufferedImage image = correctFormat(file);
            saveImageInS3(file, image, project);
            return mapper.toViewDto(repository.save(project));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при обработке файла", e);
        }
    }

    private BufferedImage correctFormat(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new DataValidationException("Некорректный формат изображения");
        }
        int width = image.getWidth();
        int height = image.getHeight();

        boolean isSquare = Math.abs(width - height) <= 1;
        int targetWidth = 1080;
        int targetHeight = isSquare ? 1080 : 566;

        if (width > targetWidth || height > targetHeight) {
            double widthRatio = (double) targetWidth / width;
            double heightRatio = (double) targetHeight / height;
            double scaleFactor = Math.min(widthRatio, heightRatio);

            int newWidth = (int) (width * scaleFactor);
            int newHeight = (int) (height * scaleFactor);

            BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, image.getType());
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
            g2d.dispose();

            image = scaledImage;
            return image;
        }
        return image;
    }

    private void saveImageInS3(MultipartFile file, BufferedImage image, Project project) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = getExtension(file.getOriginalFilename());
        ImageIO.write(image, formatName, baos);
        byte[] imageBytes = baos.toByteArray();
        String fileId = UUID.randomUUID().toString();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileId)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(imageBytes)
        );
        project.setCoverImageId(fileId);
    }

    private String getExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "jpg";
            case "png" -> "png";
            case "bmp" -> "bmp";
            case "gif" -> "gif";
            default -> throw new DataValidationException("Не поддерживаемый формат изображения");
        };
    }
}
