package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectCreateRequestDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectResponseDto;
import faang.school.projectservice.dto.ProjectUpdateRequestDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.NameSpecification;
import faang.school.projectservice.filter.SpecificationFilter;
import faang.school.projectservice.filter.StatusSpecification;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.impl.ProjectServiceImpl;
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
    private ProjectCreateRequestDto projectRequest;

    @BeforeEach
    void init() {
        projectRepository = Mockito.mock(ProjectRepository.class);
        ProjectMapperImpl projectMapper = Mockito.spy(ProjectMapperImpl.class);
        NameSpecification nameSpecification = Mockito.spy(NameSpecification.class);
        StatusSpecification statusSpecification = Mockito.spy(StatusSpecification.class);
        List<SpecificationFilter> specificationFilters = List.of(nameSpecification, statusSpecification);
        projectService = new ProjectServiceImpl(projectRepository, projectMapper, specificationFilters);

        projectCaptor = ArgumentCaptor.forClass(Project.class);

        projectRequest = ProjectCreateRequestDto.builder()
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

        ProjectCreateRequestDto projectCreateRequestDto = ProjectCreateRequestDto.builder()
                .ownerId(ownerId)
                .name(name)
                .build();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> projectService.save(projectCreateRequestDto));
    }

    @Test
    public void testSaveSuccess() {
        Mockito.when(projectRepository.existsByOwnerIdAndName(projectRequest.ownerId(), projectRequest.name())).thenReturn(false);
        Mockito.when(projectRepository.save(any())).thenReturn(new Project());

        ProjectResponseDto result = projectService.save(projectRequest);

        Assertions.assertNotNull(result);
        verify(projectRepository, times(1)).save(projectCaptor.capture());

        Project capturedProject = projectCaptor.getValue();
        Assertions.assertEquals(ProjectStatus.CREATED, capturedProject.getStatus());
    }

    @Test
    public void testFindAllByFilterSuccess() {
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
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
        ProjectUpdateRequestDto request = ProjectUpdateRequestDto.builder()
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
        ProjectUpdateRequestDto projectUpdateRequest = ProjectUpdateRequestDto.builder()
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
