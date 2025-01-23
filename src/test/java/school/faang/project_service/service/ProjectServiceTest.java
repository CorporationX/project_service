package school.faang.project_service.service;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectRequestDto;
import faang.school.projectservice.dto.ProjectResponseDto;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapperImpl projectMapper;
    @InjectMocks
    private ProjectServiceImpl projectService;
    @Captor
    private ArgumentCaptor<Project> projectCaptor;
    private ProjectRequestDto projectRequest;

    @BeforeEach
    void init() {
        projectRequest = ProjectRequestDto.builder()
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

        ProjectRequestDto projectRequestDto = ProjectRequestDto.builder()
                .ownerId(ownerId)
                .name(name)
                .build();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> projectService.save(projectRequestDto));
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
    public void testFindAllByFilterIfIncorrectStatusFailed() {
        String incorrectStatus = "extra";
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .statusFilter(incorrectStatus)
                .build();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> projectService.findAllByFilter(filterDto));
    }

    @Test
    public void testFindAllByFilterSuccess() {
        ProjectFilterDto filterDto = ProjectFilterDto.builder()
                .nameFilter("super")
                .statusFilter("create")
                .build();

        projectService.findAllByFilter(filterDto);

        verify(projectRepository, times(1))
                .findAllByNameAndStatus(filterDto.nameFilter(), ProjectStatus.CREATED.name());
    }

    @Test
    public void testUpdateSuccess() {
        Project responseEntity = Project.builder()
                .id(1L)
                .build();
        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(responseEntity));

        projectService.update(1L, projectRequest);
        Mockito.verify(projectRepository, times(1)).save(projectCaptor.capture());

        Project project = projectCaptor.getValue();
        Assertions.assertEquals(project.getDescription(), projectRequest.description());
        Assertions.assertNotNull(project.getUpdatedAt());
    }
}
