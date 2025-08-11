package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.dto.stage.StageViewDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.enums.DeleteStrategy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.stage.deletion.StageDeletionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceImplTest {

    @Mock
    private StageRepository stageRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StageMapper stageMapper;

    @Mock
    private Map<DeleteStrategy, StageDeletionStrategy> strategies;

    @InjectMocks
    private StageServiceImpl stageService;

    private final long PROJECT_ID = 1L;
    private final long STAGE_ID = 2L;
    private Project project;
    private Stage stage;

    @BeforeEach
    void setUp() {
        project = Project.builder().id(PROJECT_ID).build();
        stage = Stage.builder()
                .stageId(STAGE_ID)
                .stageName("Development")
                .project(project)
                .build();
    }

    @Test
    void create_WhenProjectNotExist_ShouldThrowDataValidationException() {
        StageCreateDto createDto = StageCreateDto.builder()
                .projectId(PROJECT_ID)
                .stageName("Valid Name")
                .build();

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> stageService.create(createDto));

        assertEquals("Проект с id" + PROJECT_ID + "не найден", exception.getMessage());
    }

    @Test
    void create_WhenStageNameIsBlank_ShouldThrowException() {
        StageCreateDto createDto = StageCreateDto.builder()
                .projectId(PROJECT_ID)
                .stageName(" ")
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> stageService.create(createDto));

        assertEquals("Имя этапа не может быть пустым", exception.getMessage());
    }

    @Test
    void getById_WhenStageNotExist_ShouldThrowException() {
        when(stageRepository.findById(STAGE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> stageService.getById(STAGE_ID));

        assertEquals("Такого этапа не существует", exception.getMessage());
    }

    @Test
    void update_WhenStageNotExist_ShouldThrowException() {
        StageUpdateDto updateDto = StageUpdateDto.builder().build();

        when(stageRepository.findById(STAGE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> stageService.update(updateDto, STAGE_ID));

        assertEquals("Этап" + STAGE_ID + "не найден", exception.getMessage());
    }

    @Test
    void delete_ShouldUseCorrectStrategy() {
        StageDeletionStrategy strategy = mock(StageDeletionStrategy.class);
        when(strategies.get(DeleteStrategy.CASCADE)).thenReturn(strategy);
        when(stageRepository.findById(STAGE_ID)).thenReturn(Optional.of(stage));

        stageService.delete(STAGE_ID, DeleteStrategy.CASCADE);

        verify(strategy).delete(stage);
    }

    @Test
    void getAllStages_WhenProjectNotExist_ShouldThrowException() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> stageService.getAllStages(PROJECT_ID));

        assertEquals("Такого проекта нет", exception.getMessage());
    }
}