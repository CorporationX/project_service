package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final S3ServiceImpl s3Service;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    @Transactional
    public ProjectDto addImage(Long projectId, MultipartFile file) throws IOException {
        Project project = projectRepository.getProjectById(projectId);

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        if (project.getMaxStorageSize().compareTo(newStorageSize) > 0) {
            String folder = project.getName() + project.getId();
            Project simpleProject = s3Service.uploadFile(file, folder);
            project.setCoverImageId(simpleProject.getCoverImageId());
            project.setStorageSize(newStorageSize);
        }

        return projectMapper.toDto(project);
    }

    @Transactional
    public void deleteImage(Long projectId){
        Project project = getProjectById(projectId);
        project.setStorageSize(BigInteger.valueOf(1));
        s3Service.deleteFile(project.getCoverImageId());
    }


    public ByteArrayOutputStream getImage(long projectId) {
        return s3Service.downloadFile(projectRepository.getProjectById(projectId).getCoverImageId());
    }
}
