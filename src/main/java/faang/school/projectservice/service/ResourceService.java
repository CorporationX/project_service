package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.FileProcessingException;
import faang.school.projectservice.exception.StorageLimitException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.utils.ImageUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {
    private final ProjectService projectService;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    @Value("${app.project.image.max_width}")
    private int MAX_WIDTH;
    @Value("${app.project.image.max_height}")
    private int MAX_HEIGHT;
    @Value("${app.project.image.quality}")
    private float QUALITY;

    @Transactional
    public ResourceDto addCover(long projectId, MultipartFile file) {
        ProjectDto projectDto = projectService.getProjectDtoById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));
        String folder = projectDto.getId() + projectDto.getName();

        ResourceDto resourceDto = s3Service.uploadFile(processImage(file), folder);
        resourceDto.setProject(projectDto);
        resourceRepository.save(resourceMapper.toEntity(resourceDto));
        return resourceDto;
    }

    private MultipartFile processImage(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            log.warn("Uploaded file is not a valid image. name={}, contentType={}",
                    originalName, file.getContentType());
            throw new FileProcessingException("Uploaded file is not a valid image");
        }
        byte[] compressed = zipImage(file);
        return new MockMultipartFile(
                file.getName(),
                file.getOriginalFilename(),
                file.getContentType(),
                compressed
        );
    }

    private byte[]  zipImage(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        try {
            byte[] originalBytes = file.getBytes();
            ImageUtils.ImageInputStreamWrapper wrapper = () -> new ByteArrayInputStream(originalBytes);
            BufferedImage img = ImageUtils.read(wrapper);
            if (img == null) {
                throw new FileProcessingException("Cannot read image");
            }

            int w = img.getWidth();
            int h = img.getHeight();

            boolean isSquare = w == h;
            boolean isHorizontal = w > h;

            String outFormat = file.getContentType().equals("image/png") ? "png" : "jpg";

            if (isHorizontal) {
                if (w > MAX_WIDTH || h > MAX_HEIGHT) {
                    log.debug("Resizing horizontal image. name={}, w={}, h={}", originalName, w, h);
                    return ImageUtils.resizeToFit(img, MAX_WIDTH, MAX_HEIGHT, outFormat, QUALITY);
                } else {
                    return originalBytes;
                }
            } else if (isSquare) {
                if (w > MAX_WIDTH) {
                    log.debug("Resizing square image. name={}, w={}, h={}", originalName, w, h);
                    return ImageUtils.resizeToFit(img, MAX_WIDTH, MAX_WIDTH, outFormat, QUALITY);
                } else {
                    return originalBytes;
                }
            } else {
                return originalBytes;
            }
        } catch (IOException e) {
            log.error("Failed to read uploaded image file. name={}, contentType={}",
                    originalName, file.getContentType(), e);
            throw new FileProcessingException("Failed to read uploaded image file", e);
        }
    }
}
