package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ProjectService projectService;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final long MAX_COVER_SIZE = 5242880L;


    public ResourceDto addCoverToProject(long projectId, long userId, MultipartFile file) {
        Project project = projectService.getProjectById(projectId);
        TeamMember teamMember = teamMemberRepository.findById(userId);

        checkCoverMemorySize(file);
        checkCoverSize(file);

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(project.getMaxStorageSize(), newStorageSize);

        String folder = project.getId() + project.getName();
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource = resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        project.setCoverImageId(resource.getKey());
        projectService.save(project);

        return resourceMapper.toDto(resource);
    }

    public InputStream downloadCover(long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Ресурс не найден"));
        return s3Service.downloadFile(resource.getKey());
    }

    private void checkStorageSizeExceeded(BigInteger maxStorageSize, BigInteger newStorageSize) {
        if (newStorageSize.compareTo(maxStorageSize) > 0) {
            throw new IllegalArgumentException("Превышен размер хранилища");
        }
    }

    private void checkCoverMemorySize(MultipartFile file) {
        if (file.getSize() > MAX_COVER_SIZE) {
            throw new IllegalArgumentException("Превышен размер обложки");
        }
    }

    private void checkCoverSize(MultipartFile file) {
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        if (originalHeight == originalWidth) {
            if (originalHeight > 1080) {
                throw new FileException("Размер обложки не должен быть больше 1080x1080");
            }
        } else {
            if (originalHeight > 566 || originalWidth > 1080) {
                throw new FileException("Размер обложки не должен быть больше 1080x566");
            }
        }
    }
}
