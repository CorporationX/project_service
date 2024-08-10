package faang.school.projectservice.service.coverimage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoverImageService {
    private final ProjectRepository projectRepository;
    private final CoverImageSizeHandler coverImageSizeHandler;
    private final S3Service s3Service;

    public void create(Long projectId, MultipartFile file) {
        MultipartFile multipartFile = coverImageSizeHandler.validateSizeAndResolution(file);
        Project project = projectRepository.getProjectById(projectId);
        String key = s3Service.putIntoBucket(multipartFile);
        project.setCoverImageId(key);
        projectRepository.save(project);
    }

    public void delete(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        s3Service.deleteFromBucket(project.getCoverImageId());
        project.setCoverImageId(null);
        projectRepository.save(project);
    }
}
