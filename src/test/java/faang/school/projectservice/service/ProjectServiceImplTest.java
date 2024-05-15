package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {
    @InjectMocks
    private ProjectServiceImpl projectServiceImpl;
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private UserContext userContext;
    @Captor
    private ArgumentCaptor<Project> captor;

    private ProjectDto firstProjectDto;

    @BeforeEach
    void init() {
        firstProjectDto = new ProjectDto();
        firstProjectDto.setId(1L);
        firstProjectDto.setName("First project");
    }

    @Test
    public void testCreateWithSaving() {
        projectServiceImpl.create(firstProjectDto);
        verify(projectRepository, times(1)).save(captor.capture());

        Project capturedProject = captor.getValue();
        assertEquals(capturedProject.getId(), firstProjectDto.getId());
        assertEquals(capturedProject.getName(), firstProjectDto.getName());
    }

    @Test
    public void testCreateWithSettingStatus() {
        projectServiceImpl.create(firstProjectDto);
        verify(projectValidator, times(1)).validateProjectByOwnerIdAndNameOfProject(firstProjectDto);

        assertEquals(ProjectStatus.CREATED, firstProjectDto.getStatus());
    }

    @Test
    public void testCreateWithSettingOwnerId() {
        when(userContext.getUserId()).thenReturn(1L);
        projectServiceImpl.create(firstProjectDto);
        verify(projectValidator, times(1)).validateProjectByOwnerIdAndNameOfProject(firstProjectDto);

        assertEquals(1L, firstProjectDto.getOwnerId());
    }

    @BeforeEach
    public void reset() {
        Mockito.reset(projectRepository, projectMapper);
    }

    @Test
    public void testCreateWithCreating() {
        Project firstProject = projectMapper.toProject(firstProjectDto);
        when(projectRepository.save(any(Project.class))).thenReturn(firstProject);
        ProjectDto result = projectServiceImpl.create(firstProjectDto);

        assertEquals(result.getId(), firstProject.getId());
        assertEquals(result.getName(), firstProject.getName());
    }

    @Test
    public void testGetAllWithGetting() {
        List<Project> projects = List.of(projectMapper.toProject(firstProjectDto));
        when(projectRepository.findAll()).thenReturn(projects);

        List<ProjectDto> result = projectServiceImpl.getAll();
        assertEquals(result, projectMapper.toDtos(projects));

        InOrder inOrder = inOrder(projectRepository, projectMapper);
        inOrder.verify(projectRepository, times(1)).findAll();
        inOrder.verify(projectMapper, times(1)).toDtos(projects);
    }

    @Test
    public void findByIdWithFinding() {
        Project project = projectMapper.toProject(firstProjectDto);
        when(projectRepository.getProjectById(firstProjectDto.getId())).thenReturn(project);

        ProjectDto result = projectServiceImpl.findById(firstProjectDto.getId());
        assertEquals(result, firstProjectDto);

        InOrder inOrder = inOrder(projectRepository, projectMapper);
        inOrder.verify(projectRepository, times(1)).getProjectById(1L);
        inOrder.verify(projectMapper, times(1)).toDto(project);
    }
}
