package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.dto.client.ResourceDto;
import faang.school.projectservice.exceptions.DBException;
import faang.school.projectservice.exceptions.FileException;
import faang.school.projectservice.exceptions.S3Exception;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.util.ImageHandler;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper = Mappers.getMapper(ResourceMapper.class);
    private final ProjectJpaRepository projectJpaRepository;
    private final S3Service s3Service;

    @Override
    public ResourceDto addResource(ResourceDto resourceDto) {
        Resource savedResource = resourceRepository.save(resourceMapper.toResource(resourceDto));
        return resourceMapper.toDto(savedResource);
    }

    @Override
    @Transactional
    public ResourceDto addCoveringImageToProject(MultipartFile file, Long projectId) {
        validateProjectExist(projectId);
        validateMaxSize(file);

        String folderName = String.format("covering-images/%d", projectId);
        String originalFileName = file.getOriginalFilename();

        byte[] imageBytes = reducePictureIfNecessary(file);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType(file.getContentType());

        Resource resource = null;
        try {
            resource = s3Service.uploadFile(imageBytes, folderName, originalFileName, metadata, projectId);
            return addResource(resourceMapper.toDto(resource));
        } catch (RuntimeException e) {
            if (resource != null) {
                s3Service.deleteFile(resource.getKey());
                throw new DBException("Saving image to DB error. Image: " + originalFileName);
            }
            throw new S3Exception("Saving image to S3 error. Image: " + originalFileName);
        }
    }

    private void validateMaxSize(MultipartFile file) {
        int imageMaxSize = 5_242_880;
        if (file.getSize() > imageMaxSize) {
            throw new MaxUploadSizeExceededException(imageMaxSize);
        }
    }

    private byte[] reducePictureIfNecessary(MultipartFile file) {
        int maxResolution = 1080;
        String imageFormat = file.getContentType().split("/")[1];
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            if (width > maxResolution || height > maxResolution) {
                return ImageHandler.changeSize(bufferedImage, imageFormat, maxResolution);
            }
            return file.getBytes();
        } catch (IOException e) {
            throw new FileException("Reading image error: " + file.getOriginalFilename());
        }
    }

    private void validateProjectExist(Long projectId) {
        if (!projectJpaRepository.existsById(projectId)) {
            throw new EntityNotFoundException(String.format("Project with id = %d not exist", projectId));
        }
    }

}
