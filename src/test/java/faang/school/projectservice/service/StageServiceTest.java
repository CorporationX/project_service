package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    @InjectMocks
    private StageService stageService;

    @Mock
    private StageMapper stageMapper;

    @Mock
    private StageRepository stageRepository;
    @Mock
    private ProjectRepository projectRepository;

    private final StageDto stageDto = new StageDto();
    private final Stage stage = new Stage();
    private final Project project = new Project();
    private final ProjectFilterDto projectFilterDto = new ProjectFilterDto();
    private final Stage stage1 = new Stage();
    private final Stage stage2 = new Stage();
    private final Stage stage3 = new Stage();
    private final StageDto stageDto1 = new StageDto();
    private final StageDto stageDto2 = new StageDto();
    private final StageDto stageDto3 = new StageDto();
    private final List<StageDto> stageDtos = List.of(stageDto1, stageDto2, stageDto3);

    @BeforeEach
    void init() {

    }

    @Test
    void shouldReturnDataValidationExceptionWhenCreateStage() {

        project.setStatus(ProjectStatus.CANCELLED);
        stageDto.setProject(project);

        assertThrows(DataValidationException.class,
                () -> stageService.createStage(stageDto));
    }

    @Test
    void shouldReturnStageDtoWenCreateStage() {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        stageDto.setProject(project);
        stage.setProject(project);

        when(stageMapper.stageDtoToEntity(stageDto))
                .thenReturn(stage);

        when(stageMapper.stageToDto(stage))
                .thenReturn(stageDto);

        when(stageRepository.save(stage))
                .thenReturn(stage);

        stageService.createStage(stageDto);

        verify(stageRepository, times(1)).save(stage);

        assertEquals(stageDto, stageService.createStage(stageDto));

    }

    @Test
    void shouldReturnDataValidationExceptionWhenGetAllStagesFilteredByProjectStatusTest() {
        when(projectRepository.getProjectById(anyLong()))
                .thenThrow(new DataValidationException("Такого проекта не существует!"));

        assertThrows(DataValidationException.class,
                () -> stageService.getAllStagesFilteredByProjectStatus(1L,
                        new ProjectFilterDto()));
    }

    @Test
    void shouldRtnExcWhenGetAllStagesFilteredByProjectStatusWithWrongProjectStatusTest() {
        project.setStatus(ProjectStatus.CANCELLED);
        project.setId(1L);
        projectFilterDto.setProjectStatus(ProjectStatus.IN_PROGRESS.toString());

        assertThrows(DataValidationException.class,
                () -> stageService.getAllStagesFilteredByProjectStatus(project.getId(),
                        projectFilterDto));
    }

    @Test
    void shouldReturnStageDtosWhenGetAllStagesFilteredByProjectStatusTest() {
        project.setId(1L);
        project.setStatus(ProjectStatus.CANCELLED);
        project.setStages(List.of(stage1, stage2, stage3));

        projectFilterDto.setProjectStatus(ProjectStatus.CANCELLED.toString());

        when(projectRepository.getProjectById(project.getId())).thenReturn(project);

        when(stageMapper.stageToDto(stage1)).thenReturn(stageDto1);

        when(stageMapper.stageToDto(stage2)).thenReturn(stageDto2);

        when(stageMapper.stageToDto(stage3)).thenReturn(stageDto3);

        assertIterableEquals(stageDtos, stageService
                .getAllStagesFilteredByProjectStatus(project.getId(), projectFilterDto));
    }

    @Test
    void shouldReturnEntityNotFoundExceptionWhenDeleteStageTest() {
        when(stageRepository.getById(anyLong()))
                .thenThrow(new EntityNotFoundException());
        assertThrows(EntityNotFoundException.class,
                () -> stageService.deleteStage(anyLong()));
    }

    @Test
    void shouldVerifyDeletingWhenDeleteStageTest() {
        when(stageRepository.getById(anyLong()))
                .thenReturn(stage);
        stageService.deleteStage(anyLong());
        verify(stageRepository, times(1)).delete(stage);
    }

    @Test
    void updateStage() {
    }

    @Test
    void shouldReturnEntityNotFoundExceptionWhenGetStagesOfProjectTest() {
        when(projectRepository.getProjectById(anyLong()))
                .thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class,
                () -> stageService.getStagesOfProject(anyLong()));
    }

    @Test
    void shouldReturnStageDtosWhenGetStagesOfProjectTest() {
        project.setId(1L);
        project.setStages(List.of(stage1, stage2, stage3));
        stage1.setProject(project);
        stage2.setProject(project);
        stage3.setProject(project);

        when(projectRepository.getProjectById(anyLong())).thenReturn(project);

        when(stageRepository.findAll()).thenReturn(project.getStages());

        when(stageMapper.stageToDto(stage1)).thenReturn(stageDto1);
        when(stageMapper.stageToDto(stage2)).thenReturn(stageDto2);
        when(stageMapper.stageToDto(stage3)).thenReturn(stageDto3);

        stageService.getStagesOfProject(project.getId());

        verify(projectRepository, times(1))
                .getProjectById(project.getId());

        verify(stageRepository, times(1)).findAll();

        assertIterableEquals(stageDtos, stageService.getStagesOfProject(project.getId()));
    }

    @Test
    void shouldReturnEntityNotFoundExceptionWhenGetStageByIdTest() {
        when(stageRepository.getById(anyLong())).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class,
                () -> stageService.getStageById(anyLong()));
    }

    @Test
    void shouldReturnStageDtoWhenGetStageByIdTest() {
        stage.setStageId(1L);
        when(stageRepository.getById(anyLong())).thenReturn(stage);

        when(stageMapper.stageToDto(stage)).thenReturn(stageDto);

        stageService.getStageById(stage.getStageId());

        verify(stageRepository, times(1)).getById(anyLong());

        assertEquals(stageDto, stageService.getStageById(stage.getStageId()));
    }

    @Test
    void shouldReturnTrueWhenValidateProjectStatusTest() {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        projectFilterDto.setProjectStatus(ProjectStatus.IN_PROGRESS.toString());

        assertTrue(stageService.validateProjectStatus(project, projectFilterDto));
    }

    @Test
    void shouldReturnFalseWhenValidateProjectStatusTest() {
        project.setStatus(ProjectStatus.CANCELLED);
        projectFilterDto.setProjectStatus(ProjectStatus.IN_PROGRESS.toString());

        assertFalse(stageService.validateProjectStatus(project, projectFilterDto));
    }
}