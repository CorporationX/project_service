package faang.school.projectservice.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.validator.StageValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StageServiceTest {

    @Mock
    private StageValidator stageValidator;

    @Mock
    private StageMapper stageMapper;

    @Mock
    private StageRepository stageRepository;

    @InjectMocks
    private StageService stageService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private List<StageFilter> stageFilters;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StageRolesRepository stageRolesRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateStage() {
        // Arrange
        StageDto stageDto = new StageDto();
        Stage stage = new Stage();
        when(stageMapper.toStage(stageDto)).thenReturn(stage);
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapper.toStageDto(stage)).thenReturn(stageDto);

        StageDto result = stageService.createStage(stageDto);

        assertNotNull(result);
        assertEquals(stageDto, result);
        verify(stageValidator).validateStageCreation(stageDto);
        verify(stageMapper).toStage(stageDto);
        verify(stageRepository).save(stage);
        verify(stageMapper).toStageDto(stage);
    }

    @Test
    public void testGetStages() {
        Long projectId = 1L;
        Project project = new Project();
        Stage stage1 = new Stage();
        Stage stage2 = new Stage();
        project.setStages(Arrays.asList(stage1, stage2));

        StageDto stageDto1 = new StageDto();
        StageDto stageDto2 = new StageDto();

        when(stageValidator.getValidProject(projectId)).thenReturn(project);
        when(stageMapper.toStageDto(stage1)).thenReturn(stageDto1);
        when(stageMapper.toStageDto(stage2)).thenReturn(stageDto2);

        List<StageDto> result = stageService.getStages(projectId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(stageDto1));
        assertTrue(result.contains(stageDto2));
        verify(stageValidator).getValidProject(projectId);
        verify(stageMapper, times(2)).toStageDto(any(Stage.class));
    }
    @Test
    public void testDeleteStage_Success() throws InterruptedException {

        Long stageId = 1L;
        Stage stage = new Stage();
        stage.setStageId(stageId);

        Task task1 = new Task();
        task1.setId(101L);
        Task task2 = new Task();
        task2.setId(102L);
        stage.setTasks(Arrays.asList(task1, task2));

        StageRoles role1 = new StageRoles();
        role1.setId(201L);
        StageRoles role2 = new StageRoles();
        role2.setId(202L);
        stage.setStageRoles(Arrays.asList(role1, role2));

        TeamMember member1 = new TeamMember();
        member1.setId(301L);
        TeamMember member2 = new TeamMember();
        member2.setId(302L);
        stage.setExecutors(Arrays.asList(member1, member2));

        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));

        stageService.deleteStage(stageId);

        verify(stageValidator).checkStageToRemove(stageId);
        verify(stageRepository).findById(stageId);
        verify(taskRepository).deleteAllById(Arrays.asList(101L, 102L));
        verify(stageRolesRepository).deleteAllById(Arrays.asList(201L, 202L));
        verify(teamMemberRepository).deleteAllById(Arrays.asList(301L, 302L));
        verify(stageRepository).delete(stage);
    }

    @Test
    public void testDeleteStage_EntityNotFoundException() {

        Long stageId = 1L;
        when(stageRepository.findById(stageId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> stageService.deleteStage(stageId));
        verify(stageValidator).checkStageToRemove(stageId);
        verify(stageRepository).findById(stageId);
        verifyNoInteractions(taskRepository, stageRolesRepository, teamMemberRepository);
    }
    @Test
    public void testGetActiveStages() {
        // Arrange
        long projectId = 1L;
        StageFilterDto stageFilterDto = new StageFilterDto();
        Project project = mock(Project.class);
        Stage stage = mock(Stage.class);
        StageDto stageDto = mock(StageDto.class);

        when(projectRepository.getReferenceById(projectId)).thenReturn(project);
        when(project.getStages()).thenReturn(Collections.singletonList(stage));
        when(stageFilters.stream()).thenReturn(Collections.singletonList(mock(StageFilter.class)).stream());
        when(stageMapper.toStageDto(stage)).thenReturn(stageDto);

        // Act
        List<StageDto> result = stageService.getActiveStages(projectId, stageFilterDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(stageDto, result.get(0));

        verify(projectRepository, times(1)).getReferenceById(projectId);
        verify(project, times(1)).getStages();
        verify(stageMapper, times(1)).toStageDto(stage);
    }

}
