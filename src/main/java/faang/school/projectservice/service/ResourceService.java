package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.FileProcessingException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.utils.ImageUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    @Transactional
    public ResourceDto addCover(ProjectDto projectDto, MultipartFile file) {
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
        byte[] compressed = ImageUtils.zipImage(file);
        return new MockMultipartFile(
                file.getName(),
                file.getOriginalFilename(),
                file.getContentType(),
                compressed
        );
    }
}
