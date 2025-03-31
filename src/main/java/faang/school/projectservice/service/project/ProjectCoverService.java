package faang.school.projectservice.service.project;

import faang.school.projectservice.repository.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.minio.ImageFormat;
import faang.school.projectservice.config.minio.properties.ProjectCoverMinioProperties;
import faang.school.projectservice.exception.BadRequestException;
import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.MinioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectCoverService {

    private final ProjectRepositoryAdapter projectRepositoryAdapter;
    private final MinioService minioService;
    private final ProjectCoverMinioProperties projectCoverMinioProperties;
    private final UserContext userContext;

    @Transactional
    public String addProjectCover(long id, MultipartFile file) {
        Project project = projectRepositoryAdapter.getById(id);

        if (project.getOwnerId() != userContext.getUserId()) {
            throw new BadRequestException("Only the owner of the project can add a cover");
        }

        if (project.getCoverImageId() != null) {
            throw new BadRequestException("The project with ID " + id + " already has a cover");
        }

        String key = uploadProjectCover(file);
        project.setCoverImageId(key);

        return key;
    }

    @Transactional
    public String deleteProjectCover(long id) {
        Project project = projectRepositoryAdapter.getById(id);

        if (project.getOwnerId() != userContext.getUserId()) {
            throw new BadRequestException("Only the owner of the project can delete a cover");
        }

        String projectCoverKey = project.getCoverImageId();

        if (projectCoverKey == null) {
            throw new BadRequestException("The project with ID " + id + " does not have a cover");
        }

        minioService.deleteFile(projectCoverKey);

        project.setCoverImageId(null);

        return projectCoverKey;
    }

    public InputStream getProjectCover(long id) {
        Project project = projectRepositoryAdapter.getById(id);

        String projectCoverKey = project.getCoverImageId();

        if (projectCoverKey == null) {
            return InputStream.nullInputStream();
        }

        return minioService.getFile(projectCoverKey);
    }

    private String uploadProjectCover(MultipartFile file) {
        long fileSize = file.getSize();
        long maxSize = projectCoverMinioProperties.getMaxSize();

        if (fileSize > maxSize) {
            throw new BadRequestException("The size of the project cover should not exceed " + maxSize + " bytes. "
                    + "The current size of the cover is " + fileSize + " bytes");
        }

        String fileContentType = file.getContentType();

        if (fileContentType == null || !fileContentType.startsWith("image/")) {
            throw new DataValidateException("Project cover must be an image");
        }

        String key = String.format("%s/%s-%s", projectCoverMinioProperties.getFolderName(), UUID.randomUUID(),
                file.getOriginalFilename());

        ImageFormat imageFormat = ImageFormat.fromContentType(fileContentType);

        InputStream processedStream;

        try {
            processedStream = compressProjectCover(file.getInputStream(), imageFormat.getFormat());
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while receiving an InputStream from a file: " + e.getMessage());
        }

        minioService.uploadFile(key, processedStream, fileSize, fileContentType);

        return key;
    }

    private InputStream compressProjectCover(InputStream fileInputStream, String imageFormat) {
        try {
            BufferedImage image = ImageIO.read(fileInputStream);

            if (image == null) {
                throw new DataValidateException("An error occurred while reading the image");
            }

            int imageHeight = image.getHeight();
            int imageWidth = image.getWidth();

            if (imageHeight > projectCoverMinioProperties.getMaxVerticalResolution()
                    || imageWidth > projectCoverMinioProperties.getMaxHorizontalResolution()) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                Thumbnails.of(image)
                        .scale(projectCoverMinioProperties.getCompressedOutputScale())
                        .outputQuality(projectCoverMinioProperties.getCompressedOutputQuality())
                        .outputFormat(imageFormat)
                        .toOutputStream(outputStream);

                return new ByteArrayInputStream(outputStream.toByteArray());
            } else {
                return fileInputStream;
            }
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while compressing the project cover: " + e.getMessage());
        }
    }
}
