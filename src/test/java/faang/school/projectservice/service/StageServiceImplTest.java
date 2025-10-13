package faang.school.projectservice.service;


import faang.school.projectservice.dto.client.CreateStageDto;
import faang.school.projectservice.dto.client.ProjectIdDto;
import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.dto.client.StageIdDto;
import faang.school.projectservice.dto.client.StageRoleDto;
import faang.school.projectservice.dto.client.UpdateStageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceImplTest {

    @Mock
    private StageRepository stageRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StageMapper stageMapper;

    @InjectMocks
    private StageServiceImpl stageService;

    private Project project;
    private Stage stage;
    private Stage savedStage;
    private StageDto stageDto;
    private CreateStageDto createStageDto;

    @BeforeEach
    void setUp() {
        Long projectId = 1L;
        project = new Project();
        project.setId(projectId);

        StageRoleDto roleDto = new StageRoleDto(TeamRole.DEVELOPER, 2);

        createStageDto = new CreateStageDto("Development", projectId, List.of(roleDto), List.of(100L));

        stage = new Stage();
        stage.setStageName("Development");
        stage.setProject(project);

        savedStage = new Stage();
        savedStage.setStageId(10L);
        savedStage.setStageName("Development");
        savedStage.setProject(project);

        stageDto = new StageDto(
                savedStage.getStageId(),
                savedStage.getStageName(),
                projectId,
                List.of(roleDto),
                List.of(),
                0
        );
    }

    @Test
    void createStage_success() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(stageMapper.toStage(createStageDto)).thenReturn(stage);
        when(stageRepository.save(any(Stage.class))).thenReturn(savedStage);
        when(stageMapper.toStageDto(savedStage)).thenReturn(stageDto);

        StageDto result = stageService.createStage(createStageDto);

        assertNotNull(result);
        assertEquals(stageDto.stageId(), result.stageId());
        assertEquals(stageDto.stageName(), result.stageName());
        assertEquals(stageDto.projectId(), result.projectId());

        verify(projectRepository).findById(project.getId());
        verify(stageMapper).toStage(createStageDto);
        verify(stageRepository).save(any(Stage.class));
        verify(stageMapper).toStageDto(savedStage);
    }

    @Test
    void createStage_projectNotFound_throwsException() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> stageService.createStage(createStageDto)
        );

        assertTrue(ex.getMessage().contains("Project with id " + project.getId() + " is not found"));
        verify(stageRepository, never()).save(any());
    }

    @Test
    void createStage_invalidStageName_throwsDataValidationException() {
        CreateStageDto invalidDto = new CreateStageDto("   ", project.getId(),
                List.of(new StageRoleDto(TeamRole.TESTER, 1)), List.of());

        assertThrows(DataValidationException.class, () -> stageService.createStage(invalidDto));

        verifyNoInteractions(projectRepository, stageMapper, stageRepository);
    }

    @Test
    void createStage_emptyRoles_throwsDataValidationException() {
        CreateStageDto invalidDto = new CreateStageDto("Stage 1", project.getId(), List.of(), List.of());

        assertThrows(DataValidationException.class, () -> stageService.createStage(invalidDto));

        verifyNoInteractions(projectRepository, stageMapper, stageRepository);
    }

    @Test
    void getAllStagesOfProject_success() {
        ProjectIdDto projectIdDto = new ProjectIdDto(project.getId());
        Stage stage1 = new Stage();
        stage1.setStageId(1L);
        stage1.setProject(project);
        stage1.setStageName("Planning");

        Stage stage2 = new Stage();
        stage2.setStageId(2L);
        stage2.setProject(project);
        stage2.setStageName("Execution");

        StageDto dto1 = new StageDto(1L, "Planning", project.getId(),
                List.of(), List.of(), 0);
        StageDto dto2 = new StageDto(2L, "Execution", project.getId(),
                List.of(), List.of(), 0);

        when(stageRepository.findAllByProjectId(project.getId())).thenReturn(List.of(stage1, stage2));
        when(stageMapper.toStageDto(stage1)).thenReturn(dto1);
        when(stageMapper.toStageDto(stage2)).thenReturn(dto2);

        List<StageDto> result = stageService.getAllStagesOfProject(projectIdDto);

        assertEquals(2, result.size());
        assertEquals("Planning", result.get(0).stageName());
        assertEquals("Execution", result.get(1).stageName());
        verify(stageRepository).findAllByProjectId(project.getId());
    }

    @Test
    void deleteById_success() {
        StageIdDto stageIdDto = new StageIdDto(stage.getStageId());
        when(stageRepository.findById(stage.getStageId())).thenReturn(Optional.of(stage));

        stageService.deleteById(stageIdDto);

        verify(stageRepository).delete(stage);
    }

    @Test
    void deleteById_notFound_throwsException() {
        StageIdDto stageIdDto = new StageIdDto(999L);
        when(stageRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> stageService.deleteById(stageIdDto));
        verify(stageRepository, never()).delete(any());
    }

    @Test
    void updateStage_success() {
        UpdateStageDto updateDto = new UpdateStageDto(savedStage.getStageId(),
                "Updated Name", List.of(), List.of());
        when(stageRepository.findById(updateDto.stageId())).thenReturn(Optional.of(stage));
        when(stageRepository.save(stage)).thenReturn(savedStage);
        when(stageMapper.toStageDto(savedStage)).thenReturn(stageDto);

        StageDto result = stageService.updateStage(updateDto);

        assertNotNull(result);
        assertEquals(stageDto.stageId(), result.stageId());
        verify(stageRepository).save(stage);
    }

    @Test
    void updateStage_notFound_throwsException() {
        UpdateStageDto updateDto = new UpdateStageDto(999L, "New", List.of(), List.of());
        when(stageRepository.findById(updateDto.stageId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> stageService.updateStage(updateDto));
        verify(stageRepository, never()).save(any());
    }

    @Test
    void getById_success() {
        StageIdDto stageIdDto = new StageIdDto(savedStage.getStageId());
        when(stageRepository.findById(savedStage.getStageId())).thenReturn(Optional.of(savedStage));
        when(stageMapper.toStageDto(savedStage)).thenReturn(stageDto);

        StageDto result = stageService.getById(stageIdDto);

        assertNotNull(result);
        assertEquals(savedStage.getStageId(), result.stageId());
        verify(stageRepository).findById(savedStage.getStageId());
        verify(stageMapper).toStageDto(savedStage);
    }

    @Test
    void getById_notFound_throwsException() {
        StageIdDto stageIdDto = new StageIdDto(404L);
        when(stageRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> stageService.getById(stageIdDto));
        verify(stageMapper, never()).toStageDto(any());
    }
}

