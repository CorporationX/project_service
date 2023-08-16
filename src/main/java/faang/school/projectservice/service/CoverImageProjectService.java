package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CoverImageFromAwsDto;
import faang.school.projectservice.dto.project.ProjectCoverImageDto;
import faang.school.projectservice.exception.DataValidException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.FileStorageService;
import faang.school.projectservice.util.ImageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CoverImageProjectService {
    private final ProjectRepository projectRepository;
    private final FileStorageService fileStorageService;
    private final ImageService imageService;

    String kee;
    @Transactional
    public ProjectCoverImageDto upload(MultipartFile file, long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new EntityNotFoundException("Project with id " + projectId + " is not found"));

        byte[] big = imageService.resizeImage(file, true);
        String bigKey = fileStorageService.uploadFile(big, file, projectId, "big");

        byte[] small = imageService.resizeImage(file, false);
        String smallKey = fileStorageService.uploadFile(small, file, projectId, "small");


        if (Arrays.equals(small, big)) {
            kee = smallKey;
        } else {
            kee = bigKey;
        }
            project.setCoverImageId(kee);

            projectRepository.save(project);
//сохранить 1 картинку релевантного размера (маленькую?)

        return new ProjectCoverImageDto(projectId, kee);

    }

    @Transactional(readOnly = true)
    public CoverImageFromAwsDto getByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new DataValidException("Project with id " + projectId + " is not found"));
//            UserProfilePic userProfilePic = user.getUserProfilePic();

        if (project.getCoverImageId() == null) {
            throw new DataValidException("User with id " + projectId + " doesn't has an avatar");
        }

        CoverImageFromAwsDto cover = fileStorageService.receiveFile(project.getCoverImageId());

        return cover;
    }

    @Transactional
    public ProjectCoverImageDto deleteByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new DataValidException("Project with id " + projectId + " is not found"));
//            UserProfilePic userProfilePic = user.getUserProfilePic();

        if (project.getCoverImageId() == null) {
            throw new DataValidException("Project with id " + projectId + " doesn't has an avatar");
        }

        fileStorageService.deleteObject(project.getCoverImageId());
//            fileStorageService.deleteObject(userProfilePic.getSmallFileId());
//            user.setUserProfilePic(null);

        return new ProjectCoverImageDto(projectId, null);
    }
}

