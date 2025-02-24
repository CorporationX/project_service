package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.s3.S3Properties;
import faang.school.projectservice.dto.client.UserDto;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
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

    @Transactional
    @Override
    public void createPresentation(long projectId) {

        Project project = getProjectById(projectId);
        final long userId = getUserId();

        projectValidator.validateUserIsOwner(userId, project);
        UserDto owner = userServiceClient.getUser(userId);

        ProjectPresentationDto presentationDto = projectMapper.toProjectPresentationDto(project, owner);

        InputStream pdfInputStream = projectPdfService.createProjectPresentation(presentationDto);
        final String presentationFileKey = String.format("%s_%s_%s_%d", project.getName(), project.getId(),
                PDF_FILE_NAME,  System.currentTimeMillis());

        project.setPresentationFileKey(presentationFileKey);
        project.setPresentationGeneratedAt(LocalDateTime.now());
        projectRepository.save(project);

        s3Service.putFileInStore(presentationFileKey, pdfInputStream);
    }

    @Override
    public S3ObjectDto downloadPdf(Long projectId) {

        String presentationFileKey = getPresentationFileKey(projectId);
        S3Object object = s3client.getObject(s3Properties.getBucketName(), presentationFileKey);

        return new S3ObjectDto(PDF_FILE_NAME, object, ResourceType.PDF.name());
    }

    @Override
    public InputStreamResource getPresentation(S3ObjectDto obj) {
        S3ObjectInputStream objectContent = obj.s3Object().getObjectContent();
        return new InputStreamResource(objectContent);
    }

    private String getPresentationFileKey(long projectId) {
        Project project = getProjectById(projectId);
        return project.getPresentationFileKey();
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Project with ID %s not found!", projectId)));
    }

    private long getUserId() {
        return userContext.getUserId();
    }
}
