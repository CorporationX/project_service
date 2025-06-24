package faang.school.projectservice.service.gallery;

import faang.school.projectservice.dto.resource.ResponseResourceDto;
import faang.school.projectservice.dto.resource.S3FileDto;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GalleryServiceFacade {

    private final GalleryService galleryService;
    private final ResourceMapper resourceMapper;

    public S3FileDto getGalleryItem(long projectId, long resourceId) {
        return galleryService.getGalleryItem(projectId, resourceId);
    }

    public Page<ResponseResourceDto> getGallery(Long projectId, int page, int size) {
        return galleryService.getGallery(projectId, page, size).map(resourceMapper::toDto);
    }

    public void addToGallery(long projectId, MultipartFile[] files) {
        galleryService.addGalleryAsync(projectId, List.of(files));
    }

    public void deleteFile(long projectId, long resourceId) {
        galleryService.deleteFile(projectId, resourceId);
    }
}