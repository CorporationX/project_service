package faang.school.projectservice.service.project;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.presentation.PresentationRequestDto;
import faang.school.projectservice.dto.presentation.PresentationFilterDto;
import faang.school.projectservice.dto.presentation.PresentationUpdateDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.NameSpecification;
import faang.school.projectservice.filter.SpecificationFilter;
import faang.school.projectservice.filter.StatusSpecification;
import faang.school.projectservice.mapper.PresentationMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.publisher.ProjectProfileViewPublisher;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectServiceImpl;
import faang.school.projectservice.service.presentation.PresentationService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.PresentationValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ProjectServiceTest {
    private ProjectRepository projectRepository;
    private ProjectServiceImpl projectService;
    private ArgumentCaptor<Project> projectCaptor;
    private PresentationRequestDto projectRequest;
    private ProjectProfileViewPublisher projectProfileViewPublisher;

    @BeforeEach
    void init() {

        projectRepository = Mockito.mock(ProjectRepository.class);
        PresentationMapperImpl projectMapper = Mockito.spy(PresentationMapperImpl.class);
        UserContext userContext = Mockito.spy(UserContext.class);
        NameSpecification nameSpecification = Mockito.spy(NameSpecification.class);
        StatusSpecification statusSpecification = Mockito.spy(StatusSpecification.class);
        List<SpecificationFilter> specificationFilters = List.of(nameSpecification, statusSpecification);
        S3Service s3client = Mockito.mock(S3Service.class);
        PresentationService presentationService = Mockito.mock(PresentationService.class);
        UserServiceClient userServiceClient = Mockito.mock(UserServiceClient.class);
        PresentationValidator presentationValidator = Mockito.mock(PresentationValidator.class);

        projectService = new ProjectServiceImpl(
                projectRepository, projectMapper, specificationFilters, projectProfileViewPublisher, userContext,
                s3client, presentationService, userServiceClient, presentationValidator);

        projectCaptor = ArgumentCaptor.forClass(Project.class);

        projectRequest = PresentationRequestDto.builder()
                .name("excellent project")
                .ownerId(1L)
                .description("some description")
                .build();
    }

    @Test
    public void testSaveWhenProjectExistsWithSuchOwnerIdAndNameFailed() {
        Long ownerId = 1L;
        String name = "superProject";

        Mockito.when(projectRepository.existsByOwnerIdAndName(ownerId, name)).thenReturn(true);

        PresentationRequestDto presentationRequestDto = PresentationRequestDto.builder()
                .ownerId(ownerId)
                .name(name)
                .build();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> projectService.save(presentationRequestDto));
    }

    @Test
    public void testSaveSuccess() {
        Mockito.when(projectRepository.existsByOwnerIdAndName(projectRequest.ownerId(), projectRequest.name()))
                .thenReturn(false);
        Mockito.when(projectRepository.save(any())).thenReturn(new Project());

        ProjectResponseDto result = projectService.save(projectRequest);

        Assertions.assertNotNull(result);
        verify(projectRepository, times(1)).save(projectCaptor.capture());

        Project capturedProject = projectCaptor.getValue();
        Assertions.assertEquals(ProjectStatus.CREATED, capturedProject.getStatus());
    }

    @Test
    public void testFindAllByFilterSuccess() {
        PresentationFilterDto filterDto = PresentationFilterDto.builder()
                .name("super")
                .status(ProjectStatus.CREATED)
                .build();

        projectService.findAllByFilter(filterDto);

        verify(projectRepository, times(1))
                .findAll(any(Specification.class));
    }

    @Test
    public void testUpdateWhenProjectIdIsNotExistsFailed() {
        Mockito.when(projectRepository.findById(1L)).thenThrow(new EntityNotFoundException(""));
        PresentationUpdateDto request = PresentationUpdateDto.builder()
                .status(ProjectStatus.CREATED)
                .description("description")
                .build();

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> projectService.update(1L, request));
    }

    @Test
    public void testUpdateSuccess() {
        Project responseEntity = Project.builder()
                .id(1L)
                .build();
        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(responseEntity));
        PresentationUpdateDto projectUpdateRequest = PresentationUpdateDto.builder()
                .description("updated description")
                .status(ProjectStatus.COMPLETED)
                .build();

        projectService.update(1L, projectUpdateRequest);
        Mockito.verify(projectRepository, times(1)).save(projectCaptor.capture());

        Project project = projectCaptor.getValue();
        Assertions.assertEquals(project.getStatus(), projectUpdateRequest.status());
        Assertions.assertEquals(project.getDescription(), projectUpdateRequest.description());
        Assertions.assertNotNull(project.getUpdatedAt());
    }
}
