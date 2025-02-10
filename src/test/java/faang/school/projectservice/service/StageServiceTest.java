package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.*;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.*;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.validator.StageValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    @Mock
    private StageRepository stageRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private StageValidator stageValidator;
    @Mock StageService mockStageService;
    @Mock
    private StageMapper stageMapper;
    @Mock
    private List<StageFilter> stageFilters;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private StageRolesRepository stageRolesRepository;

    @InjectMocks
    private StageService stageService;

    @Test
    void testCreateStage() {
        StageDto inputDto = new StageDto();
        Stage entity = new Stage();
        StageDto expectedDto = new StageDto();

        when(stageMapper.toEntity(any(), any(), any(), any(), any())).thenReturn(entity);
        when(stageRepository.save(entity)).thenReturn(entity);
        when(stageMapper.toDto(any(), any(), any(), any())).thenReturn(expectedDto);

        StageDto result = stageService.createStage(inputDto);

        verify(stageValidator).validateStageCreation(inputDto);
        verify(stageRepository).save(entity);
        assertEquals(expectedDto, result);
    }

    @Test
    void testGetStages() {
        Long projectId = 1L;
        Project project = new Project();
        Stage stage = new Stage();
        StageDto stageDto = new StageDto();
        project.setStages(List.of(stage));

        when(stageValidator.getValidProject(projectId)).thenReturn(project);
        when(stageMapper.toDto(any(), any(), any(), any())).thenReturn(stageDto);

        List<StageDto> result = stageService.getStages(projectId);

        assertEquals(1, result.size());
        assertEquals(stageDto, result.get(0));
    }

    @Test
    void testDeleteStageSuccess() throws InterruptedException {
        Long stageId = 1L;
        StageDeleteDto deleteDto = new StageDeleteDto();
        deleteDto.setId(stageId);
        deleteDto.setId(1L);
        deleteDto.setProjectId(2L);

        Stage stage = new Stage();
        stage.setTasks(new ArrayList<>());
        stage.setStageRoles(new ArrayList<>());
        stage.setExecutors(new ArrayList<>());

        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));

        verify(stageValidator).checkStageToRemove(stageId, deleteDto);
        mockStageService.deleteStage(stageId, deleteDto);
        verify(mockStageService, Mockito.times(1)).deleteStage(stageId, deleteDto);
    }

    @Test
    void testGetActiveStages() {
        Long projectId = 1L;
        StageFilterDto filterDto = new StageFilterDto();
        Project project = new Project();
        Stage stage = new Stage();
        StageDto stageDto = new StageDto();

        when(projectRepository.getReferenceById(projectId)).thenReturn(project);
        when(stageMapper.toDto(any(), any(), any(), any())).thenReturn(stageDto);

        List<StageDto> result = stageService.getActiveStages(projectId, filterDto);

    }
    @Test
    void testDeleteStage_WithTasksAndRoles() throws InterruptedException {
        Long stageId = 1L;
        StageDeleteDto deleteDto = new StageDeleteDto();

        Stage stage = new Stage();
        stage.setTasks(List.of(new Task(), new Task()));
        stage.setStageRoles(List.of(new StageRoles()));
        stage.setExecutors(List.of(new TeamMember()));

        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));

        stageService.deleteStage(stageId, deleteDto);

        verify(taskRepository).deleteAllById(anyList());
        verify(stageRolesRepository).deleteAllById(anyList());
        verify(teamMemberRepository).deleteAllById(anyList());
        verify(stageRepository).delete(stage);
    }

    @Test
    void testUpdateStage() {
        Long stageId = 1L;
        StageUpdateDto updateDto = new StageUpdateDto();
        StageDto stageDto = new StageDto();
        Stage entity = new Stage();

        when(stageMapper.toStageDto(updateDto)).thenReturn(stageDto);
        when(stageMapper.toEntity(any(), any(), any(), any(), any())).thenReturn(entity);
        when(stageMapper.toStageUpdateDto(stageDto)).thenReturn(updateDto);

        StageUpdateDto result = stageService.updateStage(stageId, updateDto);

        verify(stageValidator).checkStageForUpdate(stageId, updateDto);
        verify(stageRepository).save(entity);
        assertEquals(updateDto, result);
    }

}