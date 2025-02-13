package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.s3.S3Properties;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.ProjectDtoResponse;
import faang.school.projectservice.dto.project.ProjectPresentationDto;
import faang.school.projectservice.dto.resource.S3ObjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.pdf.ProjectPdfService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    public static final String PDF_FILE_NAME = "presentation.pdf";
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final S3Service s3Service;
    private final S3Properties s3Properties;
    private final AmazonS3 s3client;
    private final ProjectPdfService projectPdfService;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final ProjectValidator projectValidator;

    @Override
    public ProjectDtoResponse creatingPresentation(long projectId) {

        Project project = getProjectById(projectId);
        final long userId = getUserId();

        projectValidator.validateUserId(userId);
        projectValidator.validateUserIsOwner(userId, project);

        UserDto owner = userServiceClient.getUser(userId);
        ProjectPresentationDto presentationDto = projectMapper.toProjectPresentationDto(project, owner);

        InputStream pdfInputStream = projectPdfService.createProjectPresentation(presentationDto);
        final String presentationFileKey = String.format("%s_%s_%s_%d", project.getName(), project.getId(),
                PDF_FILE_NAME,  System.currentTimeMillis());

        s3Service.putFileInStore(presentationFileKey, pdfInputStream);

        project.setPresentationFileKey(presentationFileKey);
        project.setPresentationGeneratedAt(LocalDateTime.now());
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    public String getPresentationFileKey(long projectId) {
        Project project = getProjectById(projectId);
        return project.getPresentationFileKey();
    }

    @Override
    public S3ObjectDto downloadPdf(Long projectId) {

        String presentationFileKey = getPresentationFileKey(projectId);
        S3Object object = s3client.getObject(s3Properties.getBucketName(), presentationFileKey);

        return new S3ObjectDto(PDF_FILE_NAME, object, ResourceType.PDF.name());
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Project with ID %s not found!", projectId)));
    }

    private long getUserId() {

        final long userId = userContext.getUserId();
        projectValidator.validateUserId(userId);
        return userId;
    }
}
