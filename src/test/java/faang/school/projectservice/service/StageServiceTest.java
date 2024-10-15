package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.StageDto;
import faang.school.projectservice.model.dto.StageFilterDto;
import faang.school.projectservice.filter.StageFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRoleMapper;
import faang.school.projectservice.model.entity.Task;
import faang.school.projectservice.model.entity.Stage;
import faang.school.projectservice.model.enums.TasksAfterDelete;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.impl.ProjectServiceImpl;
import faang.school.projectservice.service.impl.StageRolesServiceImpl;
import faang.school.projectservice.service.impl.StageServiceImpl;
import faang.school.projectservice.service.impl.TeamMemberServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    @InjectMocks
    private StageServiceImpl stageService;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private StageMapper stageMapper;

    @Mock
    private ProjectServiceImpl projectService;

    @Mock
    private TeamMemberServiceImpl teamMemberService;

    @Mock
    private StageRoleMapper stageRoleMapper;

    @Mock
    private StageRolesServiceImpl stageRolesService;

    @Mock
    private List<StageFilter> filters;

    @Test
    void getFilteredStages_ShouldReturnFilteredStages() {
        StageFilterDto filterDto = new StageFilterDto();
        Stage stage = new Stage();
        when(stageRepository.findAll()).thenReturn(List.of(stage));
        when(filters.stream()).thenReturn(Stream.of());

        List<StageDto> result = stageService.getFilteredStages(filterDto);

        assertNotNull(result);
        verify(stageRepository).findAll();
    }

    @Test
    void deleteStage_ShouldCascadeDeleteTasks() {
        Stage stage = new Stage();
        List<Task> tasks = List.of(new Task());
        stage.setTasks(tasks);
        when(stageRepository.getById(anyLong())).thenReturn(stage);

        stageService.deleteStage(1L, TasksAfterDelete.CASCADE_DELETE, null);

        verify(taskRepository).deleteAll(tasks);
        verify(stageRepository).delete(stage);
    }

    @Test
    void deleteStage_ShouldTransferTasksToAnotherStage() {
        Stage deletedStage = new Stage();
        Stage receivingStage = new Stage();
        List<Task> tasks = List.of(new Task());
        deletedStage.setTasks(tasks);
        when(stageRepository.getById(1L)).thenReturn(deletedStage);
        when(stageRepository.getById(2L)).thenReturn(receivingStage);

        stageService.deleteStage(1L, TasksAfterDelete.TRANSFER_TO_ANOTHER_STAGE, 2L);

        assertEquals(tasks, receivingStage.getTasks());
        verify(stageRepository).delete(deletedStage);
    }

    @Test
    void getAllStages_ShouldReturnAllStages() {
        Stage stage = new Stage();
        when(stageRepository.findAll()).thenReturn(List.of(stage));

        List<StageDto> result = stageService.getAllStages();

        assertNotNull(result);
        verify(stageRepository).findAll();
    }

    @Test
    void getStage_ShouldReturnStageDto() {
        Long stageId = 1L;
        Stage stage = new Stage();
        stage.setStageId(stageId);
        StageDto stageDto = new StageDto();
        stageDto.setStageId(stageId);

        when(stageRepository.getById(stageId)).thenReturn(stage);
        when(stageMapper.toDto(stage)).thenReturn(stageDto);

        StageDto result = stageService.getStage(stageId);

        assertNotNull(result);
        assertEquals(stageDto, result);
        verify(stageRepository).getById(stageId);
        verify(stageMapper).toDto(stage);
    }
}