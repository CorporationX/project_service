package faang.school.projectservice.service.gallery;

import faang.school.projectservice.dto.gallery.GalleryResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GalleryService {
    GalleryResponseDto uploadFiles(long projectId, List<MultipartFile> files);

    void deleteFiles(List<String> keys);

    List<String> downloadImagesAsBase64(long projectId);
}