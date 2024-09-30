package faang.school.projectservice.service.file;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
        checkResourceSize(file);

        String folder = project.getId() + project.getName();
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setProject(project);
        resource = resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        project.setCoverImageId(String.valueOf(resource.getId()));
        projectRepository.save(project);

        return resourceMapper.toDto(resource);
    }

    @Transactional
    public ResourceDto updateResource(Long resourceId, Long userDtoId, MultipartFile file) throws IOException, ImageReadException  {
        Resource resourceFromBD = getResourceWithCheckedPermissions(resourceId, userDtoId);
        var project = resourceFromBD.getProject();

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkResourceSize(file);

        String folder = project.getId() + project.getName();
        s3Service.deleteFile(resourceFromBD.getKey());
        Resource resource = s3Service.uploadFile(file, folder);
        resourceFromBD.setKey(resource.getKey());
        resourceFromBD.setSize(resource.getSize());
        resourceFromBD.setUpdatedAt(resource.getUpdatedAt());
        resourceFromBD.setName(resource.getName());
        resourceFromBD.setType(resource.getType());
        resourceRepository.save(resourceFromBD);

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);

        return resourceMapper.toDto(resource);
    }

    @Transactional
    public void deleteResource(Long resourceId, Long userId) {
        Resource resource = getResourceWithCheckedPermissions(resourceId, userId);
        s3Service.deleteFile(resource.getKey());
        resourceRepository.delete(resource);
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

    private void checkResourceSize(MultipartFile file) throws IOException, ImageReadException {
        long fileSize = file.getSize();
        long maxSizeByte = 5242880;
        if (fileSize > maxSizeByte) {
            throw new IllegalArgumentException("File is too big");
        }

        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        ImageInfo imageInfo = Imaging.getImageInfo(inputStream, fileName);

        int width = imageInfo.getWidth();
        int height = imageInfo.getHeight();

        if(width == height) {
            if(width > 1080) {
                throw new IllegalArgumentException("The file resolution is more than 1080 by 1080");
            }
        } else if(width > height) {
            if (width > 1080 || height > 566) {
                throw new IllegalArgumentException("The file resolution is more than 566 by 1080");
            }
        } else {
            throw new IllegalArgumentException("the file format is not supported");
        }
    }
}
