package faang.school.projectservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.projectservice.exception.InvalidFileException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectCoverService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class ProjectCoverServiceImpl implements ProjectCoverService {

    private final AmazonS3 amazonS3;
    private final ProjectRepository projectRepository;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public ProjectCoverServiceImpl(AmazonS3 amazonS3, ProjectRepository projectRepository) {
        this.amazonS3 = amazonS3;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional
    public String uploadProjectCover(Long projectId, MultipartFile file) throws Exception {
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new InvalidFileException("Размер файла превышает 5 Мб");
        }

        if (!isImageFile(file)) {
            throw new InvalidFileException("Файл должен быть изображением (JPEG, PNG, GIF)");
        }

        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new InvalidFileException("Невозможно прочитать изображение");
        }

        BufferedImage resizedImage = resizeImageIfNecessary(image);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String formatName = getImageFormat(file.getOriginalFilename());
        ImageIO.write(resizedImage, formatName, os);
        byte[] imageBytes = os.toByteArray();

        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName,
                new ByteArrayInputStream(imageBytes), null)
                .withCannedAcl(com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead);

        amazonS3.putObject(putObjectRequest);

        Project project = projectRepository.getProjectById(projectId);
        project.setCoverImageId(fileName);
        projectRepository.save(project);

        return fileName;
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equalsIgnoreCase("image/jpeg") ||
                        contentType.equalsIgnoreCase("image/png") ||
                        contentType.equalsIgnoreCase("image/gif")
        );
    }

    private BufferedImage resizeImageIfNecessary(BufferedImage originalImage) throws IOException {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        boolean isSquare = width == height;

        int maxWidth = 1080;
        int maxHeight = isSquare ? 1080 : 566;

        if (width > maxWidth || height > maxHeight) {
            BufferedImage resizedImage = new BufferedImage(maxWidth, maxHeight, originalImage.getType());
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, maxWidth, maxHeight, null);
            g.dispose();
            return resizedImage;
        }

        return originalImage;
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "jpg";
        }
        String[] parts = filename.split("\\.");
        return parts.length > 1 ? parts[parts.length - 1] : "jpg";
    }

    private String getImageFormat(String filename) {
        String ext = getFileExtension(filename).toLowerCase();
        return switch (ext) {
            case "png" -> "png";
            case "gif" -> "gif";
            default -> "jpg";
        };
    }
}
