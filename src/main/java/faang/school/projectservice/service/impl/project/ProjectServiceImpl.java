package faang.school.projectservice.service.impl.project;

import faang.school.projectservice.model.dto.project.ProjectDto;
import faang.school.projectservice.model.mapper.subproject.ProjectMapper;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.S3Service;
import faang.school.projectservice.service.file.FileCompressor;
import faang.school.projectservice.validator.project.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectValidator validator;
    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository memberRepository;
    private final S3Service s3Service;
    private final FileCompressor fileCompressor;

    @Value("${image.maxWidth}")
    private int maxWidth;

    @Value("${image.maxLength}")
    private int maxLength;

    @Override
    @Transactional(readOnly = true)
    public Project getProject(long projectId) {
        Project project = validator.validateProject(projectId);

        return project;
    }

    @Override
    @Transactional
    public ProjectDto uploadCover(long projectId, MultipartFile file, long userId) {
        memberRepository.findByUserIdAndProjectId(userId, projectId);
        Project project = projectRepository.getProjectById(projectId);

        String folderName = project.getId() + project.getName();
        file = fileCompressor.resizeImage(file, maxWidth, maxLength);
        String key = s3Service.uploadFile(file, folderName);

        project.setCoverImageId(key);
        projectRepository.save(project);

        return projectMapper.toDto(project);
    }
}
