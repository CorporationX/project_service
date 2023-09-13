package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.CoverImageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.s3.S3ServiceImpl;
import faang.school.projectservice.util.CoverService;
import faang.school.projectservice.validator.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final ResourceValidator resourceValidator;
    private final S3ServiceImpl s3Service;
    private final CoverService coverService;
    private final CoverImageMapper coverImageMapper;

    public ResourceDto addCoverToProject(Long projectId, MultipartFile multipartFile) {
        Project project = projectRepository.getProjectById(projectId);

        resourceValidator.validateSizeFile(multipartFile);
        coverService.resizeCover(multipartFile);

        String folder = project.getId() + project.getName();
        Resource resource = s3Service.uploadFile(multipartFile, folder);
        resource.setProject(project);
        resource = resourceRepository.save(resource);

        project.setCoverImageId(String.valueOf(resource.getId()));
        projectRepository.save(project);

        return coverImageMapper.toDto(resource);
    }
}