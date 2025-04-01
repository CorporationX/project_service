package faang.school.projectservice.service;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.ExceptionMessage;
import faang.school.projectservice.exception.InvalidFileException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private static final int MAX_WIDTH_HORIZONTAL = 1080;
    private static final int MAX_HEIGHT_HORIZONTAL = 566;
    private static final int MAX_WIDTH_SQUARE = 1080;
    private static final int MAX_HEIGHT_SQUARE = 1080;
    private static final String FOLDER = "cover";
    private final ProjectService projectService;
    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;

    public ResourceDto saveProjectCover(long projectId, MultipartFile multipartFile) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(ExceptionMessage.PROJECT_NOT_FOUND, projectId));
        byte[] image = resizeImage(multipartFile);

        Resource resource = s3Service.uploadFromBytes(image, FOLDER, multipartFile.getOriginalFilename(), multipartFile.getContentType());
        resource.setProject(project);
        resourceRepository.save(resource);

        log.info("Created a new cover with ID = {} for a project with ID = {}", resource.getId(), projectId);

        return resourceMapper.toDto(resource);
    }

    public byte[] resizeImage(MultipartFile multipartFile) {
        BufferedImage originalImage = validateAndGetBufferedImage(multipartFile);

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        int[] newSizes = calculateNewSize(originalWidth, originalHeight);
        int newWidth = newSizes[0];
        int newHeight = newSizes[1];

        if (newWidth == originalWidth && newHeight == originalHeight) {
            try {
                return multipartFile.getBytes();
            } catch (IOException e) {
                throw new InvalidFileException(ExceptionMessage.UNABLE_READ_FILE);
            }
        }

        return createResizedImageBytes(multipartFile, originalImage, newWidth, newHeight);
    }

    private byte[] createResizedImageBytes(MultipartFile multipartFile, BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();

        String contentType = Objects.requireNonNull(multipartFile.getContentType()).split("/")[1];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(resizedImage, contentType, outputStream);
        } catch (IOException e) {
            throw new InvalidFileException(ExceptionMessage.IMAGE_PROCESSING_WRITE);
        }
        return outputStream.toByteArray();
    }

    private int[] calculateNewSize(int width, int height) {
        int newWidth = width;
        int newHeight = height;

        if (width > height) {
            if (width > MAX_HEIGHT_HORIZONTAL) {
                newWidth = MAX_WIDTH_HORIZONTAL;
                newHeight = (height * newWidth) / width;
            }

            if (newHeight > MAX_HEIGHT_HORIZONTAL) {
                newHeight = MAX_HEIGHT_HORIZONTAL;
                newWidth = (width * newHeight) / height;
            }
        } else if (width == height) {
            if (width > MAX_WIDTH_SQUARE) {
                newWidth = MAX_WIDTH_SQUARE;
                newHeight = MAX_HEIGHT_SQUARE;
            }
        }

        return new int[]{newWidth, newHeight};
    }

    private BufferedImage validateAndGetBufferedImage(MultipartFile multipartFile) {
        if (multipartFile == null) {
            throw new InvalidFileException(ExceptionMessage.FILE_NOT_SENT);
        }

        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(multipartFile.getInputStream());
        } catch (IOException e) {
            throw new InvalidFileException(ExceptionMessage.IMAGE_PROCESSING_READ);
        }

        if (originalImage == null) {
            throw new InvalidFileException(ExceptionMessage.FILE_NOT_VALID);
        }

        return originalImage;
    }

}
