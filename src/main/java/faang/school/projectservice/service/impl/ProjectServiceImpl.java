package faang.school.projectservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.filestorage.AwsProperties;
import faang.school.projectservice.dto.ProjectCreateRequestDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectUpdateRequestDto;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.ProjectPresentationDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectViewProfileEvent;
import faang.school.projectservice.dto.resource.S3ObjectDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.SpecificationFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.publisher.ProjectProfileViewPublisher;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.pdf.ProjectPdfService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    public static final String PDF_FILE_NAME = "presentation.pdf";

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<SpecificationFilter> specificationFilters;
    private final ProjectProfileViewPublisher projectProfileViewPublisher;
    private final UserContext userContext;
    private final S3Service s3Service;
    private final AwsProperties s3Properties;
    private final AmazonS3 s3client;
    private final ProjectPdfService projectPdfService;
    private final UserServiceClient userServiceClient;
    private final ProjectValidator projectValidator;

    @Override
    public ProjectResponseDto save(ProjectCreateRequestDto projectDto) {
        validateProject(projectDto);
        Project projectForSaving = projectMapper.toProjectEntity(projectDto);
        projectForSaving.setStatus(ProjectStatus.CREATED);
        Project projectEntity = projectRepository.save(projectForSaving);
        return projectMapper.toProjectResponseDto(projectEntity);
    }

    @Override
    public List<ProjectResponseDto> findAllByFilter(ProjectFilterDto filter) {
        Specification<Project> spec = getProjectSpecification(filter);
        return projectMapper.toProjectResponseDtos(projectRepository.findAll(spec));
    }

    @Override
    public ProjectResponseDto update(Long id, ProjectUpdateRequestDto projectDto) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("There is no project with id:%d in database", id)));
        projectMapper.update(projectDto, project);
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
        return projectMapper.toProjectResponseDto(project);
    }

    @Override
    public ProjectResponseDto findById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("#ProjectServiceImpl: project with id:%d has not been found", projectId)));
        ProjectResponseDto projectResponseDto = projectMapper.toProjectResponseDto(project);
        projectProfileViewPublisher.publish(new ProjectViewProfileEvent(
                projectId, userContext.getUserId(), LocalDateTime.now()));
        return projectResponseDto;
    }

    @Override
    public List<ProjectResponseDto> findAll() {
        List<Project> projects = projectRepository.findAll();
        return projectMapper.toProjectResponseDtos(projects);
    }

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
                PDF_FILE_NAME, System.currentTimeMillis());

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

    @Override
    public List<Long> getProjectResourceIds(Long projectId) {
        Project project = getProject(projectId);
        return project.getResources().stream()
                .map(Resource::getId)
                .sorted()
                .toList();
    }

    @Override
    public Project getProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Not found project with Id = " + projectId));
    }

    private String getPresentationFileKey(long projectId) {
        Project project = getProjectById(projectId);
        return project.getPresentationFileKey();
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        String.format("Project with ID %s not found!", projectId)));
    }

    private long getUserId() {
        return userContext.getUserId();
    }

    private void validateProject(ProjectCreateRequestDto projectDto) {
        if (projectRepository.existsByOwnerIdAndName(projectDto.ownerId(), projectDto.name())) {
            throw new IllegalArgumentException(String.format(
                    "#Validation error: the same user with id:%d cannot create projects with the same name: %s",
                    projectDto.ownerId(), projectDto.name()));
        }
    }

    private Specification<Project> getProjectSpecification(ProjectFilterDto filter) {
        return specificationFilters.stream()
                .filter(spec -> spec.isApplicable(filter))
                .map(spec -> spec.apply(filter))
                .reduce((spec1, spec2) -> spec1.and(spec2))
                .orElse(null);
    }
}
