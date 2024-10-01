package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;

    @Override
    public ResourceDto addResource(Long projectId, MultipartFile file) throws IOException, ImageReadException{
        var project = projectRepository.getProjectById(projectId);

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        MultipartFile newFile = checkResourceSize(file);
        log.info("The file {}has been successfully verified for add", newFile.getOriginalFilename());

        String folder = project.getId() + project.getName();
        Resource resource = s3Service.uploadFile(newFile, folder);
        resource.setProject(project);
        resource = resourceRepository.save(resource);
        log.info("The resource with {}id has been successfully saved in repository", resource.getId());
        project.setStorageSize(newStorageSize);
        project.setCoverImageId(String.valueOf(resource.getId()));
        projectRepository.save(project);
        log.info("The project with {}id has been successfully saved in repository", project.getId());

        return resourceMapper.toDto(resource);
    }

    @Transactional
    public ResourceDto updateResource(Long resourceId, Long userDtoId, MultipartFile file) throws IOException, ImageReadException  {
        Resource resourceFromBD = getResourceWithCheckedPermissions(resourceId, userDtoId);
        log.info("User with {}id has permissions to update the file", userDtoId);
        var project = resourceFromBD.getProject();

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        MultipartFile newFile = checkResourceSize(file);
        log.info("The file {}has been successfully verified for update", newFile.getOriginalFilename());

        String folder = project.getId() + project.getName();
        s3Service.deleteFile(resourceFromBD.getKey());
        Resource resource = s3Service.uploadFile(newFile, folder);
        resourceFromBD.setKey(resource.getKey());
        resourceFromBD.setSize(resource.getSize());
        resourceFromBD.setUpdatedAt(resource.getUpdatedAt());
        resourceFromBD.setName(resource.getName());
        resourceFromBD.setType(resource.getType());
        resourceRepository.save(resourceFromBD);
        log.info("The resource with {}id has been successfully update", resourceFromBD.getId());
        project.setStorageSize(newStorageSize);
        projectRepository.save(project);
        log.info("The project with {}id has been successfully update", project.getId());

        return resourceMapper.toDto(resource);
    }

    @Transactional
    public void deleteResource(Long resourceId, Long userId) {
        Resource resource = getResourceWithCheckedPermissions(resourceId, userId);
        log.info("User with {}id has permissions to delete the file", userId);
        s3Service.deleteFile(resource.getKey());
        resourceRepository.delete(resource);
        log.info("The file {}has been successfully deleted from the repository", resource.getName());
    }

    private Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Resource with %s id not found", resourceId)));
    }

    private Resource getResourceWithCheckedPermissions(Long resourceId, Long userDtoId) {
        Resource resource = getResourceById(resourceId);
        var teamMember = resource.getCreatedBy();
        if (teamMember.getUserId().equals(userDtoId)) {
            return resource;
        } else {
            throw new IllegalArgumentException("You do not have permission to access this resource");
        }
    }

    private MultipartFile checkResourceSize(MultipartFile file) throws IOException, ImageReadException {
        long fileSize = file.getSize();
        long maxSizeByte = 5242880;
        if (fileSize > maxSizeByte) {
            throw new IllegalArgumentException("File is too big");
        }

        String fileName = file.getOriginalFilename();

        ImageInfo imageInfo;
        try (InputStream inputStream = file.getInputStream()) {
            imageInfo = Imaging.getImageInfo(inputStream, fileName);
        }

        int width = imageInfo.getWidth();
        int height = imageInfo.getHeight();

        if(width == height) {
            if(width > 1080) {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            BufferedImage lala = resizeImage(bufferedImage, 1080, 1080);
            return convertBufferedImageToMultipartFile(lala, file.getOriginalFilename(), file.getContentType());
            }
        } else if(width > height) {
            if (width > 1080 || height > 566) {
                BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
                BufferedImage lala = resizeImage(bufferedImage, 1080, 566);
                return convertBufferedImageToMultipartFile(lala, file.getOriginalFilename(), file.getContentType());
            }
        } else {
            throw new IllegalArgumentException("the file format is not supported");
        }
        return file;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    private MultipartFile convertBufferedImageToMultipartFile(BufferedImage image, String originalFilename, String contentType) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", baos);
            baos.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert image", e);
        }

        byte[] imageBytes = baos.toByteArray();
        return new MockMultipartFile(originalFilename, originalFilename, contentType, imageBytes);
    }
}
